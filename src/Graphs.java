import java.util.*;

public class Graphs {
    public static void main(String[] args) {
        int INF = 100000000;

        int[][] dist = {{0, 4, INF, 5, INF},
                {INF, 0, 1, INF, 6},
                {2, INF, 0, 3, INF},
                {INF, INF, 1, 0, 2},
                {1, INF, INF, 4, 0}};

        floydWarshall(dist);
        for (int i = 0; i < dist.length; i++) {
            for (int j = 0; j < dist.length; j++) {
                System.out.print(dist[i][j] + " ");
            }
            System.out.println();
        }


        String s1 = "abcd";
        String s2 = "bcfe";
        System.out.println(editDistance(s1, s2));
        /*
            robber cant steal from adjacent houses max loot
        */
        int[] hval = {6, 7, 1, 3, 8, 2, 4};
        System.out.println(maxLoot(hval));
        int[] nums = {1, 2, 3, 1};
        System.out.println("max loot in circular layout: " + maxLootCircle(nums));

        /**
         * max the loot but houses are in circular fashion & cant rob adjacent houses
         */

        int[] arr = {140, 20, 50, 40, 10, 50, 60, 50};
        System.out.println("max area of rectangle in histogram : " + getMaxArea(arr));
        int[][] mat = {
                {0, 1, 1, 0},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 0, 0}
        };

        System.out.println("max area of rectangle in matrix : "+maxAreaMatrix(mat));

        int[] price = {1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println("cut rod to get max price: " + cutRod(price));

        /**
         * Imagine you have a special keyboard with the following keys:
         * Key 1:  Prints 'A' on screen
         * Key 2: (Ctrl-A): Select screen
         * Key 3: (Ctrl-C): Copy selection to buffer
         * Key 4: (Ctrl-V): Print buffer on screen appending it after what has already been printed.
         * If you can only press the keyboard for n times (with the above four keys), write a program to produce maximum numbers of A's on the screen.
         * print max number of A's if you can press keyboard n times.
         */
        System.out.println(maxAWithKeys(6));
        System.out.println(maxAWithKeys(7));
        System.out.println(maxAWithKeys(10));
        System.out.println(maxAWithKeys(17));
        /**
         * A Derangement is a permutation of n elements, such that no element appears in its original position. For example, a derangement of [0, 1, 2, 3] is [2, 3, 1, 0].
         * Given a number n, find the total number of Derangements of a set of n elements.
         **/
        System.out.println(countDearrangements(3));
        System.out.println(countDearrangements(5));


        int n = 4;
        int[][] prerequisites = {  {2, 0}, {2, 1}, {3, 2}  };

        ArrayList<Integer> result = findOrder(n, prerequisites);

        for (int course : result) {
            System.out.print(course + " ");
        }
    }


    public static int countDearrangements(int n) {
        // bottom up construction to get de-arrangements
        //f(n) = (n-1)*(f(n-1) + f(n-2))
        if (n <= 2) {
            return n - 1;
        }
        int prev1 = 0; // for n= 1 it is 0
        int prev2 = 1; // for n =2
        int j = 3;
        int curr = prev1;
        while (j <= n) {
            curr = (j - 1) * (prev2 + prev1);
            prev1 = prev2;
            prev2 = curr;
            j++;
        }
        return curr;
    }
    public static ArrayList<Integer> findOrder(int n, int[][] prerequisites) {
        // code here
        Map<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> val;
        int[] indegress = new int[n+1];
        for(int i=0; i<prerequisites.length;i++) {
            int x = prerequisites[i][0];
            int y = prerequisites[i][1];

            if(map.containsKey(x)) {
                val = map.get(x);
            }
            else{
                val = new ArrayList<>();
            }
            val.add(y);
            map.put(x,val);
            indegress[x]++;
        }
        ArrayList<Integer> order = new ArrayList<>();
        Queue<Integer> q = new LinkedList<>();
        for(int i = 0;i<n;i++) {
            if(!map.containsKey(i)) {
                q.add(i);
            }
        }
        while (!q.isEmpty()){
            int curr = q.poll();
            order.add(curr); // all no pre add the order
            for (var entry : map.entrySet()){
                if (entry.getValue().contains(curr)){
                    indegress[entry.getKey()]--;
                    if(indegress[entry.getKey()] == 0) {
                        q.add(entry.getKey());
                    }
                }
            }
        }
        if(order.size() == n) {
            return order;
        }
        return new ArrayList<>();

    }

