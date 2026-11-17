package processors;
import java.util.*;

class RateLimiterProcessor {
    public static void main(String[] args) {
        // Example test input: each query is [command, userId, timestamp]
        String[][] queries = {
                {"ALLOW", "alice", "1"},
                {"ALLOW", "alice", "2"},
                {"ALLOW", "alice", "3"},
                {"ALLOW", "alice", "4"}, // should block if limit=3 per window
                {"ALLOW", "alice", "5"},
                {"ALLOW", "alice", "10"},
                {"ALLOW", "alice", "12"},

                {"RESET", "alice"},
                {"ALLOW", "alice", "15"}  // allowed again after reset
        };

        // Configure rate limiter: max 3 requests per 3-second window
        RateLimiter limiter = new RateLimiter(3, 3);
        String[] results = limiter.processQueries(queries);

        System.out.println("=== Test Results ===");
        for (int i = 0; i < queries.length; i++) {
            System.out.println(Arrays.toString(queries[i]) + " -> " + results[i]);
        }
    }
}

class RateLimiter {

    Map<String, Queue<Integer>> userRequestQueue;
    Integer maxRequests;
    Integer window;

    RateLimiter(int req, int size) {
        maxRequests = req;
        window = size;
        userRequestQueue = new HashMap<>();
    }

    public String[] processQueries(String[][] queries) {
        String[] output = new String[queries.length];
        for (int i = 0; i < queries.length; i++) {
            switch (queries[i][0]) {
                case "ALLOW" -> output[i] = processRequest(queries[i][1], Integer.valueOf(queries[i][2]));
                case "RESET" -> output[i] = resetForUser(queries[i][1]);
            }
        }
        return output;
    }

    private String resetForUser(String user) {
        userRequestQueue.remove(user);
        return "true";
    }

    private String processRequest(String user, Integer time) {
        userRequestQueue.putIfAbsent(user,new ArrayDeque<>());
        var queue = userRequestQueue.get(user);
        // remove  requests outside the window
        while(!queue.isEmpty() && queue.peek() < time - window) {
            queue.poll();
        }
        if(queue.size() < maxRequests) {
            queue.add(time);
            return "true";
        }
        return "false";
    }
}
