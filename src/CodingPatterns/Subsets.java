package CodingPatterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Subsets {
    public static void main(String[] args) {
        Integer[] nums = {1, 2, 3};
        List<List<Integer>> subsets = getAllSubsets1(nums);
        System.out.println("All subsets: " + subsets);

        // subset with duplicates
        System.out.println("All subsets handle duplicates: " + getAllSubsetDuplicateInput(Arrays.asList(1, 2, 3, 2, 4)));

        // Combination sum - take any number of copies of element but sum should be equal to target value , print the set of elements sum =target
        System.out.println("sets of combination sum for a given array: " + combinationSum(new int[]{2, 3, 6, 4, 7}, 7));

        // permutation of elements given in an array
        System.out.println("permuatation of array elements:" + permutation(new int[]{1, 2, 3, 4}));

        //Given a string s, you can transform every letter individually to be lowercase or uppercase to create another string.
        System.out.println("permuatation of string in all cases for characters:" + letterCasePermutation("a1b3"));
        System.out.println("permuatation of string in all cases for characters:" + letterCasePermutation("5a1b3"));

        //generate balanced parentheses
        ArrayList<String> res = generateParentheses(6);

        for (String seq : res) {
            System.out.println(seq);
        }
        // generate genaralAbb note: digits should not be adjacent
        //"BAT", "BA1", "B1T", "B2", "1AT", "1A1", "2T", "3"
        System.out.println("general abbveriations : " + generateGeneralizedAbbreviation("BAT"));

        // rat in maze
        int[][] m = {{1, 0, 0, 0, 0},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 0, 1},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 0, 1}};
        int n = m.length;
        List<String> ans = ratMaze(m, n);
        for (String i : ans) {
            System.out.println(i + " ");
        }

        int[][] result = knightTour(5);

        for (int[] row : result) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }

    private static int[][] knightTour(int n) {
        int[][] board = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(board[i], -1);

        }
        board[0][0] = 0;
        int[] dx = new int[]{2, 1, -1, -2, 2, 1, -1, -2};
        int[] dy = new int[]{1, 2, -2, 1, -1, -2, 2, -1};

        if (knightTourUtil(board, n, 1, 0, 0, dx, dy)){
            return board;
        }
            return new int[0][];
    }

    private static boolean knightTourUtil(int[][] board, int n, int step, int x, int y, int[] dx, int[] dy) {
        if (step == n * n) {
            return true;
        }

        for (int i = 0; i < 8; i++) {
            var p = x + dx[i];
            var q = y + dy[i];

            if (p >= 0 && p < n && q >= 0 && q < n && board[p][q] == -1) {
                board[p][q] = step;
                if (knightTourUtil(board, n, step + 1, p, q, dx, dy)) {
                    return true;
                }
                board[p][q] = -1;
            }
        }
        return false;
    }

    /*
     Consider a rat placed at (0, 0) in a square matrix m[ ][ ] of order n and
     has to reach the destination at (n-1, n-1).
     The task is to find a sorted array of strings denoting all the possible directions
     which the rat can take to reach the destination at (n-1, n-1).
     The directions in which the rat can move are
     'U'(up), 'D'(down), 'L' (left), 'R' (right).
     */
    private static List<String> ratMaze(int[][] m, int n) {
        List<String> results = new ArrayList<>();
        boolean[][] visit = new boolean[n][n];
        backtrackRatPath(m, results, n, new StringBuilder(), new int[]{0, 0}, visit);
        return results;
    }

    private static void backtrackRatPath(int[][] mat, List<String> results, int n, StringBuilder temp, int[] start, boolean[][] visit) {
        if (start[0] == n - 1 && start[1] == n - 1) {
            results.add(temp.toString());
            return;
        }
        // explore paths
        int x = start[0];
        int y = start[1];
        int len = temp.length();
        if (x + 1 < n && mat[x + 1][y] == 1 && !visit[x + 1][y]) {
            temp.append('D');
            visit[x + 1][y] = true;
            backtrackRatPath(mat, results, n, temp, new int[]{x + 1, y}, visit);
            temp.setLength(len);
            visit[x + 1][y] = false;

        }
        if (x - 1 > 0 && mat[x - 1][y] == 1 && !visit[x - 1][y]) {
            temp.append('U');
            visit[x - 1][y] = true;
            backtrackRatPath(mat, results, n, temp, new int[]{x - 1, y}, visit);
            temp.setLength(len);
            visit[x - 1][y] = false;
        }
        if (y + 1 < n && mat[x][y + 1] == 1 && !visit[x][y + 1]) {
            temp.append('R');
            visit[x][y + 1] = true;
            backtrackRatPath(mat, results, n, temp, new int[]{x, y + 1}, visit);
            temp.setLength(len);
            visit[x][y + 1] = false;
        }
        if (y - 1 > 0 && mat[x][y - 1] == 1 && !visit[x][y - 1]) {
            temp.append('L');
            visit[x][y - 1] = true;
            backtrackRatPath(mat, results, n, temp, new int[]{x, y - 1}, visit);
            visit[x][y - 1] = false;
            temp.setLength(len);
        }

    }

    private static List<List<Integer>> getAllSubsets1(Integer[] nums) {
        List<List<Integer>> results = new ArrayList<>();
        backtrackSubsets(nums, results, new ArrayList<>(), 0);
        return results;
    }

    private static void backtrackSubsets(Integer[] nums, List<List<Integer>> results, ArrayList<Integer> temp, int start) {
        results.add(new ArrayList<>(temp));
        for (int i = start; i < nums.length; i++) {
            temp.add(nums[i]);
            backtrackSubsets(nums, results, temp, i + 1);
            temp.remove(temp.size() - 1);
        }
    }


    private static List<String> generateGeneralizedAbbreviation(String s) {
        // increase off set by counter or temp by adding char
        // if before run counter happened then add char only!!
        List<String> result = new ArrayList<String>();
        backtrackGeneralizedAbbreviation(s, result, new StringBuilder(), 0, 0);
        return result;
    }

    private static void backtrackGeneralizedAbbreviation(String s, List<String> result, StringBuilder temp, int offSet, int curr) {
        System.out.printf("backtrack call with inputs temp:%s, offset : %d with curr:%d%n", temp, offSet, curr);
        int len = temp.length();

        if (curr == s.length()) {
            if (offSet != 0) temp.append(offSet);
            result.add(temp.toString());
            temp.setLength(len);
            return;
        }
        backtrackGeneralizedAbbreviation(s, result, temp, offSet + 1, curr + 1);

        if (offSet != 0) temp.append(offSet);
        temp.append(s.charAt(curr));
        backtrackGeneralizedAbbreviation(s, result, temp, 0, curr + 1);


        temp.setLength(len);
    }

    private static List<String> letterCasePermutation(String s) {
        List<String> result = new ArrayList<String>();
        backtrackLetterCasePermutation(s.toLowerCase(), s.toUpperCase(), s.length(), new StringBuilder(), result, 0);
        return result;
    }

    private static void backtrackLetterCasePermutation(String lower, String upper, int length, StringBuilder tempBuilder, List<String> result, int start) {

        if (start == length) {
            result.add(tempBuilder.toString());
            return;
        }
        if (Character.isDigit(lower.charAt(start))) {
            tempBuilder.append(lower.charAt(start));
            backtrackLetterCasePermutation(lower, upper, length, tempBuilder, result, start + 1);
            tempBuilder.deleteCharAt(tempBuilder.length() - 1);
        } else {
            tempBuilder.append(lower.charAt(start));
            backtrackLetterCasePermutation(lower, upper, length, tempBuilder, result, start + 1);
            tempBuilder.deleteCharAt(tempBuilder.length() - 1);
            tempBuilder.append(upper.charAt(start));
            backtrackLetterCasePermutation(lower, upper, length, tempBuilder, result, start + 1);
            tempBuilder.deleteCharAt(tempBuilder.length() - 1);
        }
    }

    private static List<List<Integer>> permutation(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackPermutation(nums, result, new boolean[nums.length], new ArrayList<Integer>());
        return result;
    }

    private static void backtrackPermutation(int[] nums, List<List<Integer>> result, boolean[] used, ArrayList<Integer> temp) {
        if (temp.size() == nums.length) {
            result.add(new ArrayList<>(temp));
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            temp.add(nums[i]);
            backtrackPermutation(nums, result, used, temp);
            temp.remove(temp.size() - 1);
            used[i] = false;
        }
    }

    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(result, new ArrayList<>(), candidates, target, 0);
        return result;
    }

    private static void backtrack(List<List<Integer>> result, ArrayList<Integer> temp, int[] candidates, int remain, int start) {
        if (remain == 0) {
            result.add(new ArrayList<>(temp));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remain) continue;
            temp.add(candidates[i]);
            backtrack(result, temp, candidates, remain - candidates[i], i); // not i+1 but i; allowing multiple copies of same element
            temp.remove(temp.size() - 1);
        }
    }


    private static List<List<Integer>> getAllSubsetDuplicateInput(List<Integer> list) {
        list = list.stream().distinct().toList();
        Integer[] arr = list.toArray(new Integer[0]);
        Arrays.sort(arr);
        List<List<Integer>> sets = new ArrayList<>();
        generateAllSubsets(arr, new ArrayList<>(), sets, 0);

        return sets;
    }

    private static List<List<Integer>> getAllSubsets(Integer[] nums) {
        List<List<Integer>> sets = new ArrayList<>();
        generateAllSubsets(nums, new ArrayList<>(), sets, 0);
        return sets;
    }

    private static void generateAllSubsets(Integer[] nums, ArrayList<Integer> temp, List<List<Integer>> sets, int start) {
        sets.add(new ArrayList<>(temp)); // deep copy
        for (int i = start; i < nums.length; i++) {
            temp.add(nums[i]);
            generateAllSubsets(nums, temp, sets, i + 1);
            temp.remove(temp.size() - 1); // removes by index
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
