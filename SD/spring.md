# Spring Boot — Senior Engineer Deep Dive & Interview Guide (Expanded)

> Written so you can *explain* each concept out loud in an interview, not just recognize it. Every section leads with the mechanism — what actually happens, in what order, and why it was built that way — before getting to code and interview framing.

---

## 1. The IoC Container & Bean Lifecycle

### 1.1 What problem the container is even solving

Before getting into mechanics: the IoC container exists to invert who's responsible for constructing and wiring objects. In plain Java, `OrderService` would call `new PaymentGateway()` itself, which means `OrderService` is coupled to a concrete `PaymentGateway` implementation and to *how* it's constructed (config, credentials, retries). Spring's container takes over construction: you declare *what* you need (a constructor parameter of type `PaymentGateway`), and the container decides *which* concrete instance to hand you, when to build it, and in what order relative to everything else. That's the entire value proposition — the "magic" is just a very literal graph-resolution algorithm running once at startup.

### 1.2 What `@SpringBootApplication` actually expands to

This annotation is not itself magic — it's a bundle of three separate annotations, and you should be able to name what each one does independently, because interviewers sometimes ask you to use them separately (e.g., "why would you ever need `@ComponentScan` with custom `basePackages`?"):

```
@SpringBootApplication
= @Configuration            // this class itself may declare @Bean-annotated factory methods
+ @EnableAutoConfiguration  // triggers the conditional autoconfiguration machinery (full detail in §2)
+ @ComponentScan            // scans the current package and all sub-packages for
                             //   @Component / @Service / @Repository / @Controller / @RestController
                             //   and registers them as candidate beans
```

The practical implication of `@ComponentScan`'s default behavior: if you put your `@SpringBootApplication` class in `com.gs.orders`, and you have a utility bean in `com.gs.shared.audit`, it will **not** be picked up unless that package is a sub-package of `com.gs.orders`, or you explicitly widen the scan with `@ComponentScan(basePackages = {"com.gs.orders", "com.gs.shared"})`. This is a genuinely common real-world bug in multi-module Maven/Gradle projects — a shared library's beans silently don't load — and being able to explain *why* (package scanning root, not classpath presence) is a good signal.

### 1.3 The startup sequence, step by step, with the "why" at each step

This is worth memorizing as a narrative, not a list, because interviewers often ask you to "walk me through" it rather than name the steps in isolation.

**Step 1 — Context creation.** `SpringApplication.run()` picks an `ApplicationContext` implementation based on what's on the classpath — for a web app with Spring MVC it's `AnnotationConfigServletWebServerApplicationContext`. This object is, at this point, essentially empty — it's a registry that doesn't yet contain any of your beans.

**Step 2 — Environment preparation.** Before a single bean is created, Spring resolves the `Environment`: it merges property sources in a defined precedence order (command-line args highest, then OS environment variables, then `application-{profile}.yml`, then `application.yml`, then defaults baked into code). This matters because a very common senior-level debugging question is "my property isn't taking effect even though it's in `application.yml`" — the answer is almost always that something higher in this precedence chain (an env var, a `-D` system property, a `--spring.config.additional-location`) is overriding it silently.

**Step 3 — BeanFactoryPostProcessor phase.** This is where `ConfigurationClassPostProcessor` runs. It reads every `@Configuration` class, every `@ComponentScan` result, every `@Import`, and every autoconfiguration class, and from all of that it builds `BeanDefinition` objects — **metadata only**. A `BeanDefinition` says "there should eventually be a bean named `orderService`, of type `OrderService`, constructed via this constructor, with these dependencies, scoped as a singleton" — but no `OrderService` object exists yet. This distinction (definition vs instance) is the key to understanding a lot of Spring's flexibility: `BeanFactoryPostProcessor`s can still rewrite definitions (e.g., `PropertySourcesPlaceholderConfigurer` resolves `${...}` placeholders in bean definitions) before anything is actually instantiated.

**Step 4 — Singleton pre-instantiation.** Now the container walks every non-lazy singleton `BeanDefinition` and, for each one, does this inner sequence — and *this* inner sequence is the part interviewers most want you to narrate precisely:

```
a. Resolve the constructor Spring should use, and resolve each constructor
   argument by looking up (or recursively creating) the matching bean.
   -> this is why constructor-injected circular dependencies fail fast:
      resolving argument B requires B to already exist, but B's constructor
      needs A, which is what we're in the middle of building.

b. Instantiate the raw object via that constructor. At this instant the
   object exists in memory but none of its @Autowired fields/setters have
   run yet, and no @PostConstruct has run.

c. Populate remaining dependencies — field and setter injection, if used.

d. Call postProcessBeforeInitialization() on every registered BeanPostProcessor.
   THIS is the step where AOP proxies get created. AbstractAutoProxyCreator
   (which backs @Transactional, @Async, @Cacheable, and any custom @Aspect)
   is itself just a BeanPostProcessor. It inspects the raw bean, and if it
   matches a pointcut, it does NOT modify the raw object — it creates an
   entirely separate proxy object that WRAPS the raw object, and it is the
   PROXY that gets registered as the bean going forward, not the original.

e. Call afterPropertiesSet() (if the bean implements InitializingBean) and
   any @PostConstruct method.

f. Call postProcessAfterInitialization() on every BeanPostProcessor.

g. The resulting object (proxy, if one was created in step d) is cached in
   the singleton pool and is what every future getBean() / @Autowired call
   will receive.
```

**Step 5 — Embedded server starts.** Deliberately *last*. Tomcat/Netty only starts accepting connections once the entire application context has finished refreshing. This is why a bean that throws during construction prevents the app from ever starting to serve traffic at all — there's no partially-up state.

**Step 6 — `ApplicationReadyEvent` fires.** Anything listening for this (health checks, warmup logic) runs now.

