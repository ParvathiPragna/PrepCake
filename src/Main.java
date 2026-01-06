import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {



        char[][] grid =
                {{'L', 'L', 'W', 'W', 'W'},
                        {'W', 'L', 'W', 'W', 'L'},
                        {'L', 'W', 'W', 'L', 'L'},
                        {'W', 'W', 'W', 'W', 'W'},
                        {'L', 'W', 'L', 'L', 'W'}};

        System.out.println(countIslands(grid));


        System.out.println("****$$$");
        System.out.println(countNumberOfWaysForNSteps(4));

        int[] arr = {1, 4, 1, 4, 5, 2};
        int k = 3;
        System.out.println("**");
        System.out.println(countPairs(arr, k));
        System.out.println("*******");

        int[] arr2 = {2, 7, 6, 1, 4, 5};
        int k2 = 3;

        System.out.println(longestSubarrayDivK(arr2, k2));

        int[][] result = knightTour(8);

        for (int[] row : result) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }

    /*
    number steps in a way is either 1 or 2;
    count(n) = count(n-1) + count(n-2)
     */
    public static int countNumberOfWaysForNSteps(int n) {
        if (n <= 1) {
            return 1;
        }
        int prev1 = 1; // this for 0
        int prev2 = 1;// this for 1
        int curr = prev1 + prev2; // this for 2
        int i = 3;
        while (i <= n) {
            curr = prev1 + prev2;
            prev1 = curr;
            prev2 = prev1;
            i++;
        }
        return curr;
    }

    /*
    / Java Program to find the longest subarray with sum divisible by k by iterating over all subarrays
     */
    public static int longestSubarrayDivK(int[] arr, int k) {
        //prefix sum modulo k hashmap check

        int res = 0;
        int sum = 0;
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        //int rem;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
            sum = ((sum % k) + k) % k;  // to handle negative sums

            if (sum == 0) {
                res = i + 1;
            } else if (map.containsKey(sum)) {
                res = Math.max(res, i - map.get(sum));
            } else {
                map.put(sum, i);
            }
        }
        return res;
    }

    public static int[][] knightTour(int n) {
        int[][] res = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(res[i], -1);
        }

        int[] dx = {2, 1, -1, -2, -2, -1, 1, 2};
        int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};
        res[0][0] = 0;
        if (knightTourValidUtil(n, res, dx, dy, 0, 0, 1)) {
            return res;
        }
        return new int[0][];
    }

    private static boolean knightTourValidUtil(int n, int[][] res, int[] dx, int[] dy, int x, int y, int step) {
        if (step == n * n) {
            return true;
        }

        int nx, ny;
        for (int i = 0; i < 8; i++) {
            nx = x + dx[i];
            ny = y + dy[i];
            if (nx >= 0 && nx < n && ny >= 0 && ny < n && res[nx][ny] == -1) {
                res[nx][ny] = step;
                if (knightTourValidUtil(n, res, dx, dy, nx, ny, step + 1)) {
                    return true;
                }
                res[nx][ny] = -1;
            }
        }
        return false;
    }

    /**
     * @param arr
     * @param k   absolute diffrence
     * @return number of pairs where absolute difference between them is k
     */
    public static int countPairs(int[] arr, int k) {
        HashMap<Integer, Integer> fe = new HashMap<>();
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (fe.containsKey(arr[i] - k)) {
                count += fe.get(arr[i] - k);
            } else if (fe.containsKey(arr[i] + k)) {
                count += fe.get(arr[i] + k);
            }
            fe.put(arr[i], fe.getOrDefault(arr[i], 0) + 1);
        }
        return count;
    }

    public static int countIslands(char[][] grid) {
        int colors = 0;
        int m = grid.length; // rows
        int n = grid[0].length; // col

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 'L') {
                    //use color;
                    islandUtil(grid, colors, i, j, m, n);
                    colors++;
                }
            }
        }
        return colors;
    }

    private static void islandUtil(char[][] grid, int colors, int x, int y, int m, int n) {
        grid[x][y] = Character.forDigit(colors, 0);
        // directions
        int[] dx = {0, 1, -1, 0, 1, -1, 1, -1};
        int[] dy = {1, 0, 0, -1, -1, 1, 1, -1};

        for (int i = 0; i < 8; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < m && ny >= 0 && ny < n) {
                if (grid[nx][ny] == 'L') {
                    islandUtil(grid, colors, nx, ny, m, n);
                }
            }
        }
    }



}
