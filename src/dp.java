import java.util.Arrays;

public class dp {
    public static void main(String[] args) {
        int[] arr = {10, 22, 9, 33, 21, 50, 41, 60};
        System.out.println(lis(arr));

        int[] val = {1, 2, 3};
        int[] wt = {4, 5, 1};
        int W = 4;

        System.out.println(knapsack(W, val, wt));

        String s1 = "AGGTAB";
        String s2 = "GXTXAYB";

        System.out.println(lcs(s1, s2));
    }

    private static int lcs(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int [][] dp = new int[m+1][n+1];

        for(int i = 0; i<=m; i++) {
            for(int j = 0 ; j<=n; j++) {
                if(i==0 || j ==0) {
                    dp[i][j] = 0;
                }
                else if(s1.charAt(i-1) == s2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1] + 1;
                }
                else {
                    dp[i][j] = Math.max(dp[i-1][j],dp[i][j-1]);
                }
            }
        }
        return dp[m][n];
    }

    private static int knapsack(int w, int[] val, int[] wt) {
        int[][] dp = new int[wt.length + 1][w + 1];
        for (int i = 0; i <= wt.length; i++) {
            for (int j = 0; j <= w; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else {
                    if (j >= wt[i - 1]) {
                        dp[i][j] = Math.max(dp[i - 1][j], val[i - 1] + dp[i - 1][j - wt[i - 1]]);
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


    //practice
    private static int knapsackP1(int w, int[] val, int[] wt) {
        int n = wt.length;
        int[][] dp = new int[n + 1][w + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= w; j++) {
                if (i == 0 || j == 0)
                    dp[i][j] = 0;
                if (wt[i - 1] <= j)
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - wt[i]] + val[i - 1]);
                else
                    dp[i][j] = dp[i - 1][j];
            }
        }
        return dp[n][w];
    }

    // practice: longest increase seq
    private static int lisP1(int[] arr) {
        int n = arr.length;
        int[] lis = new int[n];
        Arrays.fill(lis, 1);
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (arr[i] > arr[j]) {
                    lis[i] = Math.max(lis[j] + 1, lis[i]);
                }
            }
        }

        int max = 1;
        for (int i = 0; i < n; i++) {
            max = Math.max(lis[i], max);
        }

        return max;
    }

    private static boolean partitionEqualSubsets(int[] arr) {
        int sum = 0;
        int n = arr.length;

        for (int i = 0; i < n; i++) {
            sum = sum + arr[i];
        }
        if (sum % 2 == 1) {
            return false;
        }
        sum = sum / 2;
        boolean[][] dp = new boolean[n + 1][sum + 1];
        dp[0][0] = true;

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j <= sum; j++) {
                if (j == 0) {
                    dp[i][j] = true; // empty subset
                }
                if (arr[i - 1] <= j) {
                    dp[i][j] = dp[i - 1][j] || dp[i - 1][j - arr[i - 1]];
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        return dp[n][sum];
    }
}
