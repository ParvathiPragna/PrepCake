public class Tree {
    public static void main(String[] args) {
        // Constructed binary tree is
        //          5
        //        /   \
        //       8     6
        //      / \   /
        //     3   7 9
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(8);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(7);
        root.right.left = new TreeNode(9);

        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(-3);
        root2.left.left = new TreeNode(-2);
        System.out.println(diameter(root));
        System.out.println(lca(root, 7, 8).data);
        System.out.println(maxPathSum(root2));



        System.out.println(flipBinaryTree(root));
    }

    private static TreeNode flipBinaryTree(TreeNode root) {
        if(root == null || (root.left == null && root.right == null)) {
            return root;
        }
        TreeNode curr = root;
        TreeNode next = null;
        TreeNode prev = null;
        TreeNode ptr = null;


        while(curr != null) {
            next = curr.left; // next will be curr left beoming root
            curr.left = ptr; // assign sibling now
            ptr = curr.right; // store next runs sibling now
            curr.right = prev; // assign parent now
            prev = curr;
            curr = next;
        }
        return prev;
    }

    public static TreeNode lca(TreeNode root, int i, int i1) {
        if (root == null) {
            return null;
        }
        if (root.data == i || root.data == i1) {
            return root;
        }
        //check in left & check in right if both are non-null then lca

        TreeNode left = lca(root.left, i, i1);
        TreeNode right = lca(root.right, i, i1);
        if (right == null) {
            return left;
        }
        if (left == null) {
            return right;
        }
        return root;

    }

    public static int diameter(TreeNode root) {
        int[] res = new int[1];
        diameterRecc(root, res);
        return res[0];
    }

    private static int diameterRecc(TreeNode root, int[] res) {  // return heights & updates res with max dia
        if (root == null) {
            return 0;
        }
        int lh = diameterRecc(root.left, res);
        int rh = diameterRecc(root.right, res);
        res[0] = Math.max(res[0], lh + rh);
        return 1 + Math.max(lh, rh);
    }

    public static int maxPathSum(TreeNode root) {
        int[] res = new int[1];
        res[0] = -1001;
        res[0] = Math.max(maxPathSumUtil(root, res), res[0]);
        return res[0];
    }

    private static int maxPathSumUtil(TreeNode root, int[] res) {
        if (root == null) {
            return 0;
        }
        int lsum = maxPathSumUtil(root.left, res);
        int rsum = maxPathSumUtil(root.right, res);
        res[0] = Math.max(res[0], lsum + rsum + root.data);
        if (lsum > rsum) {
            return Math.max(lsum + root.data, root.data);
        }
        return Math.max(rsum + root.data, root.data);
    }
}

class TreeNode {
    int data;
    TreeNode left, right;

    TreeNode(int x) {
        data = x;
        left = null;
        right = null;
    }
}
