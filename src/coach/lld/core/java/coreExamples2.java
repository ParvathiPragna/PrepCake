package coach.lld.core.java;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class coreExamples2 {
}

/**
 * Future
 * get() blocks until result available
 * complete() should succeed only once
 * Multiple threads may call get()
 * Thread Safe
 * Future<String> future =
 * new CustomFuture<>();
 */
class CustomFuture<T> {
    T value;

    boolean isDone;


    synchronized T get() throws InterruptedException {
        while (!isDone) {
            wait();
        }
        return value;
    }

    synchronized void complete(T value) {
        if (!isDone) {
            this.value = value;
            isDone = true;
            notifyAll();
            return;
        }
        throw new IllegalStateException();
    }

    boolean isDone() {
        return isDone;
    }
}
//-------------CUstomConcurrentHAShMAp------------------//

/**
 * ConcurrentHashMap<K,V> map =
 * new ConcurrentHashMap<>(16);
 * <p>
 * map.put("A",1);
 * <p>
 * Integer value = map.get("A");
 * <p>
 * map.remove("A");
 */

class ConcurrentHashMap<K, V> {
    Map<K, V> map;
    int capacity;
    private final ReadWriteLock[] lockArray;

    ConcurrentHashMap(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>(capacity);
        lockArray = new ReadWriteLock[capacity];
        for (ReadWriteLock lock : lockArray) {
            lock = new ReadWriteLock();
        }
    }

    void put(K key, V value) throws InterruptedException {
        var flagIndex = key.hashCode() % capacity;
        lockArray[flagIndex].writeLock();
        try {
            map.put(key, value);
        } finally {
            lockArray[flagIndex].writeUnLock();
        }
    }

    V get(K key) throws InterruptedException {
        var flagIndex = key.hashCode() % capacity;
        lockArray[flagIndex].readLock();

        try {
            V value = map.get(key);
            return value;
        } finally {
            lockArray[flagIndex].writeUnLock();
        }
    }
}

//---------ProducerConsumerQueue---------//

class ProducerConsumerQueue<T> {
    int capacity;
    Queue<T> queue;

    boolean shutdown;

    ProducerConsumerQueue(int capacity) {
        this.capacity = capacity;
        queue = new ArrayDeque<>(capacity);
        shutdown = false;
    }

    synchronized void put(T item) throws InterruptedException {
        while (!shutdown && queue.size() == capacity) {
            wait();
        }
        if (shutdown) {
            throw new IllegalStateException();
        }
        queue.add(item);

        notifyAll();
    }

    synchronized T take() throws InterruptedException {
        while (!shutdown && queue.isEmpty()) {
            wait();
        }
        if (shutdown && queue.isEmpty()) {
            return null;
        }
        var item = queue.poll();
        notifyAll(); // notify other producers to push
        return item;
    }

    void shutDown() {
        shutdown = true;
        notifyAll();
    }

}

//-----------------------LFU---------------------------------//

class LFUCache<K, V> {

    private final int capacity;

    private int size;

    private int minFrequency;

    private final Map<K, Node<K, V>> keyToNode;

    private final Map<Integer, LinkedList<Node<K, V>>> frequencyMap;


    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.minFrequency = 0;

