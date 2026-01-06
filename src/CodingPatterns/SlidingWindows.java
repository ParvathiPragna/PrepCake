package CodingPatterns;


import java.util.*;

public class SlidingWindows {

    public static void main(String[] args) {
        //
        // Call maxSumSubarray
        SlidingWindows swe = new SlidingWindows();
        int[] arr1 = {2, 1, 5, 1, 3, 2};
        int k1 = 3;
        int maxSum = swe.maxSumSubarray(arr1, k1);
        System.out.println("Max sum subarray of size " + k1 + " = " + maxSum);

        // Call firstNegativeInWindow
        int[] arr2 = {12, -1, -7, 8, -15, 30, 16, 28};
        int k2 = 3;
        List<Integer> negatives = swe.firstNegativeInWindow(arr2, k2);
        System.out.println("First negatives in each window: " + negatives);

        // 3. Longest Substring Without Repeating characters
        System.out.println("Longest Unique Substring: " + swe.lengthOfLongestSubstring("abcabcbbxyzw"));

        // 4. minimum window where are characters of p are in s r including duplicates char.
        System.out.println("Minimum window for p in s: " + swe.minWindow("abcabcbbxyzw", "bcbz"));
        System.out.println("Minimum window for p in s: " + swe.minWindow("abcabcbbxyzw", "bcb"));
        System.out.println("Minimum window for p in s: " + swe.minWindow("abcabcbbxyzw", "bcbp"));


    }

    public int minWindow(String s, String t) {
        // find the window & measure it then move it.
        if (t.length() > s.length()) {
            return -1;
        }
        int minLen = Integer.MAX_VALUE;
        int[] counter = new int[26];
        int[] counterS = new int[26]; // curr window counter

        char[] p = t.toCharArray();
        char[] arr = s.toCharArray();
        for (int i = 0; i < p.length; i++) {
            counter[p[i] - 'a']++;
        }
        int left = 0;
        int right = 0;
        int c = 0;
        while (right < arr.length) {
            counterS[arr[right] - 'a']++;
            if (counter[arr[right] - 'a'] > 0 && counterS[arr[right] - 'a'] <= counter[arr[right] - 'a']) {
                c++;
            }
            // all char found
            if (c == p.length) {
                // reduce the window size
                while (counter[arr[left] - 'a'] == 0 || counterS[arr[left] - 'a'] > counter[arr[left] - 'a']) {
                    if (counterS[arr[left] - 'a'] > counter[arr[left] - 'a']) {
                        counterS[arr[left] - 'a']--;
                    }
                    left++;
                }
                minLen = Math.min(minLen, right - left + 1);
            }
            right++;

        }
        if(minLen == Integer.MAX_VALUE) {
            return  -1;
        }
        return minLen;

    }

    private int lengthOfLongestSubstring(String s) {
        int max = 0;
        int[] arr = new int[26];
        Arrays.fill(arr, -1);
        char[] ch = s.toCharArray();
        int i, j;
        i = 0;
        j = 0;
        while (j < ch.length) {
            if (arr[ch[j] - 'a'] != -1) {
                // measure the window
                max = Math.max(max, j - i);
                //reset the window
                i = arr[ch[j] - 'a'];
                i++;
            }
            arr[ch[j] - 'a'] = j;
            j++;
        }
        max = Math.max(max, ch.length - i);
        return max;
    }

    private int maxSumSubarray(int[] arr, int k) {
        int i = 0;
        int j = 0;
        int sum = 0;
        int msum;
        // get window of size k
        for (; j < k; j++) {
            sum = sum + arr[j];
        }
        // i =0; j= k-1 wsize= k
        // slide the window from i
        msum = sum;

        while (j < arr.length) {
            sum = sum - arr[i++] + arr[j++];

            msum = Math.max(msum, sum);
        }
        return msum;
    }

    private List<Integer> firstNegativeInWindow(int[] arr, int k) {
        int i = 0;
        int j = k - 1;
        int prev = searchfirstNegative(arr, i, j);
        List<Integer> result = new ArrayList<>();
        result.add(arr[prev]);
        while (j < arr.length - 1) {
            i++;
            j++;
            if (prev < i) {

                prev = searchfirstNegative(arr, i, j);
            }
            if (prev != -1) {
                result.add(arr[prev]);
            } else {
                result.add(0);
            }

        }
        return result;
    }

    private int searchfirstNegative(int[] arr, int i, int j) {
        for (int z = i; z <= j; z++) {
            if (arr[z] < 0) {
                return z;
            }
        }
        return -1;
    }


}
