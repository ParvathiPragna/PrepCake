package CodingPatterns;

import java.util.*;

public class Graphs {
    /*
        TODO: add order of executions for all this algo.
     */
    public static void main(String[] args) {
        int V = 5;
        List<List<Integer>> adj = new ArrayList<>();
        List<List<Integer>> directedAdj = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
            directedAdj.add(new ArrayList<>());
        }
        adj.get(0).addAll(Arrays.asList(1, 2));
        adj.get(1).addAll(Arrays.asList(0, 3));
        adj.get(2).addAll(Arrays.asList(0, 4));
        adj.get(3).add(1);
        adj.get(4).add(2);


        //traversals
        System.out.print("BFS Traversal: " + bfs(V, adj, 0));
        System.out.print("DFS Traversal: " + dfs(V, adj, 0));

        //Application of BFS & DFS
        System.out.println("shortest distance from start node 0 to all others: " + bfsDistances(V, adj, 0));
        System.out.println("number of connect components : " + dfsCountConnectedParts(V, adj));

        //bipartite graph
        // 2 disjoint groups with every vertex of one group has an edge to another group
        // 2 way colours with no two nodes adjacent have same colour.
        System.out.println("check is the graph bipartite graph: " + isBipartiteGraph(V, adj));


        // detect cycles using DFS & BFS for undirected graph
        System.out.println("has a cycle check with BFS: " + hasCycleBFS(V, adj));
        System.out.println("has a cycle check with DFS: " + hasCycleDFS(V, adj));
        adj.get(3).add(4);
        adj.get(4).add(3);
        System.out.println("has a cycle check with BFS: " + hasCycleBFS(V, adj));
        System.out.println("has a cycle check with DFS: " + hasCycleDFS(V, adj));

        // detect cycle with DFS & BFS for directed graph
        directedAdj.get(4).add(1);
        directedAdj.get(1).add(2);
        directedAdj.get(2).add(3);
        directedAdj.get(3).addAll(Arrays.asList(0, 4));
        System.out.println("has a cycle check with BFS directed Graph: " + hasCycleBFSDirected(V, adj));

        //Weighted Graph (Non-Negative Weights → Dijkstra)
        int vertexs = 4;
        List<List<int[]>> weights = new ArrayList<>();
        for (int i = 0; i < vertexs; i++) {
            weights.add(new ArrayList<>());
        }
        weights.get(0).addAll(Arrays.asList(new int[]{1, 4}, new int[]{2, 1}));
        weights.get(1).add(new int[]{3, 1});
        weights.get(2).addAll(Arrays.asList(new int[]{1, 2}, new int[]{3, 5}));
        System.out.println("shortest distance from start node 0 to all others in a directed weighted graph: " + printArray(dijkstra(vertexs, weights, 0)));
        // negative weights for edges
        int[][] edges = new int[5][3];
        edges = new int[][]{new int[]{0, 1, 4}, new int[]{0, 2, 5}, new int[]{1, 2, -3}, new int[]{2, 3, 4}, new int[]{3, 1, -10}};
        bellmanFord(V, edges, 0);

        //MST - connect the cities  by roads with minimum weights
        List<Edge> edgeList = Arrays.asList(
                new Edge(0, 1, 10), // A-B
                new Edge(0, 2, 6),  // A-C
                new Edge(0, 3, 5),  // A-D
                new Edge(2, 3, 4)   // C-D
        );
        kruskalMST(V, edgeList);

        List<List<int[]>> adjList = new ArrayList<>();
        for (int i = 0; i < V; i++) adjList.add(new ArrayList<>());

        // Add edges (u, v, w)
        adjList.get(0).add(new int[]{1, 2}); // O1-O2
        adjList.get(0).add(new int[]{3, 6}); // O1-O4
        adjList.get(1).add(new int[]{0, 2});
        adjList.get(1).add(new int[]{2, 3});
        adjList.get(1).add(new int[]{3, 8});
        adjList.get(1).add(new int[]{4, 5});
        adjList.get(2).add(new int[]{1, 3});
        adjList.get(2).add(new int[]{4, 7});
        adjList.get(3).add(new int[]{0, 6});
        adjList.get(3).add(new int[]{1, 8});
        adjList.get(4).add(new int[]{1, 5});
        adjList.get(4).add(new int[]{2, 7});
        adjList.get(4).add(new int[]{3, 9});

