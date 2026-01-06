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
