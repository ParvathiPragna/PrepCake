
---

## 🧠 Java Collections Interview Cheat Sheet

### 🔹 Core Interfaces & Implementations

```
Collection
├── List
│   ├── ArrayList
│   └── LinkedList
├── Set
│   ├── HashSet
│   ├── LinkedHashSet
│   └── TreeSet
├── Queue
│   ├── PriorityQueue
│   └── ArrayDeque
└── Map
    ├── HashMap
    ├── LinkedHashMap
    ├── TreeMap
    └── ConcurrentHashMap
```

---

### ⚙️ Internal Structures

| Type         | Backing Structure | Notes |
|--------------|-------------------|-------|
| ArrayList    | Dynamic Array     | Fast access, slow insert/delete in middle |
| LinkedList   | Doubly Linked List| Good for frequent insert/delete |
| HashSet      | HashMap           | Keys stored as set elements |
| TreeSet      | Red-Black Tree    | Sorted, log(n) operations |
| HashMap      | Array + LinkedList/Tree | Buckets with chaining |
| TreeMap      | Red-Black Tree    | Sorted keys |
| ConcurrentHashMap | Segmented Buckets | Thread-safe, lock striping |

---

### ⏱️ Time Complexity Summary

| Collection        | Insert | Search | Delete | Order |
|------------------|--------|--------|--------|-------|
| ArrayList        | O(1)*  | O(n)   | O(n)   | ✅    |
| LinkedList       | O(1)   | O(n)   | O(1)   | ✅    |
| HashSet          | O(1)   | O(1)   | O(1)   | ❌    |
| TreeSet          | O(log n)| O(log n)| O(log n)| ✅ Sorted |
| HashMap          | O(1)   | O(1)   | O(1)   | ❌    |
| TreeMap          | O(log n)| O(log n)| O(log n)| ✅ Sorted |
| ConcurrentHashMap| O(1)   | O(1)   | O(1)   | ❌    |

> *ArrayList insert is amortized O(1); resizing costs O(n)

---

### 🧩 When to Use What

| Scenario | Best Fit |
|----------|----------|
| Fast lookup by key | `HashMap` / `ConcurrentHashMap` |
| Maintain insertion order | `LinkedHashMap` / `LinkedHashSet` |
| Sorted data | `TreeMap` / `TreeSet` |
| Thread-safe map | `ConcurrentHashMap` |
| Stack/Queue behavior | `ArrayDeque` |
| Unique elements | `HashSet` |

---

### 🧠 Interview Nuggets

- **HashMap collision resolution**: Linked list → Tree after threshold (Java 8+)
- **Fail-fast iterators**: `ConcurrentModificationException` on structural changes
- **ConcurrentHashMap**: Lock striping for scalability
- **TreeMap/TreeSet**: Use `Comparator` or `Comparable` for custom sorting
- **Immutable collections**: `Collections.unmodifiableList()` or `List.of()` (Java 9+)

---