        primMST(V, adjList);

    }

    /*
     add a self edge of weight 0 at the start and push  it the queue
     in loop: pick node from queue if node is un-visited then mark it visited &
      take all edges connected to the node & add them where the other vertex is not visited
      loops: vertex using priority queue
     */
    private static void primMST(int v, List<List<int[]>> adj) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1])); // sort based on weight
        boolean[] visit = new  boolean[v];
        pq.add(new int[]{0, 0});
        int totalWeight = 0;
        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            if(visit[curr[0]]) continue;
            visit[curr[0]] = true;
            totalWeight = totalWeight + curr[1];
            System.out.printf("node %d with weight %d is included%n",curr[0],curr[1]);
            for(int[] edge: adj.get(curr[0])) {
                if(!visit[edge[0]]) {
                    pq.add(new int[]{edge[0], edge[1]});
                }
            }
        }
        System.out.println("total cost for connecting all nodes " + totalWeight);

    }

    /*
        Union find takes care no cycle formation
        Algo: sort edges and keeping adding edges whenever it is connecting new un-visted vertex
        loops sorted edges
     */
    private static void kruskalMST(int v, List<Edge> edges) {
        UnionFind uf = new UnionFind(v);
        int totalWeight = 0;
        edges.sort(Comparator.comparingInt(Edge -> Edge.w)); // sort according to weights
        for (Edge e : edges) {
            if (uf.find(e.u) != uf.find(e.v)) {
                uf.union(e.u, e.v);
                totalWeight = totalWeight + e.w;
                System.out.printf("Road chosen %d - %d weight - %d %n", e.u, e.v, e.w);
            }
        }
        System.out.println("total weight is :" + totalWeight);
    }

    static class Edge {
        int u, v, w;

        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    static class UnionFind {
        int[] parent;

        UnionFind(int n) {
            parent = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int x, int y) {
            parent[find(x)] = find(y);
        }
    }

    private static void bellmanFord(int v, int[][] edges, int start) {
        int[] dist = new int[v];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        // loop for v-1 times
        for (int i = 1; i < v; i++) {
            for (int[] edge : edges) {
                int u = edge[0];
                int q = edge[1];
                int w = edge[2];
                if (dist[u] != Integer.MAX_VALUE && dist[q] > dist[u] + w) {
                    dist[q] = dist[u] + w;
                }
            }
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int q = edge[1];
            int w = edge[2];
            if (dist[u] != Integer.MAX_VALUE && dist[q] > dist[u] + w) {
                System.out.println("negative cycle detected");
                return;
            }
        }

        System.out.println(printArray(dist));

    }


    private static String printArray(int[] dijkstra) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dijkstra.length; i++) {
            builder.append(dijkstra[i]).append(" ");
        }
        return builder.toString();
    }

    /*
        dijkstra algo : for non-negative weighted graph
        before marking a node visited you evaluate if distance to source for its neighbours can be optimised via current node
        if yes you update the distance for the neighbours then mark it as visited then move on to next node
        O(E+VlogV) - on  heap
     */
    private static int[] dijkstra(int v, List<List<int[]>> adj, int start) {
        int[] dist = new int[v];
        // boolean[] visit = new boolean[v];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{start, 0});
        while (!pq.isEmpty()) {
            int[] ele = pq.poll();
            int vertex = ele[0];
            int d = ele[1];
            if (d > dist[vertex]) continue; // this entry can be ignored!
            for (int[] a : adj.get(ele[0])) {
                if (dist[a[0]] > dist[vertex] + a[1]) {
                    dist[a[0]] = dist[vertex] + a[1];
                    pq.add(new int[]{a[0], dist[a[0]]});
                }
            }
        }
        return dist;
    }

    // using colour theory BFS
    private static boolean isBipartiteGraph(int v, List<List<Integer>> adj) {
        int[] colours = new int[v];
        Arrays.fill(colours, -1);
        for (int start = 0; start < v; start++) {
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            if (colours[start] == -1) {
                colours[start] = 1;
                while (!queue.isEmpty()) {
                    int curr = queue.poll();
                    for (var neb : adj.get(curr)) {
                        if (colours[neb] == -1) {
                            colours[neb] = 1 - colours[curr];
                            queue.add(neb);
                        } else if (colours[curr] == colours[neb]) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /*
     * HINT topological sort -> courses problem
     * directed BFS can use topological sort
     * concept of in-degrees -> incoming edges towards node
     * no cycle -> on topological sort count of in-degrees == 0
     * else cycle exist
     * on doing a bFS if cycle exist on removing edges step by step BFS still there wil some node where you cant remove a edge
     * impossible course completion problem!!
     *
     * */
    private static boolean hasCycleBFSDirected(int v, List<List<Integer>> adj) {
        int[] indegrees = new int[v];
        Arrays.fill(indegrees, 0);
        for (int i = 0; i < adj.size(); i++) {
            for (var edge : adj.get(i)) {
                indegrees[edge]++;
            }
        }
        Queue<Integer> queue = new ArrayDeque<>();

        int count = 0;
        for (int i = 0; i < v; i++) {
            if (indegrees[i] == 0) {
                queue.add(i);
            }
        }
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            for (int neb : adj.get(curr)) {
                indegrees[neb]--;
                if (indegrees[neb] == 0) {
                    queue.add(neb);
                    count++;
                }
            }
        }
        return !(count == v);
    }

    private static boolean hasCycleDFS(int v, List<List<Integer>> adj) {
        boolean[] visited = new boolean[v];
        for (int i = 0; i < v; i++) {
            if (!visited[i]) {
                if (dfscycleUtil(adj, visited, i, -1)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean dfscycleUtil(List<List<Integer>> adj, boolean[] visited, int node, int parent) {
        visited[node] = true;
        for (int neighbour : adj.get(node)) {
            if (!visited[neighbour]) {
                if (dfscycleUtil(adj, visited, neighbour, node)) {
                    return true;
                }
            } else if (neighbour != parent) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasCycleBFS(int v, List<List<Integer>> adj) {
        // on doing layer by layer traversal if you reach parent then cycle exist
        // note not all nodes need to be part of cycle so check for all

        boolean[] visited = new boolean[v];
        for (int i = 0; i < v; i++) {
            if (!visited[i]) {
                visited[i] = true;
                Queue<int[]> queue = new ArrayDeque<>();
                queue.add(new int[]{i, -1}); // stating no parent

                while (!queue.isEmpty()) {
                    int[] curr = queue.poll();
                    for (var neighbour : adj.get(curr[0])) {
                        if (!visited[neighbour]) {
                            visited[neighbour] = true;
                            queue.add(new int[]{neighbour, curr[0]});
                        } else if (neighbour != curr[1]) {
                            return true;
                            // cycle found already neighbour is visited from some other direction other than incoming direction parent.
                        }
                    }
                }
            }
        }
        return false;
    }

    private static int dfsCountConnectedParts(int v, List<List<Integer>> adj) {
        int count = 0;
        boolean[] visit = new boolean[v];
        for (int i = 0; i < v; i++) {
            visit[i] = true;
            for (int c : adj.get(i)) {
                if (!visit[c]) {
                    dfsUtil(adj, visit, c);
                    count++;
                }
            }
        }
        return count;
    }

    private static List<Integer> bfsDistances(int v, List<List<Integer>> adj, int start) {
        Integer[] dist = new Integer[v];
        Arrays.fill(dist, -1);
        Queue<Integer> queue = new ArrayDeque<>();

        queue.add(start);
        dist[start] = 0;

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            for (var c : adj.get(curr)) {
                if (dist[c] == -1) {
                    dist[c] = dist[curr] + 1;
                    queue.add(c);
                }
            }
        }


        List<Integer> results = new ArrayList<>();

        Collections.addAll(results, dist);
        return results;
    }

    private static List<Integer> dfs(int v, List<List<Integer>> adj, int i) {

        return dfsUtil(adj, new boolean[v], i);
    }

    /*
     * DFS - depth first explore each neighbour (follow till no extended neighbours left)completely then move on to next one
     * */
    private static List<Integer> dfsUtil(List<List<Integer>> adj, boolean[] visit, int start) {
        List<Integer> result = new ArrayList<>();
        result.add(start);
        visit[start] = true;
        for (Integer e : adj.get(start)) {
            if (!visit[e]) {
                result.addAll(dfsUtil(adj, visit, e));
            }
        }
        return result;
    }

    /*
     * BFS - breath first traversal - check all neighbours
     *  then move forward followed by their neighbours
     * */
    private static List<Integer> bfs(int v, List<List<Integer>> adj, int i) {
        List<Integer> result = new ArrayList<>(v);
        Queue<Integer> queue = new ArrayDeque<>();
        boolean[] visit = new boolean[v];
        queue.add(i);
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            visit[curr] = true;
            result.add(curr);
            for (int item : adj.get(curr)) {
                if (!visit[item]) {
                    queue.add(item);
                }
            }

        }
        return result;
    }


}
