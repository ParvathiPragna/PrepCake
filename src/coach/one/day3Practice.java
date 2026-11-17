package coach.one;

import java.util.*;

public class day3Practice {

    public static void main(String[] args) {

        /*
         ============================================================
         PROBLEM 1 : LRU CACHE
         ============================================================

         Design and implement a data structure that follows
         the Least Recently Used (LRU) cache policy.

         Implement:

         LRUCache(int capacity)
         int get(int key)
         void put(int key, int value)

         Rules:

         - get(key) returns value if present else -1
         - put(key,value) inserts/updates key
         - when capacity exceeded, evict least recently used key
         - both operations must be O(1)

         Example:

         LRUCache cache = new LRUCache(2);

         put(1,1)
         put(2,2)

         get(1) => 1

         put(3,3)

         get(2) => -1

         put(4,4)

         get(1) => -1
         get(3) => 3
         get(4) => 4
         */

        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);

        System.out.println(cache.get(1)); // 1

        cache.put(3, 3);

        System.out.println(cache.get(2)); // -1

        cache.put(4, 4);

        System.out.println(cache.get(1)); // -1
        System.out.println(cache.get(3)); // 3
        System.out.println(cache.get(4)); // 4


        /*
         ============================================================
         PROBLEM 2 : ALIEN DICTIONARY
         ============================================================

         There is a new alien language that uses the English
         alphabet but the order of letters is unknown.

         Given a sorted dictionary of words according to the
         alien language, return one valid character order.

         If no valid ordering exists return "".

         Example:

         Input:
         ["wrt","wrf","er","ett","rftt"]

         Output:
         "wertf"

         Explanation:

         wrt -> wrf : t before f
         wrf -> er  : w before e
         er  -> ett : r before t
         ett -> rftt: e before r
         */

        String[] words = {"wrt", "wrf", "er", "ett", "rftt"};

        System.out.println(alienOrder(words));
        // expected: wertf
        String[] words1 = {
                "wrt", "wrf",
                "er", "ett",
                "rftt"
        };

        System.out.println(
                alienOrder(words1));

        String[] words2 = {
                "abc",
                "ab"
        };

        System.out.println(
                alienOrder(words2)); // ""

        /*
         ============================================================
         PROBLEM 3 : NETWORK DELAY TIME
         ============================================================

         There are N servers numbered 1 to N.

         times[i] = [u,v,w]

         means signal travels from u to v
         in w units of time.

         Starting from source K,
         determine minimum time needed for all nodes
         to receive the signal.

         Return -1 if impossible.

         Example:

         times =
         [
           [2,1,1],
           [2,3,1],
           [3,4,1]
         ]

         n = 4
         k = 2

         Output = 2
         */

        int[][] times = {{2, 1, 1}, {2, 3, 1}, {3, 4, 1}};

        System.out.println(networkDelayTime(times, 4, 2)); // 2

        /*
         ============================================================
         PROBLEM 4 : COURSE SCHEDULE II
         ============================================================

         There are numCourses courses.

         prerequisites[i] = [a,b]

         means course b must be completed
         before course a.

         Return one valid order to complete all courses.

         If impossible return empty array.

         Example:

         numCourses = 4

         prerequisites =
         [
          [1,0],
          [2,0],
          [3,1],
          [3,2]
         ]

         Output:

         [0,1,2,3]
         */

        int[][] prerequisites = {{1, 0}, {2, 0}, {3, 1}, {3, 2}};

        System.out.println(Arrays.toString(findOrder(4, prerequisites)));


        /*
         ============================================================
         PROBLEM 5 : DESIGN FILE SYSTEM
         ============================================================

         Design a filesystem supporting:

         createPath(path,value)
         get(path)

         Rules:

         - Parent must already exist
         - Creating existing path returns false
         - Missing path returns -1

         Example:

         createPath("/a",1) => true

         createPath("/a/b",2) => true

         get("/a") => 1

         get("/a/b") => 2

         get("/x") => -1
         */

        FileSystem fs = new FileSystem();

        System.out.println(fs.createPath("/a", 1)); // true