    private static int maxAWithKeys(int n) {
        if (n <= 6) { // to select all copy past in a 3-step process so un-till  6
            return n;
        }
        int[] dp = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            dp[i] = i;
        }
        // select all then select copy then paste till 3 times is fine
        // post that  repeat entire process is optimal rather than just pasting
        // now 3 cases. if you can paste only 1,2,3 times post select all & copy
        // for n -> n-3 , all, cp, paste
        //for n-> n-4 , all,cp,paste ,paste
        //for n-> n-5 ,all,cp,paste,paste,paste max of this optimal
        for (int i = 7; i <= n; i++) {
            dp[i] = Math.max(dp[i - 3] * 2, Math.max(dp[i - 4] * 3, dp[i - 5] * 4));
        }
        return dp[n];
    }

    public static int cutRod(int[] price) {
        int n = price.length;
        int[] dp = new int[n + 1];
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                dp[i] = Math.max(dp[i], price[j - 1] + dp[i - j]);
            }
        }
        return dp[n];
    }

    private static int maxAreaMatrix(int[][] mat) {
        int m = mat[0].length;
        int[] h = new int[m];
        Arrays.fill(h,0);
        int ans = 0 ;
        for (int[] ints : mat) {
            for (int j = 0; j < m; j++) {
                if (ints[j] == 1) {
                    h[j]++;
                } else {
                    h[j] = 0;
                }
            }
            ans = Math.max(ans, getMaxArea(h));
        }
        return ans;
    }

    /*
    push into stack as long as it is non-decreasing sequence else process it
    pop the top to min value which height & get width
     */
    public static int getMaxArea(int[] arr) {
        Stack<Integer> st = new Stack<>();
        int maxArea = 0;
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && arr[st.peek()] > arr[i]) {
                int tp = st.pop();
                int w = st.isEmpty() ? i : i - st.peek() - 1;
                //System.out.println("rectangle area" + arr[tp] + " *" + w + "at index:" + i);
                maxArea = Math.max(maxArea, arr[tp] * w);
            }
            st.push(i);
        }
        while (!st.isEmpty()) {
            int tp = st.pop();
            int w = st.isEmpty() ? n : n - st.peek() - 1;
          //  System.out.println("rectangle area" + arr[tp] + " *" + w + "at index:" + n);

            maxArea = Math.max(maxArea, arr[tp] * w);
        }
        return maxArea;
    }

    private static int maxLoot(int[] hval) {
        int[] dp = new int[hval.length + 1];
        Arrays.fill(dp, 0);
        dp[1] = hval[0];
        for (int i = 2; i <= hval.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + hval[i - 1]);
        }
        return dp[hval.length];
    }

    private static int maxLootIndex(int[] hval, int start, int end) {
        int bPrev = 0;
        int prev = 0;
        for (int i = start; i <= end; i++) {
            int curr = Math.max(bPrev + hval[i], prev);
            bPrev = prev;
            prev = curr;
        }
        return prev;
    }

    public static int maxLootCircle(int[] hval) {
        // max of 0 to l-2 , 1 to l-1
        return Math.max(maxLootIndex(hval, 0, hval.length - 2), maxLootIndex(hval, 1, hval.length - 1));
    }

    public static int editDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            Arrays.fill(dp[i], -1);
        }
        return editDistanceRec(s1.toCharArray(), s2.toCharArray(), s1.length(), s2.length(), dp);
    }

    private static int editDistanceRec(char[] s1, char[] s2, int m, int n, int[][] dp) {
        if (m == 0) {
            return n;
        }
        if (n == 0) {
            return n;
        }
        if (dp[m][n] != -1) {
            return dp[m][n];
        }
        if (s1[m - 1] == s2[n - 1]) {
            dp[m][n] = editDistanceRec(s1, s2, m - 1, n - 1, dp);
            return dp[m][n];
        }
        dp[m][n] = Math.min(editDistanceRec(s1, s2, m, n - 1, dp), editDistanceRec(s1, s2, m - 1, n, dp)) + 1;
        dp[m][n] = Math.min(dp[m][n], 1 + editDistanceRec(s1, s2, m - 1, n - 1, dp));
        return dp[m][n];
    }

    public static void floydWarshall(int[][] dist) {
        int INF = 100000000;

        for (int k = 0; k < dist.length; k++) {
            for (int i = 0; i < dist.length; i++) {
                for (int j = 0; j < dist.length; j++) {
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }
    }
}