### 1.4 Why self-invocation breaks `@Transactional` / `@Async` / `@Cacheable` — explained mechanically, not just stated

Given step 4d above, you can now derive this rather than memorize it. The proxy is a *separate object* sitting in front of your real object. When Spring hands out `orderService` anywhere else in the app (another class's constructor injection, etc.), it hands out the **proxy**. The proxy's job, on every method call, is to check "does this method have `@Transactional`/`@Cacheable`/etc.? If so, run the cross-cutting logic (open a transaction, check the cache) and then delegate to the real object."

But inside `OrderService` itself, `this` refers to the **raw, unwrapped object** — because that's literally the object executing the code; it never goes back through the proxy. So:

```java
@Service
public class OrderService {
    public void placeOrder(Order o) {
        this.charge(o);   // this = the raw object. No proxy involved. Annotation on
                           // charge() is completely invisible from here.
    }
    @Transactional
    public void charge(Order o) { ... }
}
```

`this.charge(o)` never passes through the proxy, so the transactional advice never runs — no error, no warning, it just silently doesn't happen. This is one of the highest-value things you can explain clearly in an interview, because most candidates know the *symptom* ("self-invocation doesn't work") but can't explain the *mechanism* (proxy-vs-target object identity), and being able to derive it from the bean lifecycle is exactly the senior signal interviewers are listening for.

**The three standard fixes**, and when you'd pick each:
1. **Inject a self-reference** (`@Autowired private OrderService self;` or via `ApplicationContext.getBean()`), and call `self.charge(o)`. Works, but feels awkward and some teams disallow it as a code-smell.
2. **Split into two beans** — move `charge()` into a separate `PaymentService` bean, and have `OrderService` depend on it. Usually the cleanest fix, and often improves the design anyway (single responsibility).
3. **Switch to AspectJ compile-time/load-time weaving** instead of proxy-based AOP — the advice gets woven directly into the bytecode, so there's no proxy object and self-invocation works correctly. Rarely used in practice because of build complexity, but knowing it exists (and *why* it solves the problem — no proxy indirection at all) shows depth.

### 1.5 JDK dynamic proxy vs CGLIB — the mechanism, not just the table

**JDK dynamic proxy** (`java.lang.reflect.Proxy`) can only proxy an **interface**. At runtime, the JVM generates a new class that implements the same interface(s) as your bean, and every method call on that generated class is routed through an `InvocationHandler` you (or Spring) provide, which decides what to do (run advice, then call the real object). Because it works purely at the interface level, it fundamentally cannot proxy a class that has no interface, or intercept a method that isn't declared on the interface.

**CGLIB** works differently: it generates an actual **subclass** of your concrete bean class at runtime (via bytecode manipulation, using ASM under the hood), and overrides your methods in that subclass to insert the advice before calling `super.method()`. Because it subclasses your real class, it doesn't require an interface at all.

Spring Boot 2.x onward defaults `spring.aop.proxy-target-class=true`, meaning **CGLIB is used even for beans that implement an interface** (this was a change from earlier Spring versions, and worth mentioning if asked "has this ever changed" — it shows you track framework evolution, which ties nicely into the virtual-threads point in §13).

**The gotcha that follows directly from "CGLIB subclasses your class"**: subclassing cannot override `final` methods or `final` classes — Java doesn't allow it. So if you mark a `@Transactional` method (or its class) `final`, CGLIB silently produces a proxy that just calls straight through to your original method with **no advice applied** — no exception, no warning, the transaction simply never opens. This is a real bug class, not a theoretical one; if you've ever seen "why isn't my transaction rolling back" and the method turned out to be `final`, that's a strong personal story to have ready.

### 1.6 Bean scopes beyond singleton/prototype — and the injection gotcha

- **`singleton`** (default): exactly one instance per container, created once (per §1.3 step 4) and reused for the life of the application.
- **`prototype`**: a *new* instance is created every time `getBean()` is called. The gotcha: if you `@Autowired` a prototype-scoped bean as a field into a singleton, that field is only resolved **once**, at the singleton's own construction time (step 4c above) — you get one prototype instance forever, not a fresh one per use, because field injection just does a single `getBean()` call and caches the result on the singleton field. To actually get a fresh prototype instance on every logical use, you need `ObjectProvider<T>` (call `.getObject()` each time you need a fresh one), `@Lookup` method injection, or a scoped proxy.
- **`request` / `session`**: web-aware scopes tied to the HTTP request/session lifecycle. Injecting one into a singleton requires `proxyMode = ScopedProxyMode.TARGET_CLASS`, because the singleton is fully constructed at application startup — long before any HTTP request exists — so Spring can't hand it a real request-scoped object at that point. Instead it hands the singleton a proxy that, on every method call, looks up "the request-scoped bean for whichever HTTP request is currently being processed on this thread" via a thread-local. This is worth explaining in exactly those terms if asked, because it's a nice small example of the proxy pattern being reused for a completely different purpose than AOP.
- **Custom scopes**: you can implement the `Scope` interface yourself — a genuinely good example if you've done multi-tenant work is a `@TenantScope`, where "one instance per tenant ID" is resolved from a thread-local or request attribute the same way `request` scope resolves per-HTTP-request.

---

## 2. Auto-Configuration — How It Actually Decides What to Wire Up

### 2.1 The core insight to lead with

Auto-configuration is often described as "magic," but it's genuinely just: a big list of ordinary `@Configuration` classes, each one guarded by conditions that get evaluated at startup, that only take effect if their conditions pass **and** you haven't already defined the same bean yourself. If you can say that one sentence clearly and then back it up with mechanism, you'll sound more senior than someone who lists annotation names.

### 2.2 Where the list comes from and how it's loaded

Every autoconfiguration class is listed in a file on the classpath:
```
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```
(In Spring Boot versions before 2.7, this same information lived in `META-INF/spring.factories` under the key `org.springframework.boot.autoconfigure.EnableAutoConfiguration`.) When `@EnableAutoConfiguration` runs (as part of `@SpringBootApplication`, per §1.2), Spring reads every jar on the classpath for this file, collects every listed class name, and adds all of them as candidate `@Configuration` classes — but "candidate" is the key word, because almost none of them will actually activate for any given application.

### 2.3 The conditional annotations, explained by what question each one answers

- **`@ConditionalOnClass(DataSource.class)`** answers: "is the relevant *library* even on the classpath?" If you never added a JDBC driver dependency, `DataSourceAutoConfiguration` never even gets evaluated further — this is why adding `spring-boot-starter-data-jpa` as a dependency is often the *entire* reason a `DataSource` bean appears, without you writing a single line of configuration.
- **`@ConditionalOnMissingBean(DataSource.class)`** answers: "has the *user* (you) already defined this bean themselves?" This is evaluated **after** your own `@Configuration` classes and component-scanned beans have already been registered, specifically so that autoconfiguration always defers to you. This is the entire mechanism behind "convention over configuration, but always overridable" — there's no special-casing, it's just ordering plus a bean-presence check.
- **`@ConditionalOnProperty("spring.datasource.url")`** answers: "did the user configure enough via `application.yml` to make this worth activating?"
- **`@ConditionalOnWebApplication`** / **`@ConditionalOnNotWebApplication`** answer: "is this even a web app context, or a plain batch/CLI context?"
- **`@AutoConfigureOrder`** / **`@AutoConfigureAfter`** / **`@AutoConfigureBefore`** answer: "in what order do autoconfiguration classes need to run relative to each other?" — this matters because some autoconfigurations depend on beans that other autoconfigurations produce (e.g., transaction manager autoconfiguration needs a `DataSource` to already be decided).

### 2.4 Explaining "why doesn't my bean load" as a live debugging narrative

If asked to debug a missing/unexpected bean live, the actual tool is the **autoconfiguration report** — run with `--debug`, or hit the actuator conditions endpoint — which prints every autoconfiguration class under two headings: "Positive matches" (activated, and why) and "Negative matches" (skipped, and which specific condition failed). Being able to say "I'd turn on `--debug` and read the negative-matches section to see exactly which condition failed" is a concrete, checkable answer, much stronger than "I'd check my `application.yml`."

### 2.5 Writing your own starter — walked through with reasoning at each file

This is a common senior/staff-level question framed as "how would you standardize Kafka producer config across 40 microservices at the company." Here's the shape, with the reasoning for each piece:

```
my-platform-kafka-starter/
├── pom.xml
│     depends on spring-boot-autoconfigure (NOT spring-boot-starter, which would
│     pull in a full app skeleton you don't want inside a library)
│
├── src/main/java/.../PlatformKafkaProperties.java
│     @ConfigurationProperties(prefix = "platform.kafka")
│     class PlatformKafkaProperties {
│         private String bootstrapServers;
│         private boolean mtlsEnabled = true;   // sane platform-wide default
│         ...
│     }
│     // Why a properties class instead of @Value: type safety, validation via
│     // @Validated/JSR-303, and every downstream team gets IDE autocomplete
│     // for platform.kafka.* in their application.yml.
│
├── src/main/java/.../KafkaPlatformAutoConfiguration.java
│     @AutoConfiguration
│     @ConditionalOnClass(KafkaTemplate.class)      // only activate if the team
│                                                    // actually pulled in Kafka
│     @EnableConfigurationProperties(PlatformKafkaProperties.class)
│     class KafkaPlatformAutoConfiguration {
│         @Bean
│         @ConditionalOnMissingBean                  // any team can still override
│         KafkaTemplate<String,Object> platformKafkaTemplate(PlatformKafkaProperties p) {
│             // wire in mTLS, standardized serializers, audit-logging interceptor
│         }
│     }
│
└── src/main/resources/META-INF/spring/
      org.springframework.boot.autoconfigure.AutoConfiguration.imports
      // a single line: the fully-qualified name of KafkaPlatformAutoConfiguration
      // -> this file is literally what makes the "auto" part happen when another
      //    team adds this starter as a dependency; without it, they'd need to
      //    manually @Import the configuration class themselves.
```

This is exactly the pattern behind internal "golden path" starters at large orgs — standardized Kafka/mTLS/audit-logging config that every team gets for free but can still override. Having this pre-structured in your head lets you answer both "how does autoconfiguration work" and "how would you build platform tooling" from the same mental model.

---

## 3. Transaction Management — Mechanism First, Then the Traps

### 3.1 Restating the proxy point in this specific context

Everything in §1.4–1.5 applies directly to `@Transactional`: it is implemented as AOP advice via a proxy, so self-invocation doesn't trigger it, `final` methods/classes silently don't get advised, and by default **only public methods** are proxied (a `private` or package-private method annotated `@Transactional` is silently ignored — another real, non-obvious bug source worth having as a mental checklist item).

### 3.2 Propagation, explained through what physical transaction is actually open

Propagation settings answer one question: "when this method starts, and there's already a transaction open on the current thread, what should happen?" Walking through the three most-tested ones in terms of the actual JDBC connection/transaction underneath:

- **`REQUIRED`** (the default): if a transaction is already open on this thread, join it — meaning your work becomes part of the *same* commit/rollback unit as whatever called you. If none is open, start a new one. Most business logic wants this.
- **`REQUIRES_NEW`**: unconditionally suspend whatever transaction is currently open (Spring parks the current one aside), and start a **brand new, physically separate** transaction — its own connection-level begin/commit, independent of the outer one. When the inner method returns, the outer transaction resumes. The concrete use case: audit logging that must survive even if the surrounding business transaction later rolls back. If you log "payment attempted" inside the same transaction as the payment itself, and the payment fails and rolls back, your audit record vanishes too — which defeats the purpose of an audit log. Marking the audit-write method `REQUIRES_NEW` (and calling it from a **separate bean**, because of the proxy rule) makes it commit independently and survive the outer rollback.
- **`NESTED`**: uses a JDBC **savepoint** within the *same* physical transaction, rather than a wholly separate one. If the nested portion fails, only the work since the savepoint rolls back; the outer transaction can catch that failure and continue, ultimately committing everything before the savepoint. The distinction from `REQUIRES_NEW` to be able to state clearly: `NESTED` is still part of the same outer commit/rollback unit and depends on the driver supporting savepoints; `REQUIRES_NEW` is a fully independent transaction that will commit even if the outer one later fails.

### 3.3 Isolation levels tied to what's physically happening at the DB

- **`READ_UNCOMMITTED`**: your transaction can see another transaction's uncommitted writes ("dirty reads") — rarely used, mostly historical/theoretical at interview level.
- **`READ_COMMITTED`**: you only ever see committed data, but if you read the same row twice within your transaction, another committed write in between can make the second read differ ("non-repeatable read"). This is Postgres/Oracle/SQL Server's default.
- **`REPEATABLE_READ`**: guarantees the same row read twice returns the same value within your transaction, but a *new* row matching your query's WHERE clause inserted by someone else could still appear on a re-run of the same query ("phantom read"). This is MySQL/InnoDB's default — and it's worth knowing that InnoDB's actual implementation, via MVCC (each transaction sees a consistent snapshot taken at its first read), prevents most practical phantom-read scenarios even though the isolation level name only strictly promises repeatable-read semantics — a detail that shows you know the real engine behavior, not just the textbook isolation ladder.
- **`SERIALIZABLE`**: transactions behave as if executed one at a time in some serial order — strongest guarantee, but implemented via heavy locking or optimistic conflict detection, so throughput drops and you'll see more rollback-and-retry under contention.

### 3.4 The rollback-on-checked-exception trap, explained fully

Spring's default rollback rule is: **roll back on unchecked exceptions (`RuntimeException`/`Error`), commit through checked exceptions.** This mirrors an old EJB convention, not a Spring invention, but it surprises people constantly:

```java
@Transactional
public void process() throws IOException {
    orderRepository.save(order);
    fileWriter.writeReceipt(order);   // throws IOException
    // Spring sees a CHECKED exception -> does NOT roll back -> the
    // order.save() above COMMITS even though the method as a whole "failed."
}
```
This has caused real data-integrity incidents — a partial write commits silently because the failure happened to be a checked exception. The fix is explicit: `@Transactional(rollbackFor = Exception.class)` (or narrower, `rollbackFor = IOException.class`), which tells Spring to roll back regardless of the exception's checked/unchecked status. If you've hit this in production, it's a strong, concrete story — name the symptom (partial commit), the root cause (checked exception + default rollback rule), and the fix.

### 3.5 `readOnly = true` — what it actually does (and doesn't)

`@Transactional(readOnly = true)` is a **hint**, not an enforcement mechanism — nothing stops you from executing a write statement inside a read-only transaction at the JDBC/SQL level by default (some databases will reject it, most won't). What it *does* reliably do: with Hibernate, it skips dirty-checking and the automatic flush at commit time, which is a real performance win for read-heavy paths. Separately, some connection-routing layers (for read-replica setups) key off this exact flag to decide whether to route the connection to a replica versus the primary — which is the version of this worth mentioning if you've done any read/write splitting, since it turns a "just a hint" fact into a concrete infrastructure decision you can describe.

---

## 4. Exception Handling Architecture — Full Treatment

### 4.1 Restating what went wrong, mechanically

A blanket handler:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        log.error("error", e);
        return ResponseEntity.status(500).body("Internal error");
    }
}
```
`Exception.class` is the root of essentially the entire exception hierarchy other than a few `Error` subtypes, so this single `@ExceptionHandler` intercepts *everything* — a genuine bug (`NullPointerException`), a business-rule violation that should be a 400, a downstream timeout that should be a 503 — and flattens all of them into an identical 500 response with an identical generic message. Three concrete costs of this: (1) callers lose the ability to distinguish "retry this" from "don't retry, your request was wrong," (2) you lose the *type* of the exception in the response, which is often the fastest diagnostic signal, (3) monitoring/alerting built on status codes can't tell a client-caused 4xx-shaped failure from a genuine server bug, so either everything pages on-call or nothing does.

### 4.2 The layered replacement, with the reasoning for each handler

```java
@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse("NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        return ResponseEntity.status(400).body(new ErrorResponse("VALIDATION_FAILED", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)   // thrown by @Valid on @RequestBody
    ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException e) {
        return ResponseEntity.status(400).body(new ErrorResponse("VALIDATION_FAILED", extractFieldErrors(e)));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ErrorResponse> handleConflict(OptimisticLockingFailureException e) {
        return ResponseEntity.status(409).body(new ErrorResponse("CONFLICT", e.getMessage()));
    }

    // Deliberately LAST, and deliberately treated as a genuine safety net rather
    // than the primary mechanism: if a request reaches THIS handler, that means
    // something threw an exception type nobody explicitly planned for, which by
    // definition means it's more likely to be a real bug than an expected business
    // outcome. So this path should alert, not just log.
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        meterRegistry.counter("unhandled.exceptions", "type", e.getClass().getSimpleName()).increment();
        log.error("UNHANDLED EXCEPTION", e);
        return ResponseEntity.status(500).body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
```
The principle to state explicitly in an interview: **business outcomes (not-found, validation failure, conflict) should be modeled as distinct typed exceptions, each mapped to the status code that actually describes what happened; a catch-all `Exception.class` handler should exist purely as a last-resort safety net with monitoring attached, and hitting it in production should be treated as a bug to investigate, not a normal code path.**

### 4.3 A sensible exception hierarchy, and why unchecked is the idiomatic choice here

```
AppException (abstract, extends RuntimeException — unchecked)
 ├── NotFoundException          -> 404
 ├── ValidationException        -> 400
 ├── ConflictException          -> 409
 ├── UpstreamServiceException   -> 502/503, carries a boolean "retryable" flag
 └── AuthorizationException     -> 403
```
Unchecked is the idiomatic choice in Spring codebases for two concrete reasons, not just convention: (1) checked exceptions don't compose cleanly through functional-style code — a `Stream.map()` lambda can't throw a checked exception without wrapping it, which pushes people toward exactly the kind of blanket try/catch that caused the original bug; (2) per §3.4, checked exceptions don't trigger Spring's default `@Transactional` rollback, so using them for business-logic failures inside transactional methods is a second, independent way to accidentally commit partial writes.

### 4.4 The debugging narrative to rehearse for a live "find the bug" round

Structure this as a spoken sequence you can actually narrate under time pressure:
1. **Reproduce with the smallest input** that triggers the wrong output.
2. **Un-mask the real exception.** If a blanket handler is swallowing the type, temporarily log `e.getClass().getName()` inside the catch-all, or add a breakpoint there, before doing anything else — you cannot diagnose what you can't see.
3. **Classify it**: is this an *expected* business outcome that's being mis-handled (e.g., returning 500 for something that should be 404), or a genuine *unexpected* bug (NPE, off-by-one)? This single classification usually tells you whether the fix belongs in the business logic or in how it's being caught/mapped.
4. **Trace the actual logic path**, not just the handler — step through with a debugger or targeted logging to find where the wrong value or null originates.
5. **Fix at the correct layer.** The temptation under time pressure is to patch the exception handler to "handle" the symptom; the correct fix is almost always in the business logic itself, with the exception handler left to correctly *classify* the outcome rather than paper over it.

---

## 5. Spring MVC vs WebFlux — Reasoning About When Reactive Actually Helps

### 5.1 The mental model for the difference

MVC's execution model is **thread-per-request**: each incoming HTTP request is handed a thread from a pool, and that thread stays blocked/occupied for the entire duration of the request — including while waiting on a slow database call or a downstream HTTP call doing nothing but waiting on I/O. Throughput under load is therefore bounded by thread pool size, and a thread pool can only grow so large before context-switching and memory overhead dominate.

WebFlux's execution model is **event-loop based**, built on Reactor (`Mono`/`Flux`), running on a small, fixed number of threads (Netty by default). Nothing blocks a thread waiting on I/O — when your code is waiting on a downstream response, that thread is released back to the event loop to do other work, and a callback resumes your logic when the response actually arrives. This is *only* a throughput win if every layer in the chain is genuinely non-blocking end to end.

### 5.2 The mistake that makes WebFlux worse than MVC, explained precisely

```
If any single hop in a reactive chain calls a BLOCKING library — a
traditional JDBC driver being the classic case, since JDBC has no
non-blocking mode — that blocking call ties up one of the small, fixed
number of event-loop threads for its entire duration.
```
Because there are so few event-loop threads (by design — that's the whole point of the model), blocking even one of them for the duration of a slow JDBC call can stall a disproportionate share of *all* concurrent requests, not just the one that made the blocking call. This is why "just switch to WebFlux for better performance" without also switching to a genuinely reactive stack top-to-bottom (R2DBC instead of JDBC, reactive HTTP clients) can make throughput measurably *worse* than plain MVC, not better. If you must call a blocking library from reactive code, you offload it explicitly onto a separate bounded thread pool via `Schedulers.boundedElastic()`, which isolates the blocking work away from the small event-loop pool.

### 5.3 The interview-ready trade-off answer

If asked "should we migrate to WebFlux," a strong answer names the actual condition under which it helps rather than treating it as a universal upgrade: it helps when the bottleneck is *concurrency under I/O-bound waiting* — e.g., a service that fans out to a dozen downstream calls per request and needs to serve thousands of concurrent requests without thousands of blocked threads — **and** the whole call chain (DB driver, HTTP clients, any blocking library dependency) is reactive. Otherwise, it adds real costs — harder-to-read stack traces, a smaller ecosystem of reactive-compatible libraries, a steeper debugging curve — for no throughput benefit, because the bottleneck was never thread-blocking in the first place.

---

## 6. Resilience & Production Concerns

### 6.1 Resilience4j and the circuit breaker state machine, explained by what each state means operationally

```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")
@Retry(name = "paymentService")
@Bulkhead(name = "paymentService")
@TimeLimiter(name = "paymentService")
public CompletableFuture<PaymentResult> charge(Order o) { ... }
```

- **`CLOSED`**: normal operation — every call goes through to the real downstream dependency, and the breaker is silently counting recent failures.
- **`OPEN`**: once the failure rate crosses a configured threshold within a rolling window, the breaker trips. While open, calls **fail immediately without even attempting the downstream call** — this is the actual point of a circuit breaker: it protects a struggling downstream dependency from being hammered by continued traffic, and protects your own service from piling up threads/connections waiting on a dependency that's already failing.
- **`HALF_OPEN`**: after a configured wait duration, the breaker lets a small number of trial calls through. If they succeed, it transitions back to `CLOSED`; if they fail, it goes back to `OPEN` and waits again.

**The ordering question that separates people who've configured this from people who've only read about it**: retry should generally sit *closer* to the actual call than the circuit breaker, i.e., `TimeLimiter → CircuitBreaker → Retry → Bulkhead` as a typical decoration order, so that repeated retries against a struggling dependency still collectively count toward the circuit breaker's failure threshold — if you get the ordering backwards and retry sits outside the breaker, each retry attempt bypasses the breaker's protection and you can end up hammering an already-failing dependency even harder, which is the exact opposite of what a circuit breaker is for.

### 6.2 HikariCP connection pool exhaustion — a full debugging walkthrough

**Symptom**: p99 latency spikes under load, and thread dumps show application threads `BLOCKED` or `WAITING`, specifically waiting to acquire a connection from the pool (this shows up distinctly from being blocked on application logic — you'll see the stack trace inside HikariCP's `getConnection()`).

**What to check, and why each one is a plausible root cause**:
- **Pool sized too small for actual concurrent load.** A rough starting formula sometimes cited is `connections ≈ (core_count × 2) + effective_spindle_count`, but the real answer is to size it empirically from measured concurrent DB-bound work, then watch `hikaricp.connections.pending` (requests waiting for a connection) via Micrometer/Actuator to confirm whether pool size is actually the bottleneck.
- **Long-running transactions holding a connection the whole time.** A classic version of this: an N+1 query pattern (§6.4) executing inside a single `@Transactional` method holds one connection from the pool for the entire loop duration instead of releasing it quickly — under load, this multiplies pool pressure directly.
- **A genuine connection leak** — some code path acquires a connection and never returns it to the pool (an exception thrown before a `finally`/try-with-resources closes it, for example). HikariCP's `leakDetectionThreshold` configuration is specifically for this: it logs a stack trace of *where the connection was acquired* if it isn't returned within the configured threshold, which is the actual tool you'd reach for to pinpoint a leak rather than guessing.

### 6.3 Actuator + Micrometer, and what each piece is actually for

- **`/actuator/health`**: an aggregate, composed of individual `HealthIndicator` beans — one for the DB, one for disk space, and any custom ones you write. The senior-level point worth making: a custom `HealthIndicator` should check the health of things your service actually *depends on* (a downstream service, a queue connection), not just "is the JVM process running," because a JVM that's up but can't reach its database is not actually healthy from a traffic-routing perspective.
- **`/actuator/metrics`** and **`/actuator/prometheus`**: Micrometer is the vendor-neutral facade; Prometheus/Datadog/CloudWatch are backends it can export to without your application code caring which one is in use. Know the metric types: **`Counter`** (monotonically increasing, e.g., total requests), **`Gauge`** (a point-in-time value that can go up or down, e.g., active connections), **`Timer`** (both a count and a distribution of durations, e.g., request latency), **`DistributionSummary`** (a distribution of arbitrary non-time values, e.g., request payload sizes).
- **`/actuator/threaddump`** and **`/actuator/heapdump`**: what you'd actually pull mid-incident to diagnose thread contention or a memory leak, respectively — worth naming these specifically if asked "what would you actually do" during a live-incident question rather than describing monitoring in the abstract.

### 6.4 N+1 queries, explained with why the "obvious" fix (eager loading) isn't actually the right default

```java
// Order has: @OneToMany(fetch = FetchType.LAZY) List<OrderItem> items;
for (Order o : orderRepository.findAll()) {
    o.getItems().size();   // each call triggers its OWN separate SQL query
}
// Result: 1 query for the orders, then N additional queries — one per order —
// for their items. This is the "N+1" name.
```
The fixes, in order of preference, and the reasoning for each: **`@EntityGraph`** lets you specify, per query method, which associations to eagerly fetch in a single JOIN — the most targeted fix, because it's scoped to the specific query that needs it. **`JOIN FETCH`** in a hand-written JPQL query achieves the same thing more explicitly. **`@BatchSize`** doesn't eliminate the extra queries but batches them (e.g., fetching items for 20 orders in one `IN (...)` query instead of 20 separate ones) — a reasonable middle ground when you can't easily restructure the query. A **DTO/projection query** that selects only the fields you actually need, bypassing the entity association mechanism entirely, is often the best-performing option when you don't need full entity objects.

The reason lazy loading is still the right *default* despite this footgun: eager-loading every association by default means every query that touches `Order` — even ones that never look at `items` — pays the cost of the join or the extra query, which is worse in aggregate across a codebase than occasionally hitting N+1 on the specific paths that do need the association and fixing those explicitly.

### 6.5 Cache correctness — not just the annotation, but the race conditions

Beyond knowing `@Cacheable`/`@CacheEvict` exist, the senior-level content is reasoning about *when caching goes wrong*:
- **Cache-aside**: application checks cache, on miss reads from DB and populates cache. Simple, but the cache and DB can drift if you forget to invalidate on writes.
- **Write-through**: writes go to the cache and the DB together (or the cache writes through to the DB), keeping them consistent at write time at the cost of write latency.
- **Write-behind**: writes go to the cache immediately and are asynchronously flushed to the DB later — lowest write latency, but a real risk of data loss if the cache layer fails before the flush.
- **Eviction-ordering race**: if you evict a cache entry *before* the DB write that invalidated it, and that write then fails, you've evicted a still-valid cache entry for no reason (a performance cost, not a correctness bug). If you evict *after* the write, there's a window where another thread can read the stale cached value between the write completing and the eviction running — a genuine correctness bug, and one that won't self-heal until the entry's TTL expires naturally. Being able to walk through this ordering trade-off out loud is exactly the kind of race-condition reasoning senior interviewers are probing for.
- **Cache stampede** ("thundering herd"): when a very hot key expires, many concurrent requests can all miss the cache simultaneously and all hammer the DB at once to repopulate it. Mitigations: jittered TTLs (so hot keys don't all expire at exactly the same moment) or a lock-and-recompute pattern where only one request repopulates the cache while others wait briefly or serve slightly stale data.

---

## 7. Live Debugging — the Structured Narrative (Rehearse This)

Given the Salesforce round specifically tested this, treat this as something to rehearse verbally, not just read:

```
1. Reproduce / isolate — find the smallest input that triggers the wrong behavior.
2. Un-mask the real exception if something is catching too broadly — you cannot
   fix what you can't see the true type/stack trace of.
3. Classify: expected business outcome mis-handled, vs. genuine unexpected bug.
   This tells you whether the fix belongs in business logic or in exception mapping.
4. Trace the actual logic path with a debugger or targeted logging — don't stop
   at the exception handler, that's where the symptom surfaces, not where it starts.
5. Fix at the correct layer — fix the underlying bug; let the exception handler
   correctly classify the outcome rather than patching the handler to hide it.
```

---

## 8. Spring Boot vs Dropwizard vs Guice — Your Differentiator

You've shipped services on more than one of these, which most Spring-only candidates haven't. Framing to use: don't just list differences — explain *why* each framework made the choices it did.

**Dropwizard's design philosophy** is minimal indirection: it bundles Jetty (server), Jersey (JAX-RS), Jackson (JSON), and Metrics together with very little "magic" wiring — you construct most things explicitly in an `Application.run()` method. The trade-off it's making deliberately: faster startup, a shallower stack to step through when debugging (fewer proxy/reflection layers between your code and the actual behavior), at the cost of more boilerplate and less ecosystem breadth than Spring.

**Guice's design philosophy** is DI without an opinion on anything else — no bundled web framework, no autoconfiguration, just explicit binding modules (`bind(PaymentGateway.class).to(StripeGateway.class)`) resolved by an `Injector`. The trade-off: you get compile-time-visible, explicit wiring (no "why didn't my bean load" debugging, because there's no conditional autoconfiguration layer to reason about) at the cost of building/choosing everything else yourself.

**Spring Boot's design philosophy** is the opposite end of that spectrum: maximize what you get "for free" via conditional autoconfiguration and a huge first-party ecosystem (Spring Security, Spring Data, Spring Cloud), at the cost of more indirection to reason about when something doesn't wire up the way you expect — which is precisely the debugging skill §2.4 is about.

A strong spoken answer: *"I'd reach for Dropwizard or Guice when I want minimal indirection and fast, predictable startup — small, focused services where I want to be able to explain every wiring decision by reading the constructor. I'd reach for Spring Boot when the team is already standardized on it, or when I need the breadth of the ecosystem — Spring Security's OAuth2 resource server support, Spring Data's repository abstraction, Spring Cloud's service-discovery integration — where reimplementing that myself in a leaner framework would cost more than the debugging overhead Spring's autoconfiguration sometimes adds."*

---

## 9. Testing at Senior Level

- **`@SpringBootTest`**: boots the *full* application context — slow, and should be used sparingly, reserved for genuine end-to-end integration tests rather than every test in the suite.
- **`@WebMvcTest(OrderController.class)`**: a "slice" test — loads only the web layer (the specified controller plus MVC infrastructure), and auto-configures Mockito mocks for its dependencies via `@MockBean`, so you're testing request/response handling, validation, and serialization without paying for the full context or a real service layer.
- **`@DataJpaTest`**: slices in just the JPA layer, against an embedded or containerized database, for testing repository query correctness in isolation.
- **Testcontainers over H2**: H2 is a popular in-memory substitute for integration tests, but it doesn't share Postgres's actual SQL dialect, so Postgres-specific behavior (certain functions, certain constraint behaviors, certain JSON operators) can pass against H2 and then fail against real Postgres in production. Testcontainers spins up an actual Postgres instance in Docker for the test run, which is slower per test run but catches a real class of bugs H2 structurally cannot. The trade-off worth stating explicitly if asked: slower CI feedback loop, in exchange for integration tests that are actually testing against the real database engine's behavior.
- **Contract testing** (Spring Cloud Contract, Pact): worth raising if asked about testing strategy across microservice boundaries — it lets a consumer service and a provider service each verify against a shared contract independently, catching breaking API changes before they reach a shared environment, which connects naturally to your distributed-systems background.

---

## 10. Security — OAuth2 Resource Server Pattern

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) {
    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
}
```
The flow to be able to narrate: an external client authenticates against an authorization server (Okta, Keycloak, or an internal equivalent) and receives a JWT. Your service is configured purely as a **resource server** — it never issues tokens itself, it only *validates* incoming ones: checking the signature against the authorization server's published JWKS (JSON Web Key Set) endpoint, checking expiry, and checking the audience/issuer claims match what's expected. `SecurityFilterChain` replaced the older `WebSecurityConfigurerAdapter` pattern (now deprecated) — worth mentioning if the conversation turns to Spring Security configuration style, as a small but genuine "I keep current with the framework" signal.

---

## 11. Rapid-Fire Q&A Bank — With Full Reasoning, Not Just the Answer

**Q: `@Component` vs `@Service` vs `@Repository` vs `@Controller` — what's the actual difference?**
All four are meta-annotated with `@Component`, so functionally, for the purposes of component-scanning and being registered as a bean, they're nearly identical — the difference is mostly semantic/documentation value, signaling intent to other developers and to tools. The one *genuine* behavioral difference: `@Repository` additionally triggers registration of a `PersistenceExceptionTranslationPostProcessor`, which translates persistence-framework-specific exceptions (Hibernate's exceptions, for instance) into Spring's own unchecked `DataAccessException` hierarchy — meaning code calling a `@Repository` can catch a consistent Spring exception type regardless of which underlying persistence technology is in use.

