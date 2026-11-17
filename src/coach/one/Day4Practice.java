package coach.one;

import java.util.*;

public class Day4Practice {

    public static void main(String[] args) {

        /*
         ===========================================================
         16. LOWEST COMMON ANCESTOR OF A BINARY TREE

         Given a binary tree and two nodes p and q,
         find their lowest common ancestor.

               3
             /   \
            5     1
           / \   / \
          6  2  0  8
            / \
           7   4

         LCA(5,1) = 3
         LCA(5,4) = 5
         ===========================================================
         */

        TreeNode root = new TreeNode(3);

        root.left = new TreeNode(5);
        root.right = new TreeNode(1);

        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);

        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);

        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);

        System.out.println(lowestCommonAncestor(root, root.left, root.right).val); // 3


        /*
         ===========================================================
         17. SERIALIZE AND DESERIALIZE BINARY TREE

         Convert tree -> string
         Convert string -> tree

         Example:

              1
             / \
            2   3
               / \
              4   5

         ===========================================================
         */


        String serialized = serialize(root);

        System.out.println(serialized);

        TreeNode rebuilt = deserialize(serialized);

         System.out.println(rebuilt.val);


        /*
         ===========================================================
         18. KTH LARGEST ELEMENT

         Input:
         [3,2,1,5,6,4]
         k=2

         Output:
         5

         Pattern:
         Heap / QuickSelect
         ===========================================================
         */

        int[] nums1 = {3, 2, 1, 5, 6, 4};
        System.out.println(findKthLargest(nums1, 2)); // 5

        System.out.println(findKthLargest(nums1, 2)); // 5


    }

    private static int findKthLargest(int[] nums1, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.naturalOrder()); //1,2,3,4,5,6
        for (int i = 0; i < nums1.length; i++) {
            if (pq.size() > k && pq.peek() < nums1[i]) {
                pq.poll();
            }
            pq.offer(nums1[i]);
        }
        while (pq.size() > k) {
            pq.poll();
        }
        return pq.peek();
    }

    private static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        return lcaUtil(root, p, q);
    }

    static TreeNode lcaUtil(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        var left = lowestCommonAncestor(root.left, p, q);
        var right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) {
            return root;
        } else if (left != null) {
            return left;
        } else {
            return right;
        }
    }

    private static TreeNode deserialize(String serialized) {
        String[] parts = serialized.split(",");
        Queue<String> q = new ArrayDeque<>(Arrays.asList(parts));
        return deserializeUtil(q);
    }

    private static TreeNode deserializeUtil(Queue<String> q) {
        var curr = q.poll();
        if (curr.equals("#")) {
            return null;
        }
        TreeNode node = new TreeNode(Integer.parseInt(curr));
        node.left = deserializeUtil(q);

        node.right = deserializeUtil(q);

        return node;
    }

    private static String serialize(TreeNode root) { //LVR
        StringBuilder sb = new StringBuilder();
        serializeUtil(root, sb);
        return sb.toString();
    }

    private static void serializeUtil(TreeNode root, StringBuilder sb) {
        if (root == null) {
            sb.append("#,");
            return;
        }

        sb.append(root.val).append(",");
        serializeUtil(root.left, sb);
        serializeUtil(root.right, sb);
    }
}

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}
