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
        findMedian(new int[]{3, 4, 5, 6});

        //max 1 Transaction  allowed
        System.out.println("*");
        int[] prices = {7, 10, 1, 3, 6, 9, 2};
        System.out.println(maxProfit(prices));

        int[] prices1 = { 100, 180, 260, 310, 40, 535, 695 };
        System.out.println(maxProfitMulti(prices1));
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
        for(int i = 1; i<prices.length; i++) {
           if(prices[i] > prices[i-1]) {
               res = res+=prices[i]-prices[i-1];
           }
        }
        return res;
    }

    public static int maxProfit(int[] prices) {
        // in order to sell you need to buy first lowest price before this element & maximize it
        int low = prices[0];
        int max = 0;
        for(int i = 1; i<prices.length; i++) {
            low = Math.min(low, prices[i]);
            max = Math.max(max, prices[i] - low);
        }
        return max;
    }

    public static void findMedian(int arr[]) {
        int n = arr.length;

        PriorityQueue < Integer > lowerHalf = new PriorityQueue < > (Comparator.reverseOrder());

        PriorityQueue< Integer > higherHalf = new PriorityQueue < > ();

        int median;
        for (int size = 1; size <= n; size++) {
            if (!lowerHalf.isEmpty() && lowerHalf.peek() > arr[size - 1]) {
                lowerHalf.add(arr[size - 1]);

                if (lowerHalf.size() > higherHalf.size() + 1) {
                    higherHalf.add(lowerHalf.poll());
                }
            } else {
                higherHalf.add(arr[size - 1]);

                if (higherHalf.size() > lowerHalf.size() + 1) {
                    lowerHalf.add(higherHalf.poll());
                }
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

        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
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
        HashMap<Integer,Integer> map = new HashMap<>();
        int csum=0;
        int count=0;
        for(int i=0; i<arr.length; i++) {
            csum+=arr[i];
            if(csum == k) { //0-i
                count++;
            }
            if(map.containsKey(csum-k)) {
                count=count+map.get(csum-k);
            }
            map.put(csum,map.getOrDefault(csum,0)+1);
        }

        return count;
    }
}