**Q: Constructor injection vs field injection — why does it actually matter, beyond "best practice"?**
Constructor injection lets fields be declared `final`, which gives you real immutability guarantees. It fails fast: if a required dependency is missing, the application fails to start (during step 4a of §1.3) rather than failing at runtime the first time the null field is used. It's directly testable without any reflection or a Spring context at all — `new OrderService(mockPaymentGateway)` in a plain unit test. And it makes circular dependencies **visible as a compile-time-adjacent design problem** (the app fails fast at startup with a clear `BeanCurrentlyInCreationException`) rather than something that silently "just works" via field injection's more forgiving resolution order, which papers over what's usually a real design smell.

**Q: How does Spring pick a bean when there are multiple candidates of the same type?**
First by type. If more than one bean matches, it looks for `@Primary` (a default candidate among several), then `@Qualifier` (an explicit name match at the injection point), then falls back to matching the field/parameter *name* against bean names.

**Q: What actually happens with a circular dependency?**
With constructor injection, it fails at startup, deterministically, with `BeanCurrentlyInCreationException` — which, as above, is a feature: it forces the cycle to be broken by refactoring rather than allowing it to exist. With setter/field injection, Spring *can* resolve simple cases via an internal three-tier cache (`singletonObjects`, `earlySingletonObjects`, and `singletonFactories`, used to expose a not-yet-fully-initialized bean reference early enough to satisfy the other side of the cycle) — but this only works for singleton-scoped beans, and even when it "works," it's still worth flagging as a design smell to refactor rather than something to rely on.

