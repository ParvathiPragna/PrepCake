import java.util.Arrays;

public class dp {
    public static void main(String[] args) {
        int arr[] = {10, 22, 9, 33, 21, 50, 41, 60};
        System.out.println(lis(arr));

        int[] val = {1, 2, 3};
        int[] wt = {4, 5, 1};
        int W = 4;

        System.out.println(knapsack(W, val, wt));
    }

    private static int knapsack(int w, int[] val, int[] wt) {
        int[][] dp = new int[wt.length + 1][w + 1];
        for (int i = 0; i <= wt.length; i++) {
            for (int j = 0; j <= w; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else {
                    if (j > wt[i-1]) {
                        dp[i][j] = Math.max(dp[i - 1][j], val[i-1] + dp[i - 1][j - wt[i-1]]);
                    } else {
                        dp[i][j] = dp[i - 1][j];
                    }
                }
            }
        }

        return dp[wt.length][w];
    }

    private static int lis(int[] arr) {
        int[] li = new int[arr.length];
        Arrays.fill(li, 1);
        for (int i = 0; i < arr.length; i++) {
            for (int prev = 0; prev < i; prev++) {
                if (arr[prev] < arr[i]) {
                    li[i] = Math.max(li[i], li[prev] + 1);
                }
            }
        }
        int res = 1;
        for (int i = 0; i < arr.length; i++) {
            res = Math.max(res, li[i]);
        }
        return res;
    }


}
