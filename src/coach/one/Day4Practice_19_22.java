package coach.one;

import java.util.*;

public class Day4Practice_19_22 {

    public static void main(String[] args) {

        /*
         ===========================================================
         19. RECONSTRUCT ITINERARY

         You are given a list of airline tickets where

         tickets[i] = [from, to]

         Reconstruct the itinerary starting from "JFK".

         Rules:
         - Use all tickets exactly once.
         - If multiple valid itineraries exist,
           return the lexicographically smallest.

         Example:

         JFK -> MUC
         MUC -> LHR
         LHR -> SFO
         SFO -> SJC

         Output:

         [JFK,MUC,LHR,SFO,SJC]

         Pattern:
         Graph
         Eulerian Path
         PriorityQueue

         ===========================================================
         */

        List<List<String>> tickets = new ArrayList<>();

        tickets.add(Arrays.asList("MUC", "LHR"));

        tickets.add(Arrays.asList("JFK", "MUC"));

        tickets.add(Arrays.asList("SFO", "SJC"));

        tickets.add(Arrays.asList("LHR", "SFO"));

        System.out.println(findItinerary(tickets));



        /*
         ===========================================================
         20. BUS ROUTES

         routes[i] = bus stops for bus i.

         Find minimum buses required to travel
         from source to target.

         Example:

         routes =
         [
           [1,2,7],
           [3,6,7]
         ]

         source = 1
         target = 6

         Output = 2

         Pattern:
         BFS
         Graph Modeling

         ===========================================================
         */

        int[][] routes = {{1, 2, 7}, {3, 6, 7}};

        System.out.println(numBusesToDestination(routes, 1, 6)); // 2



        /*
         ===========================================================
         21. MINIMUM HEIGHT TREES

         Given an undirected graph with n nodes.

         Find all roots that produce minimum
         height trees.

         Example:

         n = 4

         edges =
         [
           [1,0],
           [1,2],
           [1,3]
         ]

         Output:
         [1]

         Pattern:
         Topological Sort
         Leaf Peeling

         ===========================================================
         */

        int[][] edges = {{1, 0}, {1, 2}, {1, 3}};

        System.out.println(findMinHeightTrees(4, edges)); // [1]



        /*
         ===========================================================
         22. RANDOM PICK WITH WEIGHT

         Given weights:

         [1,3]

         Probability:

         index 0 -> 25%
         index 1 -> 75%

         Implement:

         Solution(int[] w)

         int pickIndex()

         Pattern:
         Prefix Sum
         Binary Search

         ===========================================================
         */

        Solution solution = new Solution(new int[]{1, 3});

        System.out.println(solution.pickIndex());

        System.out.println(solution.pickIndex());

        System.out.println(solution.pickIndex());

        System.out.println(solution.pickIndex());
    }


    /*
     ===========================================================
     19. RECONSTRUCT ITINERARY
     ===========================================================
     */
    static List<String> findItinerary(List<List<String>> tickets) {

        Map<String, PriorityQueue<String>> graph = new HashMap<>();

        for (List<String> ticket : tickets) {

            String from = ticket.get(0);
            String to = ticket.get(1);

            graph.computeIfAbsent(from, k -> new PriorityQueue<>()).offer(to);
        }

        LinkedList<String> result = new LinkedList<>();

        dfs("JFK", graph, result);

        return result;
    }

    static void dfs(String airport, Map<String, PriorityQueue<String>> graph, LinkedList<String> result) {

        PriorityQueue<String> destinations = graph.get(airport);

        while (destinations != null && !destinations.isEmpty()) {

            String next = destinations.poll();

            dfs(next, graph, result);
        }

        result.addFirst(airport);
    }


    /*
     ===========================================================
     20. BUS ROUTES
     ===========================================================
     */
    static int numBusesToDestination(int[][] routes, int source, int target) {

        if (source == target) return 0;

        Map<Integer, Set<Integer>> stopToBus = new HashMap<>();

        Map<Integer, Set<Integer>> busToStop = new HashMap<>();

        for (int bus = 0; bus < routes.length; bus++) {

            for (int stop : routes[bus]) {

                stopToBus.computeIfAbsent(stop, k -> new HashSet<>()).add(bus);

                busToStop.computeIfAbsent(bus, k -> new HashSet<>()).add(stop);
            }
        }

        if (!stopToBus.containsKey(source) || !stopToBus.containsKey(target)) return -1;

        Queue<Integer> q = new ArrayDeque<>();

        Set<Integer> visitedStops = new HashSet<>();

        Set<Integer> visitedBuses = new HashSet<>();

        q.offer(source);

        visitedStops.add(source);

        int busesTaken = 0;

        while (!q.isEmpty()) {

            int size = q.size();

            busesTaken++;

            for (int i = 0; i < size; i++) {

                int curr = q.poll();

                for (int bus : stopToBus.get(curr)) {

                    if (!visitedBuses.add(bus)) continue;

                    for (int stop : busToStop.get(bus)) {

                        if (stop == target) return busesTaken;

                        if (visitedStops.add(stop)) q.offer(stop);
                    }
                }
            }
        }

        return -1;
    }


    /*
     ===========================================================
     21. MINIMUM HEIGHT TREES
     ===========================================================
     */
    static List<Integer> findMinHeightTrees(int n, int[][] edges) {
        int left = n;
        Map<Integer, Set<Integer>> graph = new HashMap<>();
        for (var edge : edges) {
            graph.computeIfAbsent(edge[0], k -> new HashSet<>()).add(edge[1]);
            graph.computeIfAbsent(edge[1], k -> new HashSet<>()).add(edge[0]);
        }
        Queue<Integer> leaves = new ArrayDeque<>();
        for (var node : graph.keySet()) {
            if (graph.get(node).size() == 1) {
                leaves.offer(node);
            }
        }
        if (n == 1) {
            return List.of(0);
        }
        while (left > 2) {
            int size = leaves.size();
            left = left - size;
            for (int i = 0; i < size; i++) {
                var curr = leaves.poll();
                for (var nei : graph.get(curr)) {
                    graph.get(nei).remove(curr);
                    if(graph.get(nei).size() == 1) {
                        leaves.offer(nei);
                    }
                }
            }
        }

        return new ArrayList<>(leaves);
    }


    /*
     ===========================================================
     22. RANDOM PICK WITH WEIGHT
     ===========================================================
     */
    static class Solution {

        public Solution(int[] w) {

        }

        public int pickIndex() {

            return -1;
        }
    }
}