**Q: `@Value` vs `@ConfigurationProperties` — when would you choose one over the other?**
`@Value("${some.property}")` is fine for a single, one-off scalar injection. `@ConfigurationProperties` is the senior-preferred choice for anything beyond that: it binds a whole structured block of configuration to a typed class, supports validation via `@Validated` plus JSR-303 annotations, and is refactor-safe in a way `@Value`'s SpEL-style strings aren't — renaming a field in a `@ConfigurationProperties` class is a normal IDE rename, while a typo inside a `@Value("${...}")` string fails silently at runtime instead of at compile time.

**Q: How would you approach idempotency for a POST endpoint, e.g., creating a payment?**
The standard pattern: the client sends an `Idempotency-Key` header with each attempt; the service checks a fast store (Redis, or a dedicated DB table) for that key before processing — if the key has already been seen, it returns the previously stored response instead of re-executing the operation; if not, it processes the request and stores the key-to-result mapping with a TTL. This is directly relevant to fintech/payment-adjacent work, so it's a strong domain-fit answer to have polished given your background.

**Q: `@ExceptionHandler` on a single controller vs `@ControllerAdvice` — what's the scoping difference, and can you narrow `@ControllerAdvice`?**
An `@ExceptionHandler` method declared inside a specific `@Controller` only catches exceptions thrown from within that controller. `@ControllerAdvice` is global by default, applying across every controller in the application — implemented via Spring MVC's `HandlerExceptionResolver` chain. It doesn't have to be fully global, though: `@ControllerAdvice(basePackages = "...")` or `assignableTypes = {...}` lets you scope it to a specific module or set of controllers, which is a genuinely useful step up from one single global blanket handler if different parts of a large application warrant different exception-handling policies.

