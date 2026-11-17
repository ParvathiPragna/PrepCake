package processors;

import java.util.*;

public class CacheProcessor {
    public static void main(String[] args) {
        /*
         Constraints:
         - PUT key value ttl currentTime
         - GET key currentTime
         - REMOVE key
         - EVICT policy (LRU or LFU)
         - CAPACITY N
        */

        String[][] queries = {{"CAPACITY", "3"},
                {"PUT", "a", "apple", "5", "0"},
                {"PUT", "b", "banana", "5", "1"},
                {"PUT", "c", "cherry", "5", "2"},
                {"GET", "a", "3"},          // apple
                {"PUT", "d", "date", "5", "4"}, // triggers eviction
                {"GET", "b", "5"},          // banana may be evicted
                {"EVICT", "LFU"}, {"PUT", "e", "elderberry", "5", "6"}, {"GET", "c", "7"}           // cherry expired
        };

        String[] expected = {"true", "true", "true", "true", "apple", "true", "banana", "true", "true", "false"};

        CacheSystem cache = new CacheSystem();
        String[] results = cache.processQueries(queries);

        boolean allPass = true;
        for (int i = 0; i < queries.length; i++) {
            System.out.println(Arrays.toString(queries[i]) + " -> " + results[i] + " | expected: " + expected[i]);
            if (!Objects.equals(results[i], expected[i])) {
                allPass = false;
            }
        }
        System.out.println(allPass ? "All test cases PASSED" : "Some test cases FAILED");
    }
}

class CacheSystem {
    // TODO: implement maps for values, expiry, usage counts, eviction policy

    Policy EVICTION_POLICY = Policy.LRU;
    Map<String, Entity> cache = new HashMap<>();

    enum Policy {LRU, LFU}

    Integer MAX_SIZE;

    public String[] processQueries(String[][] queries) {
        String[] output = new String[queries.length];
        for (int i = 0; i < queries.length; i++) {
            output[i] = switch (queries[i][0]) {
                case "CAPACITY" -> setCapacity(Integer.valueOf(queries[i][1]));
                case "EVICT" -> setEvictionPolicy(queries[i][1]);
                case "PUT" ->
                        addToCache(queries[i][1], queries[i][2], Integer.valueOf(queries[i][3]), Integer.valueOf(queries[i][4]));
                case "GET" -> getFromCache(queries[i][1], Integer.valueOf(queries[i][2]));
                default -> "false";
            };
        }
        return output;
    }

    private String setCapacity(Integer size) {
        MAX_SIZE = size;
        return "true";
    }

    private String setEvictionPolicy(String policy) {
        try {
            EVICTION_POLICY = Policy.valueOf(policy);
            return "true";
        } catch (Exception e) {
            return "false";
        }
    }

    private String addToCache(String key, String value, Integer ttl, Integer time) {
        if (!cache.containsKey(key)) {
            cacheEvictionForAdd(time);
        }
        var entity = new Entity(key, value, ttl, time);
        cache.put(key, entity);
        return "true";
    }

    private void cacheEvictionForAdd(Integer time) {
        refreshCache(time);
        if (cache.size() == MAX_SIZE) {
            var entity = cache.values().stream().sorted(getComparator()).findFirst();
            cache.remove(entity.get().key);
        }
    }

    private void refreshCache(Integer time) {
        var keys = cache.values().stream().filter(e -> e.isExpired(time)).toList();
        keys.forEach(entity -> cache.remove(entity.key));
    }

    Comparator<Entity> getComparator() {
        if (EVICTION_POLICY.equals(Policy.LFU)) {
            return Comparator.comparingInt(Entity::getFrequency);
        }
        return Comparator.comparingInt(Entity::getLastAccessTime);
    }

    private String getFromCache(String key, Integer time) {
        if(!cache.containsKey(key)) return "false";
        if(cache.get(key).isExpired(time)){
            cache.remove(key);
            return "false";
        }
        return cache.get(key).getValue(time);
    }

}

class Entity {
    String value;

    Integer lastAccessTime;

    Integer frequency;

    Integer expiryTime;

    Status status;

    String key;

    enum Status {ACTIVE, EXPIRED}

    Entity(String key, String value, Integer ttl, Integer currentTime) {
        this.value = value;
        this.expiryTime = ttl + currentTime;
        this.lastAccessTime = currentTime;
        this.frequency = 1;
        this.key = key;
        this.status = Status.ACTIVE;
    }

    String getValue(Integer currentTime) {
        frequency++;
        lastAccessTime = currentTime;
        return value;
    }

    Integer getFrequency() {
        return frequency;
    }

    Integer getLastAccessTime() {
        return lastAccessTime;
    }

    boolean isExpired(Integer currentTime) {
        if (this.status.equals(Status.ACTIVE) && this.expiryTime < currentTime) {
            this.status = Status.EXPIRED;
        }
        return this.status.equals(Status.EXPIRED);
    }
}

