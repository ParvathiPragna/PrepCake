Design Patterns
---

## 🧱 1. **Creational Patterns**
These deal with **object creation** mechanisms, abstracting the instantiation logic.

### 🔹 Singleton
Ensures a class has **only one instance** and provides global access to it.
```java
public class ConfigManager {
    private static ConfigManager instance = new ConfigManager();
    private ConfigManager() {}
    public static ConfigManager getInstance() {
        return instance;
    }
}
```

### 🔹 Factory Method
Creates objects without specifying the exact class.
```java
interface Notification { void notifyUser(); }
class EmailNotification implements Notification { public void notifyUser() { /*...*/ } }
class NotificationFactory {
    public Notification create(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotification();
            default -> throw new IllegalArgumentException();
        };
    }
}
```

### 🔹 Builder
Constructs complex objects step-by-step.
```java
class User {
    private String name, email;
    public static class Builder {
        private String name, email;
        public Builder setName(String name) { this.name = name; return this; }
        public Builder setEmail(String email) { this.email = email; return this; }
        public User build() { return new User(name, email); }
    }
}
```

---

## 🧩 2. **Structural Patterns**
These focus on **object composition** and relationships.

### 🔹 Adapter
Bridges incompatible interfaces.
```java
interface MediaPlayer { void play(String filename); }
class VLCPlayer { void playVLC(String filename) { /*...*/ } }
class VLCAdapter implements MediaPlayer {
    private VLCPlayer player = new VLCPlayer();
    public void play(String filename) { player.playVLC(filename); }
}
```

### 🔹 Decorator
Adds behavior dynamically without altering the original class.
```java
interface DataSource { String read(); }
class FileDataSource implements DataSource { public String read() { return "data"; } }
class EncryptionDecorator implements DataSource {
    private DataSource wrappee;
    public EncryptionDecorator(DataSource source) { this.wrappee = source; }
    public String read() { return "encrypted(" + wrappee.read() + ")"; }
}
```

---

## 🔄 3. **Behavioral Patterns**
These handle **communication between objects**.

### 🔹 Strategy
Encapsulates interchangeable algorithms.
```java
interface CompressionStrategy { void compress(String data); }
class ZipCompression implements CompressionStrategy { public void compress(String data) { /*...*/ } }
class Compressor {
    private CompressionStrategy strategy;
    public Compressor(CompressionStrategy strategy) { this.strategy = strategy; }
    public void compress(String data) { strategy.compress(data); }
}
```

### 🔹 Observer
Notifies dependent objects of state changes.
```java
interface Subscriber { void update(String news); }
class NewsAgency {
    private List<Subscriber> subscribers = new ArrayList<>();
    public void addSubscriber(Subscriber s) { subscribers.add(s); }
    public void publish(String news) { subscribers.forEach(s -> s.update(news)); }
}
```

---

## 🧠 Bonus: When to Use What
| Pattern        | Use Case Example                              |
|----------------|-----------------------------------------------|
| Singleton      | Config loader, DB connection pool             |
| Factory        | Notification service, payment gateway switch  |
| Builder        | Immutable DTOs, complex object construction   |
| Adapter        | Integrating legacy APIs                       |
| Decorator      | Logging, caching, security wrappers           |
| Strategy       | Compression, sorting, validation logic        |
| Observer       | Event systems, pub-sub, UI updates            |

---