        System.out.println(fs.createPath("/a/b", 2)); // true

        System.out.println(fs.get("/a")); // 1

        System.out.println(fs.get("/a/b")); // 2

        System.out.println(fs.get("/x")); // -1
    }


    // ============================================================
    // TODO : LRU CACHE
    // Pattern:
    // HashMap + Doubly Linked List
    // ============================================================

    static class LRUCache {

        public LRUCache(int capacity) {

        }

        public int get(int key) {
            return -1;
        }

        public void put(int key, int value) {

        }
    }


    // ============================================================
    // TODO : ALIEN DICTIONARY
    // Pattern:
    // Graph + Topological Sort
    // ============================================================

    static List<String> alienOrder(String[] words) {

        Map<Character, Integer> indegree = new HashMap<>();
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Boolean> visited = new HashMap<>();

        for (int i = 0; i < words.length; i++) {
            for (var c : words[i].toCharArray()) {
                indegree.putIfAbsent(c, 0);
                graph.put(c, new HashSet<>());
                visited.put(c, false);
            }
        }

        for (int i = 1; i < words.length; i++) {
            String a = words[i - 1];
            String b = words[i];
            int l = Math.min(a.length(), b.length());
            for (int k = 0; k < l; k++) {
                if (a.charAt(k) != b.charAt(k)) {
                    if (!graph.get(a.charAt(k)).contains(b.charAt(k))) {
                        indegree.put(b.charAt(k), indegree.get(b.charAt(k)) + 1); //no.of chars before this
                        graph.get(a.charAt(k)).add(b.charAt(k));
                    }
                    break;
                }
            }
        }

        List<String> results = new ArrayList<>();
        for (var entry : indegree.entrySet()) {
            if (entry.getValue() == 0 && !visited.get(entry.getKey())) {
                StringBuilder sb = new StringBuilder();
                Queue<Character> q = new ArrayDeque<>();
                q.add(entry.getKey());
                while (!q.isEmpty()) {
                    var curr = q.poll();
                    sb.append(curr);
                    visited.put(curr, true);
                    for (var node : graph.get(curr)) {
                        indegree.put(node, indegree.get(node) - 1);
                        if (indegree.get(node) == 0) {
                            q.add(node);
                        }
                    }
                }
                results.add(sb.toString());
            }
        }
        return results;
    }


    static String alienOrder2(String[] words) {

        Map<Character, Set<Character>> graph = new HashMap<>();

        Map<Character, Integer> indegree = new HashMap<>();

        for (String word : words) {
            for (char c : word.toCharArray()) {
                graph.putIfAbsent(c, new HashSet<>());
                indegree.putIfAbsent(c, 0);
            }
        }

        for (int i = 0; i < words.length - 1; i++) {

            String a = words[i];
            String b = words[i + 1];

            if (a.length() > b.length() && a.startsWith(b)) return "";

            int min = Math.min(a.length(), b.length());

            for (int j = 0; j < min; j++) {

                char c1 = a.charAt(j);
                char c2 = b.charAt(j);

                if (c1 != c2) {

                    if (graph.get(c1).add(c2)) {
                        indegree.put(c2, indegree.get(c2) + 1);
                    }

                    break;
                }
            }
        }

        Queue<Character> q = new LinkedList<>();

        for (char c : indegree.keySet()) {
            if (indegree.get(c) == 0) q.offer(c);
        }

        StringBuilder sb = new StringBuilder();

        while (!q.isEmpty()) {

            char current = q.poll();

            sb.append(current);

            for (char next : graph.get(current)) {

                indegree.put(next, indegree.get(next) - 1);

                if (indegree.get(next) == 0) q.offer(next);
            }
        }

        return sb.length() == indegree.size() ? sb.toString() : "";
    }

    // ============================================================
    // TODO : NETWORK DELAY TIME
    // Pattern:
    // Dijkstra -> add min edge to pq mark it visiied and update dist to src  to its neighbours
    // Min Heap
    // ============================================================

    static int networkDelayTime(int[][] times, int n, int k) {
        int[][] dist = new int[n + 1][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        int[] srcDist = new int[n];
        Arrays.fill(srcDist, Integer.MAX_VALUE);

        for (int i = 0; i < times.length; i++) {
            dist[times[i][0] - 1][times[i][1] - 1] = times[i][2];

        }
        srcDist[k - 1] = 0;
        PriorityQueue<int[]> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

        priorityQueue.add(new int[]{k - 1, 0});

        while (!priorityQueue.isEmpty()) {
            var curr = priorityQueue.poll();
            for (int i = 0; i < n; i++) {
                if (i != curr[0] && dist[curr[0]][i] != Integer.MAX_VALUE) {
                    if (dist[curr[0]][i] + srcDist[curr[0]] < srcDist[i]) {
                        srcDist[i] = dist[curr[0]][i] + srcDist[curr[0]];
                        priorityQueue.add(new int[]{i, srcDist[i]});
                    }
                }
            }
        }

        return Arrays.stream(srcDist).max().getAsInt();

    }


    // ============================================================
    // TODO : COURSE SCHEDULE II
    // Pattern:
    // Kahn's Algorithm
    // Topological Sort
    // ============================================================

    static int[] findOrder(int numCourses, int[][] prerequisites) {
        //child,parent [a,b]

        Map<Integer, Set<Integer>> parentMap = new HashMap<>();
        Map<Integer, Integer> indegree = new HashMap<>(); //no parent for childs
        for (int i = 0; i < numCourses; i++) {
            indegree.put(i, 0);
            parentMap.put(i, new HashSet<>());
        }
        for (int i = 0; i < prerequisites.length; i++) {
            int child = prerequisites[i][0];
            int parent = prerequisites[i][1];
            var set = parentMap.get(parent);
            set.add(child);
            parentMap.put(parent, set);
            indegree.put(child, indegree.get(child) + 1);
        }

        List<Integer> order = new ArrayList<>();
        Queue<Integer> q = new ArrayDeque<>();
        for (var entry : indegree.entrySet()) {
            if (entry.getValue() == 0) {
                q.add(entry.getKey());
            }
        }

        while (!q.isEmpty()) {
            var curr = q.poll();
            for (var child : parentMap.get(curr)) {
                indegree.put(child, indegree.get(child) - 1);
                if (indegree.get(child) == 0) {
                    q.add(child);
                }
            }
            order.add(curr);
        }
        //  return  order.toArray(new int[]);
        return order.stream().mapToInt(Integer::intValue).toArray();
    }


    // ============================================================
    // TODO : DESIGN FILE SYSTEM
    // Pattern:
    // Trie / HashMap
    // ============================================================

    static class FileSystem {
        static class TrieNode {

            Map<String, TrieNode> children =
                    new HashMap<>();

            int value = -1;
        }

        TrieNode root;

        FileSystem() {
            root = new TrieNode();
        }

        public boolean createPath(String path, int value) {
            if (path.isEmpty() || !path.startsWith("/") || path.endsWith("/")) {
                return false;
            }
            var parts = path.split("/");
            var curr = root;
            for (int i = 1; i < parts.length - 1; i++) {
                if (!curr.children.containsKey(parts[i])) {
                    return false;
                }
                curr = curr.children.get(parts[i]);
            }

            var last = parts[parts.length - 1];
            if (curr.children.containsKey(last)) {
                return false; //duplicate
            }
            var node = new TrieNode();
            node.value = value;
            curr.children.put(last, node);
            return true;
        }

        public int get(String path) {
            if (path.isEmpty() || !path.startsWith("/") || path.endsWith("/")) {
                return -1;
            }
            var parts = path.split("/");
            var curr = root;
            for (int i = 1; i < parts.length - 1; i++) {
                if (!curr.children.containsKey(parts[i])) {
                    return -1;
                }
                curr = curr.children.get(parts[i]);
            }

            if (curr.children.containsKey(parts[parts.length - 1])) {
                return curr.children.get(parts[parts.length - 1]).value;
            }
            return -1;
        }
    }


}