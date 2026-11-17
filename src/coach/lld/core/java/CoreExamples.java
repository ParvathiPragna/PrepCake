package coach.lld.core.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CoreExamples {


}

//-----------------------------BoundedQueue----------------------------------//
class BlockingQueue<T> {
    private final Integer capacity;

    private Queue<T> queue;

    BlockingQueue(Integer n) {
        this.capacity = n;
        queue = new ArrayDeque<>(n);
    }

    synchronized void enqueue(T item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }

        queue.add(item);
        notifyAll();
    }

    synchronized T dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }

        T item = queue.poll();
        notifyAll();
        return item;
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }

}

//-------------------ConnectionPool---------------------------//

/**
 * requirements:
 * Connection conn = pool.acquire();
 * pool.release(conn);
 */
class ConnectionPool {

    private Integer capacity;

    private final Set<Connection> allConnections = new HashSet<>();

    private final Queue<Connection> freeConnections = new ArrayDeque<>();

    synchronized Connection acquire() throws InterruptedException, SQLException {
        while (allConnections.size() == capacity && freeConnections.isEmpty()) {
            wait();
        }
        if (!freeConnections.isEmpty()) {
            var conn = freeConnections.poll();
            return conn;
        }
        var conn = DriverManager.getConnection("connecturl");
        allConnections.add(conn);
        return conn;
    }

    synchronized void release(Connection connection) {
        freeConnections.offer(connection);
        notifyAll();
    }
}

//-----------------Custom Thread Pool-------------------//

class CustomThreadPoolExecutor {

    final Integer capacity;

    final BlockingQueue<Runnable> queue;

    final Set<WorkerThread> workers;
    final Queue<WorkerThread> availableWorkers;


    CustomThreadPoolExecutor(Integer workerCount, Integer capacity) {
        this.capacity = capacity;
        this.queue = new BlockingQueue<Runnable>(capacity);
        this.workers = getWorkers(queue);
        this.availableWorkers = new ArrayDeque<>(workers);
    }

    private Set<WorkerThread> getWorkers(BlockingQueue<Runnable> taskQueue) {
        int count = 0;
        Set<WorkerThread> q = new LinkedHashSet<>();
        while (count < capacity) {
            var worker = new WorkerThread(taskQueue);
            worker.start();
            q.add(worker);
            count++;
        }
        return q;
    }

    public void submit(Runnable task) throws InterruptedException {
        queue.enqueue(task);
    }
}

class WorkerThread extends Thread {
    final BlockingQueue<Runnable> taskQueue;
    boolean stopped;

    public WorkerThread(BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        while (!stopped) {
            Runnable task = null;

            while (!stopped && !taskQueue.isEmpty()) {
                synchronized (taskQueue) {
                    try {
                        task = taskQueue.dequeue();
                    } catch (InterruptedException e) {
                    }
                }
            }
            try {
                if (task != null) {
                    task.run();
                }
            } catch (Exception exception) {

            }

        }
    }

}


/////////----------------------SEMAPHORE-------------------------//

/**
 * sempahare(3) -> 3 threads can enter critical section at a time
 * others need to wait
 * thoughts:
 * others.can wait in boundingQueue? owners in queue?
 * ConnectionPool of capacity 3 ->
 */

class CustomSemaphore {

    int permits;
    int capacity;

    CustomSemaphore(int capacity) {
        this.capacity = capacity;
        this.permits = capacity;
    }

    synchronized void acquire() throws InterruptedException {
        while (permits == 0) {
            wait();
        }
        permits--;
    }

    synchronized void release() {
        permits++;
        notifyAll();
    }
}


///-------------------------ReadWriteLock----------------------------------//

class ReadWriteLock {
    int activeReaders;
    int activeWriters;

    ReadWriteLock() {
        activeReaders = 0;
        activeWriters = 0;
    }

    synchronized void readLock() throws InterruptedException {
        while (activeWriters != 0) {
            wait();
        }
        activeReaders++;
    }

    synchronized void readUnLock() {
        activeReaders--;
        notifyAll();
    }

    synchronized void writeLock() throws InterruptedException {
        while (activeReaders != 0 || activeWriters != 0) { // reader active or writer active
            wait();
        }
        activeWriters++;
    }

    synchronized void writeUnLock() {
        activeWriters--;
        notifyAll();
    }

}

//-----------------------tokenBUcket--------------------------//
class TokenBucketRateLimiter {

    LocalDateTime lastAccessTime;

    final int rate; // tokens per second

    final int capacity; //max number of tokens

    int tokens;

    TokenBucketRateLimiter(int rate, int capacity) {
        this.rate = rate;
        this.capacity = capacity;
        this.lastAccessTime = LocalDateTime.now(); //start time when token starts coming
        this.tokens = capacity;
    }

    synchronized boolean allowRequest() {
        var current = LocalDateTime.now();
        var elapsed = (int) Duration.between(lastAccessTime, current).getSeconds();
        tokens = Math.min(tokens + elapsed * rate, capacity);
        lastAccessTime = current;
        if (tokens > 0) {
            tokens--;
            return true;
        } else {
            return false;
        }
    }

}

//--------------------------LRUCACHE-----------------------------//

class lruCache<T, U> {
    Map<T, U> map;
    Map<T, LocalDateTime> lastAccess;

    final int capacity;

    lruCache(int capacity) {
        this.capacity = capacity;
        map = new ConcurrentHashMap<>(capacity);
        lastAccess = new LinkedHashMap<>(capacity, 0.75f, true); // in the order of insertion
    }

    synchronized void put(T key, U value) {
        if (map.size() == capacity) {
            evict();
        }
        map.put(key, value);
        lastAccess.put(key, LocalDateTime.now());
    }

    synchronized U get(T key) {
        var value = map.get(key);
        lastAccess.put(key, LocalDateTime.now());
        return value;
    }

    synchronized void evict() {
        var oldEntry = lastAccess.entrySet().iterator().next(); //first ele -> old ele
        lastAccess.remove(oldEntry.getKey());
        map.remove(oldEntry.getKey());
    }
}

//-----------------DELAY-task_schedular---------------------///

class DelayTaskSchedular {
    PriorityQueue<DelayTask> queue;
    boolean stopped = false;

    DelayTaskSchedular() throws InterruptedException {
        queue = new PriorityQueue<>((a, b) -> (int) Duration.between(a.executionTime, b.executionTime).toSeconds());
        runTasks();
    }

    public void schedule(Runnable task, int delay, TimeUnit timeUnit) {
        var ex = LocalDateTime.now().plus(delay, timeUnit.toChronoUnit());
        queue.add(new DelayTask(task, ex));
        notifyAll();
    }

    private void runTasks() throws InterruptedException {
        while (!stopped) {
            while (queue.isEmpty()) {
                wait();
            }
            DelayTask task = null;

            synchronized (queue) {
                if (!queue.isEmpty()) {
                    int delay = (int) Duration.between(queue.peek().executionTime, LocalDateTime.now()).toSeconds();
                    if (delay > 0) {
                        queue.wait(delay);
                    }
                    task = queue.poll();
                }
            }
            if (task != null) {
                task.run();
            }
        }
    }


}

class DelayTask {
    Runnable task;

    LocalDateTime executionTime;

    DelayTask(Runnable task, LocalDateTime executionTime) {
        this.task = task;
        this.executionTime = executionTime;
    }

    void run() {
        this.task.run();
    }

}