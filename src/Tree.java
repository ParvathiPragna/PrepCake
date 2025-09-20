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

        System.out.println(diameter(root));
        System.out.println(lca(root, 7, 8).data);
    }

    private static TreeNode lca(TreeNode root, int i, int i1) {
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

    private static int diameter(TreeNode root) {
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
