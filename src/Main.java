import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        int n = 6;
        ArrayList<String> res = generateParentheses(n);

        for (String seq : res) {
            System.out.println(seq);
        }


        char[][] grid = {{'L', 'L', 'W', 'W', 'W'},
                {'W', 'L', 'W', 'W', 'L'},
                {'L', 'W', 'W', 'L', 'L'},
                {'W', 'W', 'W', 'W', 'W'},
                {'L', 'W', 'L', 'L', 'W'}};

        System.out.println(countIslands(grid));

        int[] arr = {1, 4, 1, 4, 5, 2};
        int k = 3;
        System.out.println("**");
        System.out.println(countPairs(arr, k));
    }

    /**
     *
     * @param arr
     * @param k absolute diffrence
     * @return number of pairs where absolute difference between them is k
     */
    public static int countPairs(int[] arr, int k) {
        HashMap<Integer,Integer> fe = new HashMap<>();
        int count = 0;
        for (int i = 0; i< arr.length; i++) {
            if(fe.containsKey(arr[i] - k)) {
              count+=fe.get(arr[i]-k);
            }
            else if (fe.containsKey(arr[i] + k)) {
                count+=fe.get(arr[i]+k);
            }
            fe.put(arr[i],fe.getOrDefault(arr[i],0) + 1);
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

    public static ArrayList<String> generateParentheses(int n) {
        ArrayList<String> res = new ArrayList<>();
        validParentheses(n / 2, 0, "", res);
        return res;
    }

    private static void validParentheses(int n, int open, String curr, ArrayList<String> res) {
        //not to exceed given len
        if (curr.length() == 2 * n) {
            res.add(curr);
            return;
        }
        if (open < n) {  //in final string number  open == n ,so we can add till n
            validParentheses(n, open + 1, curr + "(", res);
        }
        if (curr.length() - open < open) { // same for close
            validParentheses(n, open, curr + ")", res);
        }
    }


}