---

## 12. Framing and Behavioral Tips for Senior/Staff-Level Rounds

1. **Answer with a trade-off, not a verdict.** "I'd use `REQUIRES_NEW` here because the audit write needs to survive the outer rollback, at the cost of a second physical transaction and connection" is a stronger answer than a bare "use `REQUIRES_NEW`."
2. **Bring your own production stories and use them fully.** The Salesforce exception-handler bug is genuinely a good one — walk through the symptom, the root cause you found, the fix, and specifically what you'd change architecturally (typed exceptions + alerting on the catch-all, per §4) to prevent the same class of bug recurring elsewhere.
3. **Use your Dropwizard/Guice background actively, not defensively.** Most candidates can only speak to Spring. The comparison in §8 is real differentiation — treat it as a strength to volunteer, not a gap to explain away.
4. **When given an open-ended design question, state your assumptions out loud before reaching for annotations.** Throughput, consistency requirements, team size, existing standardization — naming these first signals system-design instinct, which interviewers weight more heavily at senior level than framework trivia.
5. **When you don't know something, say what you'd do to find out, rather than guessing.** "I'd check whether Tomcat's virtual-thread executor is considered production-stable in the version we're pinned to before recommending it" is a stronger answer than fabricating a confident-sounding but wrong detail.

---