        this.keyToNode = new HashMap<>();
        this.frequencyMap = new HashMap<>();
    }

    public V get(K key) {

        Node<K, V> node = keyToNode.get(key);

        if (node == null) {
            return null;
        }

        increaseFrequency(node);

        return node.value;
    }

    public void put(K key, V value) {

        if (capacity == 0) {
            return;
        }

        if (keyToNode.containsKey(key)) {

            Node<K, V> node = keyToNode.get(key);

            node.value = value;

            increaseFrequency(node);

            return;
        }

        if (size == capacity) {
            evictLFU();
        }

        Node<K, V> node = new Node<>(key, value);

        keyToNode.put(key, node);

        LinkedList<Node<K, V>> list = frequencyMap.computeIfAbsent(1, f -> new LinkedList<>());

        list.addFirst(node);

        minFrequency = 1;

        size++;
    }

    private void increaseFrequency(Node<K, V> node) {

        int oldFrequency = node.frequency;

        LinkedList<Node<K, V>> oldList = frequencyMap.get(oldFrequency);

        oldList.remove(node);

        if (oldFrequency == minFrequency && oldList.isEmpty()) {

            minFrequency++;
        }

        node.frequency++;

        LinkedList<Node<K, V>> newList = frequencyMap.computeIfAbsent(node.frequency, f -> new LinkedList<>());

        newList.addFirst(node);
    }

    private void evictLFU() {

        LinkedList<Node<K, V>> list = frequencyMap.get(minFrequency);

        Node<K, V> victim = list.removeLast();

        keyToNode.remove(victim.key);

        size--;
    }

    static class Node<K, V> {
        K key;
        V value;
        int frequency = 1;

        Node(K key, V value) {

            this.key = key;
            this.value = value;
        }
    }


    //-------circuitBreaker-------//
    interface CircuitBreaker {

        <T> T execute(
                Supplier<T> operation) throws Exception;

        boolean allowRequest();

        void recordSuccess();

        void recordFailure();

        CircuitState getState();

        enum CircuitState {OPEN, CLOSED, HALF_OPEN}
    }

    /**
     * circuit starts closed -> on failures reaching threshold it opens
     * once opened -> wait for timeout -> it halfopens
     * then start counting success and failures -> if successCount >= sucessThreshold && failurecount*2<=failure threshold then gsate is closed
     * if gate is open -> donot allow any
     * if gate is halfopen -> allow but track count to switch status to open or  closed
     * if closed -> allow but track failurecount
     */
    class CustomCircuitBreaker implements CircuitBreaker {

        CircuitState state;

        int successThreshold; // number of success required to move from half_open to closed
        int failureThreshold; // number of failure to trigger to move from closed/half_open to open then timeout -> halfopen

        int timeoutInSeconds;

        int successCount;
        int failureCount;

        LocalDateTime openTime;


        public CustomCircuitBreaker(int successThreshold, int failureThreshold, int timeout) {
            this.state = CircuitState.CLOSED;
            this.successThreshold = successThreshold;
            this.failureThreshold = failureThreshold;
            this.timeoutInSeconds = timeout;
            this.successCount = 0;
            this.failureCount = 0;
        }

        @Override
        public <T> T execute(Supplier<T> operation) throws Exception {
            if (allowRequest()) {
                try {
                    T result = operation.get();
                    recordSuccess();
                    return result;
                } catch (Exception ex) {
                    recordFailure();
                    throw ex;
                }
            }
            throw new Exception("circuit open");
        }

        @Override
        public boolean allowRequest() {

            return switch (state) {
                case OPEN -> checkForHalfOpen();
                case CLOSED, HALF_OPEN -> true;
            };
        }

        private void tryGateClosure() {
            if (state.equals(CircuitState.HALF_OPEN)) {
                if (successCount >= successThreshold && failureCount * 2 <= failureThreshold) {
                    resetCircuitState();
                    state = CircuitState.CLOSED;
                }
            }
        }

        private boolean timeoutCompleted() {
            return Duration.between(openTime, LocalDateTime.now()).toSeconds() >= timeoutInSeconds;
        }

        private boolean failureInControl() {
            return failureCount < failureThreshold;
        }

        private void openGate() {
            resetCircuitState();
            openTime = LocalDateTime.now();
            state = CircuitState.OPEN;
        }

        private boolean checkForHalfOpen() {
            if (timeoutCompleted()) {
                resetCircuitState();
                state = CircuitState.HALF_OPEN;
                return true;
            }
            return false;
        }

        private void resetCircuitState() {
            openTime = null;
            successCount = 0;
            failureCount = 0;
        }

        @Override
        public void recordSuccess() {
            successCount++;
            if (state.equals(CircuitState.HALF_OPEN)) {
                tryGateClosure();
            }
        }

        @Override
        public void recordFailure() {
            failureCount++;
            if (!state.equals(CircuitState.OPEN) && !failureInControl()) {
                openGate();
            }
        }

        @Override
        public CircuitState getState() {
            return this.state;
        }
    }

    //-------AsyncEventBus---------//

    interface AsyncEventBus {

        <T extends Event>
        void subscribe(
                Class<T> eventType,
                EventHandler<T> handler);

        <T extends Event>
        void unsubscribe(
                Class<T> eventType,
                EventHandler<T> handler);

        void publish(
                Event event);
    }

    interface Event {
    }

    interface EventHandler<T extends Event> {

        void handle(Event event);
    }

    class CustomAsyncEventBus<T extends Event> implements AsyncEventBus {
        Map<Class<? extends Event>, List<EventHandler<?>>> subscribers;
        ExecutorService executor;

        public CustomAsyncEventBus() {
            this.subscribers = new HashMap<>();
            this.executor = Executors.newScheduledThreadPool(3);
        }

        @Override
        public <T extends Event> void subscribe(Class<T> eventType, EventHandler<T> handler) {
            subscribers.computeIfAbsent(eventType, f -> new ArrayList<>()).add(handler);
        }

        @Override
        public <T extends Event> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
            var handlers = subscribers.get(eventType);
            if (handlers != null) {
                handlers.remove(handler);
            }
        }

        @Override
        public void publish(Event event) {
            for (var handler : subscribers.get(event.getClass())) {
                executor.submit(() -> {
                    try {
                        handler.handle(event);
                    } catch (Exception ex) {
                        //log error
                    }
                });
            }
        }

    }
}
