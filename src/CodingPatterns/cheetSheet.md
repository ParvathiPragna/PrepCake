# 📘 Master DSA Cheat Sheet (All Patterns in One File)

```java
import java.util.*;

// ===================== TWO POINTERS =====================
class TwoPointers {
    public static int[] findPair(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int sum = arr[left] + arr[right];
            if (sum == target) return new int[]{left, right};
            else if (sum < target) left++;
            else right--;
        }
        return new int[]{-1, -1};
    }
    public static void main(String[] args) {
        int[] arr = {1,2,3,4,6};
        System.out.println(Arrays.toString(findPair(arr,6))); // [1,3]
    }
}

// ===================== SLIDING WINDOW =====================
class SlidingWindow {
    public static int maxSubarraySum(int[] arr, int k) {
        int sum = 0, maxSum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
            if (i >= k) sum -= arr[i-k];
            if (i >= k-1) maxSum = Math.max(maxSum, sum);
        }
        return maxSum;
    }
    public static void main(String[] args) {
        int[] arr = {2,1,5,1,3,2};
        System.out.println(maxSubarraySum(arr,3)); // 9
    }
}

// ===================== FAST & SLOW POINTERS =====================
class ListNode { int val; ListNode next; ListNode(int x){val=x;} }
class CycleDetection {
    public static boolean hasCycle(ListNode head) {
        ListNode slow=head, fast=head;
        while(fast!=null && fast.next!=null){
            slow=slow.next; fast=fast.next.next;
            if(slow==fast) return true;
        }
        return false;
    }
    public static void main(String[] args){
        ListNode n1=new ListNode(1); ListNode n2=new ListNode(2); ListNode n3=new ListNode(3);
        n1.next=n2; n2.next=n3; n3.next=n1;
        System.out.println(hasCycle(n1)); // true
    }
}

// ===================== BINARY SEARCH =====================
class BinarySearch {
    public static int search(int[] arr,int target){
        int l=0,r=arr.length-1;
        while(l<=r){
            int mid=l+(r-l)/2;
            if(arr[mid]==target) return mid;
            else if(arr[mid]<target) l=mid+1;
            else r=mid-1;
        }
        return -1;
    }
    public static void main(String[] args){
        int[] arr={1,3,5,7,9};
        System.out.println(search(arr,7)); // 3
    }
}

// ===================== BFS =====================
class BFS {
    public static void bfs(Map<Integer,List<Integer>> graph,int start){
        Queue<Integer> q=new LinkedList<>(); Set<Integer> vis=new HashSet<>();
        q.add(start); vis.add(start);
        while(!q.isEmpty()){
            int node=q.poll(); System.out.print(node+" ");
            for(int nei:graph.getOrDefault(node,new ArrayList<>())){
                if(!vis.contains(nei)){ vis.add(nei); q.add(nei); }
            }
        }
    }
    public static void main(String[] args){
        Map<Integer,List<Integer>> g=new HashMap<>();
        g.put(0,Arrays.asList(1,2)); g.put(1,Arrays.asList(3)); g.put(2,Arrays.asList(4));
        bfs(g,0); // 0 1 2 3 4
    }
}

// ===================== DFS =====================
class DFS {
    public static void dfs(Map<Integer,List<Integer>> g,int node,Set<Integer> vis){
        if(vis.contains(node)) return; vis.add(node);
        System.out.print(node+" ");
        for(int nei:g.getOrDefault(node,new ArrayList<>())) dfs(g,nei,vis);
    }
    public static void main(String[] args){
        Map<Integer,List<Integer>> g=new HashMap<>();
        g.put(0,Arrays.asList(1,2)); g.put(1,Arrays.asList(3)); g.put(2,Arrays.asList(4));
        dfs(g,0,new HashSet<>()); // 0 1 3 2 4
    }
}

// ===================== DYNAMIC PROGRAMMING =====================
class DP {
    public static int fib(int n){
        int[] dp=new int[n+1]; dp[0]=0; dp[1]=1;
        for(int i=2;i<=n;i++) dp[i]=dp[i-1]+dp[i-2];
        return dp[n];
    }
    public static void main(String[] args){ System.out.println(fib(10)); } // 55
}

// ===================== BACKTRACKING =====================
class Backtracking {
    static List<List<Integer>> res=new ArrayList<>();
    public static void backtrack(List<Integer> path,boolean[] used,int[] nums){
        if(path.size()==nums.length){ res.add(new ArrayList<>(path)); return; }
        for(int i=0;i<nums.length;i++){
            if(used[i]) continue;
            used[i]=true; path.add(nums[i]);
            backtrack(path,used,nums);
            path.remove(path.size()-1); used[i]=false;
        }
    }
    public static void main(String[] args){
        int[] nums={1,2,3};
        backtrack(new ArrayList<>(),new boolean[nums.length],nums);
        System.out.println(res);
    }
}

// ===================== UNION-FIND =====================
class UnionFind {
    int[] parent,rank;
    public UnionFind(int n){ parent=new int[n]; rank=new int[n]; for(int i=0;i<n;i++) parent[i]=i; }
    public int find(int x){ if(parent[x]!=x) parent[x]=find(parent[x]); return parent[x]; }
    public void union(int a,int b){
        int pa=find(a),pb=find(b); if(pa==pb) return;
        if(rank[pa]<rank[pb]) parent[pa]=pb;
        else if(rank[pa]>rank[pb]) parent[pb]=pa;
        else{ parent[pb]=pa; rank[pa]++; }
    }
    public static void main(String[] args){
        UnionFind uf=new UnionFind(5); uf.union(0,1); uf.union(1,2);
        System.out.println(uf.find(2));
    }
}

// ===================== HEAP =====================
class Heap {
    public static List<Integer> kLargest(int[] arr,int k){
        PriorityQueue<Integer> pq=new PriorityQueue<>();
        for(int num:arr){ pq.add(num); if(pq.size()>k) pq.poll(); }
        return new ArrayList<>(pq);
    }
    public static void main(String[] args){
        int[] arr={3,2,1,5,6,4};
        System.out.println(kLargest(arr,2)); // [5,6]
    }
}

// ===================== TOPOLOGICAL SORT =====================
class TopologicalSort {
    public static List<Integer> topoSort(int n,int[][] edges){
        List<List<Integer>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>());
        int[] indegree=new int[n];
        for(int[] e:edges){ g.get(e[0]).add(e[1]); indegree[e[1]]++; }
        Queue<Integer> q=new LinkedList<>(); for(int i=0;i<n;i++) if(indegree[i]==0) q.add(i);
        List<Integer> order=new ArrayList<>();
        while(!q.isEmpty()){
            int node=q.poll(); order.add(node);
            for(int nei:g.get(node)){ if(--indegree[nei]==0) q.add(nei); }
        }
        return order;
    }
    public static void main(String[] args){
        int[][] edges={{0,1},{0,2},{1,3},{2,3}};
        System.out.println(topoSort(4,edges)); // [0,1,2,3]
    }
}

// ===================== DIJKSTRA =====================
class Dijkstra {
    public static int[] dijkstra(int n,int[][] edges,int src){
        List<List<int[]>> g=new ArrayList<>(); for(int i=0;i<n;i++) g.add(new ArrayList<>());
        for(int[] e:edges){ g.get(e[0]).add(new int[]{e[1],e[2]}); g.get(e[1]).add(new int[]{e[0],e[2]}); }
        int[] dist=new int[n]; Arrays.fill(dist,Integer.MAX_VALUE); dist[src]=0;
        PriorityQueue<int[]> pq=new PriorityQueue<>(Comparator.comparingInt(a->a[1]));
        pq.add(new int[]{src,0});
        while(!pq.isEmpty()){
            int[] cur=pq.poll(); int node=cur[0],d=cur[1]; if(d>dist[node]) continue;
            for(int[] nei:g.get(node)){
                int nd=d+nei[1];
                dist[node] = nd;
                pq.add(new int[]{node,nd});
            }
        }
    }
}
//==================Lowest Common Anscetor==================
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int x) { val = x; }
}

public class LCAExample {
    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);

        TreeNode lca = lowestCommonAncestor(root, root.left, root.right);
        System.out.println("LCA of 5 and 1: " + lca.val); // 3
    }
}

======================= diameter of tree ============================
public class DiameterTree {
    static int diameter = 0;

    public static int depth(TreeNode root) {
        if (root == null) return 0;
        int left = depth(root.left);
        int right = depth(root.right);
        diameter = Math.max(diameter, left + right);
        return Math.max(left, right) + 1;
    }

    public static int diameterOfBinaryTree(TreeNode root) {
        depth(root);
        return diameter;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        System.out.println("Diameter: " + diameterOfBinaryTree(root)); // 3
    }
}
================= Monotonic Stack ================
import java.util.*;

public class MonotonicStackExample {
    public static int[] nextGreater(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Arrays.fill(res, -1);
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
                res[stack.pop()] = nums[i];
            }
            stack.push(i);
        }
        return res;
    }

    public static void main(String[] args) {
        int[] nums = {2,1,2,4,3};
        System.out.println("Next Greater Elements: " + Arrays.toString(nextGreater(nums)));
    }
}


            
