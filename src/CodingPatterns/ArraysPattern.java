package CodingPatterns;

import java.util.Arrays;

public class ArraysPattern {
    public static void main(String[] args) {
        //Maximum Subarray Sum - Kadane's Algorithm
        int[] arr = {2, 3, -8, 7, -1, 2, 3};
        System.out.println("maxSubArraySum: " + maxSubarraySum(arr));

        //Minimize the maximum difference between the heights
        System.out.println("minimize the max diff between towers " + getMinDiff(new int[]{1, 5, 8, 10}, 2));

    }


    private static int maxSubarraySum(int[] arr) {
        int max, curr;
        max = arr[0];
        curr = arr[0];
        for (int i = 1; i < arr.length; i++) {
            curr = Math.max(curr + arr[i], arr[i]);
            max = Math.max(curr, max);
        }
        return max;
    }
    /*
    Given an array arr[] representing the heights of towers and a positive integer k,
     we can modify each tower’s height exactly once by either adding k or subtracting k. After these modifications, find the minimum possible difference between the tallest and the shortest tower.

    Note: It is compulsory to increase or decrease the height by k for each tower. After the operation,
    the resultant array should not contain any negative integers.
     */

    private static int getMinDiff(int[] arr, int k) {
        Arrays.sort(arr);
        int diff = arr[arr.length - 1] - arr[0];

        if (arr[arr.length - 1] < k) {
            return arr[arr.length - 1] - arr[0];
        }
        for (int i = 1; i < arr.length; i++) {

            // Impossible to decrement height of ith tower by k, continue

            if (arr[i] - k < 0) continue;


            int minH = Math.min(arr[0] + k, arr[i] - k);
            int maxH = Math.max(arr[i - 1] + k, arr[arr.length - 1] - k);
            diff = Math.min(diff, maxH - minH);
        }
        return diff;

    }
}