## 13. Worth Pre-Reading: Virtual Threads (Spring Boot 3.2+, Java 21)

```
spring.threads.virtual.enabled=true
```
This changes the MVC thread-per-request model at its root: instead of each request occupying a relatively expensive platform (OS) thread from a bounded pool for its whole duration, each request gets a cheap, JVM-managed **virtual thread**. Because virtual threads are extremely lightweight, blocking one of them while waiting on I/O doesn't consume a scarce OS thread the way it would in classic MVC — which closes a meaningful part of the gap with WebFlux's I/O-concurrency advantage, **without** requiring a rewrite to reactive-style code at all. The caveat worth naming if this comes up: virtual threads don't help CPU-bound work (there's no I/O wait to reclaim time from), and blocking inside a `synchronized` block still **pins** the underlying carrier thread rather than releasing it — a real, non-obvious gotcha, for which the practical fix is preferring `ReentrantLock` over `synchronized` in code paths that will run on virtual threads. This is a genuinely current (2024–2025-era) topic that a meaningful fraction of candidates haven't studied yet, so raising it unprompted, with this level of nuance, is a strong signal of staying current with the framework rather than working from a few-years-old mental model.

---

### Suggested Study Order
1. §1–2 (container + autoconfig internals, full mechanism) — this is the foundation everything else builds on, and the part most candidates can only describe at a surface level.
2. §4 (exception handling) — directly addresses your known gap from the Salesforce round; be ready to narrate the full incident.
3. §3 + §6 (transactions + resilience) — the most common source of "senior debugging" style questions.
4. §8 (framework comparison) — your genuine differentiator; rehearse it as a fluent 60–90 second answer, not a table read-out.
5. §11 as flashcards for quick recall, §12 immediately before the interview as a framing reset.
   ENDOFFILE