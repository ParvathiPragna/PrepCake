import java.util.*;

public class ArraysP {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        int[] arr = {10, 2, -2, -20, 10};
        int k = -10;
        System.out.println(findCountSubArraySumK(arr, k));
        int[] arr1 = {5, 5, 4, 6, 4};
        ArrayList<Integer> ans = sortByFreq(arr1);
        for (int i = 0; i < ans.size(); i++) {
            System.out.print(ans.get(i) + " ");
        }
        System.out.println("$$$");

        findMedian(new int[]{5, 15, 1, 3, 2, 8});

        //max 1 Transaction  allowed
        System.out.println("*");
        int[] prices = {7, 10, 1, 3, 6, 9, 2};
        System.out.println(maxProfit(prices));

        int[] prices1 = {100, 180, 260, 310, 40, 535, 695};
        System.out.println(maxProfitMulti(prices1));

        System.out.println("**");
        int[] array = {2, 3, -8, 7, -1, 2, 3};
        System.out.println(maxSubarraySum(array));


        int[] prices2 = {10, 22, 5, 80};
        System.out.println("max profit when 2 trans allowed: " + maxProfitKTrans(prices2, 2));
    }

    public static int maxSubarraySum(int[] array) {
        int current = array[0];
        int max = array[0]; //kadane algo
        for (int i = 1; i < array.length; i++) {
            current = Math.max(current + array[i], array[i]); // start a new array by adding to current or start new
            max = Math.max(max, current);
        }
        return max;
    }

    /*
    The idea is that profit only comes when prices rise.
    If the price goes up from one day to the next,
    we can think of it as buying yesterday and selling today.
    Instead of waiting for the exact bottom and top,
    we simply grab every small upward move.
    Adding these small gains together is the same as if we had bought at each valley and
    sold at each peak because every rise between them gets counted.
     */
    public static int maxProfitMulti(int[] prices) {
        int res = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                res += prices[i] - prices[i - 1];
            }
        }
        return res;
    }

    /*
    for single transaction case
     */
    public static int maxProfit(int[] prices) {
        // in order to sell you need to buy first lowest price before this element & maximize it
        int low = prices[0];
        int max = 0;
        for (int i = 1; i < prices.length; i++) {
            low = Math.min(low, prices[i]);
            max = Math.max(max, prices[i] - low);
        }
        return max;
    }

    /**
     * max profit buy & sell at most k times
     * last day to first
     * top down approch
     */

    public static int maxProfitKTrans(int[] prices, int k) {
        int n = prices.length;
        if (n == 0 || k == 0) {
            return 0;
        }
        int[][][] dp = new int[prices.length + 1][k + 1][2]; //dp -> every day * k transcations (0 to k) * buy/sell
        for (int i = n - 1; i >= 0; i--) {
            for (int j = 0; j <= k; j++) {
                if(j==0) {
                    dp[i][j][0] = 0;
                    dp[i][j][1] = 0;
                }
                //for buy : max profit for ith day is profit of next day - today's prices & skip and let pass buy transaction to i+1 day
                dp[i][j][0] = Math.max(dp[i + 1][j][1] - prices[i], dp[i + 1][j][0]);
                //for sale : max profit for sale is profit of next day with j-1 transactions + today's sale amount or skip sell & let it pass
                dp[i][j][1] = Math.max(dp[i + 1][j - 1][0] + prices[i], dp[i + 1][j][1]);
            }
        }
// 0th day with k transactions in buy state is max profit

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                System.out.print(" " + dp[i][j][0] + " ");
            }
            System.out.println();
        }

        System.out.println("kk");

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= k; j++) {
                System.out.print(" " + dp[i][j][1] + " ");
            }
            System.out.println();
        }
        return dp[0][k][0];
    }

    public static void findMedian(int arr[]) {
        int n = arr.length;

        PriorityQueue<Integer> lowerHalf = new PriorityQueue<>(Comparator.reverseOrder());

        PriorityQueue<Integer> higherHalf = new PriorityQueue<>();

        double median;
        for (int size = 1; size <= n; size++) {
            lowerHalf.add(arr[size - 1]);
            higherHalf.add(lowerHalf.poll());
            if (higherHalf.size() > lowerHalf.size()) {
                lowerHalf.add(higherHalf.poll());
            }
            if (size % 2 == 1) {
                if (higherHalf.size() > lowerHalf.size()) {
                    median = higherHalf.peek();
                } else {
                    median = lowerHalf.peek();
                }
            } else {
                median = (lowerHalf.peek() + higherHalf.peek()) / 2;
            }
            System.out.print(median + " ");
        }
    }

    public static ArrayList<Integer> sortByFreq(int[] arr) {
        //hashMap for count then pq for sort
        HashMap<Integer, Integer> countMap = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            countMap.put(arr[i], countMap.getOrDefault(arr[i], 0) + 1);
        }

        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>((o1, o2) -> o2.getValue() - o1.getValue());
        pq.addAll(countMap.entrySet());
        ArrayList<Integer> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            var entry = pq.poll();
            for (int i = 0; i < entry.getValue(); i++) {
                result.add(entry.getKey());
            }
        }
        return result;
    }


    public static int findCountSubArraySumK(int[] arr, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        int csum = 0;
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            csum += arr[i];
            if (csum == k) { //0-i
                count++;
            }
            if (map.containsKey(csum - k)) {
                count = count + map.get(csum - k);
            }
            map.put(csum, map.getOrDefault(csum, 0) + 1);
        }

        return count;
    }
}
