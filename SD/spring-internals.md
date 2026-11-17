# Spring Internals - 15 Minute Revision

## Mental Models

```text
DI
=
Recursive Object Graph Construction
```

```text
Singleton Scope
=
Cache<Class, Object>
```

```text
Prototype Scope
=
new Object()
```

```text
Persistence Context
=
Map<(Class,Id), Entity>
```

```text
@Transactional
=
Proxy + Begin + Commit/Rollback
```

```text
Auto Configuration
=
Conditional Bean Registration
```

---

# Dependency Injection

Problem:

```java
OrderService
    -> UserService
    -> Repository
```

Container flow:

```text
getBean(OrderService)

    ↓

inspect constructor

    ↓

resolve dependencies recursively

    ↓

create bean

    ↓

cache singleton
```

Interview:

```text
Spring uses reflection to inspect constructors,
resolve dependencies recursively and create beans.
```

---

# Bean Lifecycle

```text
Constructor
    ↓
Dependency Injection
    ↓
@PostConstruct
(afterPropertiesSet)
    ↓
Bean Ready
    ↓
@PreDestroy
(destroy)
```

Interview:

```text
Initialization callbacks execute only after
all dependencies are injected.
```

---

# Bean Scopes

## Singleton

```java
@Service
```

One instance per ApplicationContext.

```java
getBean()
getBean()
```

Same object.

---

## Prototype

```java
@Scope("prototype")
```

```java
getBean()
getBean()
```

Different object.

---

### Gotcha

```java
Singleton
    ↓
Prototype
```

Prototype resolved once during singleton creation.

Interview:

```text
A prototype injected into a singleton behaves like
a singleton unless Provider/ObjectFactory is used.
```

---

# Spring Boot Auto Configuration

Core Annotations:

```java
@EnableAutoConfiguration
```

```java
@ConditionalOnClass
```

```java
@ConditionalOnProperty
```

```java
@ConditionalOnMissingBean
```

Example:

```text
db.url exists
    ↓
DataSource bean created
```

Interview:

```text
Spring Boot evaluates conditions and registers
beans automatically.
```

---

# Persistence Context

Internal View:

```java
Map<EntityKey, Entity>
```

EntityKey:

```java
(Class, PrimaryKey)
```

Example:

```java
find(User.class, 1);
find(User.class, 1);
```

Result:

```java
u1 == u2 // true
```

Interview:

```text
Persistence Context guarantees one managed
entity instance per primary key.
```

---

# First Level Cache

Scope:

```text
EntityManager
```

Properties:

```text
Enabled by default
Transaction scoped
Not shared across transactions
```

Flow:

```java
find()
```

```text
Cache Hit -> return entity
Cache Miss -> DB -> Cache
```

---

# @Transactional Internals

Actual Idea:

```java
begin();

try {
    method();
    commit();
} catch(Exception e) {
    rollback();
}
```

How?

```text
Spring AOP Proxy
```

Flow:

```text
Proxy
    ↓
Begin Transaction
    ↓
Method Execution
    ↓
Commit / Rollback
```

Interview:

```text
@Transactional is implemented using proxies.
```

---

# Self Invocation Problem

Does NOT work:

```java
class UserService {

    public void methodA() {
        methodB();
    }

    @Transactional
    public void methodB() {

    }
}
```

Reason:

```text
Proxy is bypassed.
```

Interview:

```text
Internal method calls do not pass through the
Spring proxy, so transactional advice is skipped.
```

---

# Propagation

## REQUIRED (Default)

```text
Join existing transaction
Else create new
```

---

## REQUIRES_NEW

```text
Suspend current transaction
Start new transaction
```

Most Asked:

```text
Outer transaction fails.
What happens to REQUIRES_NEW?

Answer:
Already committed independently.
```

---

# Isolation Levels

```text
READ_UNCOMMITTED
READ_COMMITTED
REPEATABLE_READ
SERIALIZABLE
```

PostgreSQL Default:

```text
READ COMMITTED
```

---

# Optimistic Locking

```java
@Version
private Long version;
```

Flow:

```text
Read Version = 5

TX1 Update
Version = 6

TX2 Update
Version Mismatch
Exception
```

No DB lock.

---

# Pessimistic Locking

```sql
SELECT * FROM users
FOR UPDATE;
```

Flow:

```text
Lock Row
Other Writers Wait
```

---

# Lazy vs Eager Loading

## Lazy

```java
fetch = LAZY
```

Load only when accessed.

---

## Eager

```java
fetch = EAGER
```

Load immediately.

---

# N + 1 Problem

Example:

```java
List<Order> orders = repo.