package coach.one;

import java.util.*;

public class meetingroom {

    public static void main(String[] args) {

        /*
         ============================================================
         1. Meeting Rooms II
         ============================================================

         Description:
         Given meeting intervals [start, end],
         return the minimum number of conference rooms required.

         Example:
         [[0,30],[5,10],[15,20]]
         Answer = 2
        */

        System.out.println("===== Meeting Rooms II =====");

        int[][] meetings1 = {
                {0, 30},
                {5, 10},
                {15, 20}
        }; // Expected: 2

        int[][] meetings2 = {
                {7, 10},
                {2, 4}
        }; // Expected: 1

        int[][] meetings3 = {
                {1, 5},
                {2, 6},
                {3, 7},
                {4, 8}
        }; // Expected: 4

        int[][] meetings4 = {
                {1, 2},
                {2, 3},
                {3, 4}
        }; // Expected: 1


        //  int rooms = minMeetingRooms(meetings1);
        System.out.println(minMeetingRooms(meetings1));
        System.out.println(minMeetingRooms(meetings2));
        System.out.println(minMeetingRooms(meetings3));
        System.out.println(minMeetingRooms(meetings4));




        /*
         ============================================================
         2. Employee Free Time
         ============================================================

         Description:
         Each employee has a list of working intervals.
         Return all common free time intervals.

         Example:
         Employee1 -> [1,3],[6,7]
         Employee2 -> [2,4]
         Employee3 -> [2,5],[9,12]

         Output:
         [5,6],[7,9]
        */

        System.out.println("\n===== Employee Free Time =====");

        List<int[][]> schedule1 = List.of(
                new int[][]{{1, 3}, {6, 7}},
                new int[][]{{2, 4}},
                new int[][]{{2, 5}, {9, 12}}
        );

        // Expected:
        // [5,6]
        // [7,9]

        List<int[][]> schedule2 = List.of(
                new int[][]{{1, 3}},
                new int[][]{{3, 5}},
                new int[][]{{5, 7}}
        );

        // Expected: No free interval


        System.out.println(employeeFreeTime(schedule1));
        System.out.println(employeeFreeTime(schedule2));




        /*
         ============================================================
         3. Merge K Sorted Lists
         ============================================================

         Description:
         Merge k sorted lists into one sorted list.

         Example:
         [1,4,5]
         [1,3,4]
         [2,6]

         Output:
         [1,1,2,3,4,4,5,6]
        */

        System.out.println("\n===== Merge K Lists =====");

        List<List<Integer>> lists1 = List.of(
                List.of(1, 4, 5),
                List.of(1, 3, 4),
                List.of(2, 6)
        );

        // Expected:
        // [1,1,2,3,4,4,5,6]

        List<List<Integer>> lists2 = List.of(
                List.of(),
                List.of(1),
                List.of(2, 3)
        );

        // Expected:
        // [1,2,3]

        List<List<Integer>> lists3 = List.of();

        // Expected:
        // []


        System.out.println(mergeKLists(lists1));
        System.out.println(mergeKLists(lists2));
        System.out.println(mergeKLists(lists3));





        /*
         ============================================================
         4. Top K Frequent Elements
         ============================================================

         Description:
         Return k most frequent elements.

         Example:
         nums = [1,1,1,2,2,3]
         k = 2

         Output:
         [1,2]
        */

        System.out.println("\n===== Top K Frequent =====");

        int[] nums1 = {1, 1, 1, 2, 2, 3};
        int k1 = 2;
        // Expected: [1,2]

        int[] nums2 = {4, 4, 4, 4, 5, 5, 6};
        int k2 = 1;
        // Expected: [4]

        int[] nums3 = {-1, -1, -2, -2, -2, -3};
        int k3 = 2;
        // Expected contains: [-2,-1]

        int[] nums4 = {1};
        int k4 = 1;
        // Expected: [1]


        System.out.println(topKFrequent(nums1, k1));
        System.out.println(topKFrequent(nums2, k2));
        System.out.println(topKFrequent(nums3, k3));
        System.out.println(topKFrequent(nums4, k4));






        /*
         ============================================================
         5. Task Scheduler
         ============================================================

         Description:
         CPU tasks represented by capital letters.
         Same task requires n cooldown periods between executions.

         Return minimum intervals needed to finish all tasks.

         Example:
         tasks = [A,A,A,B,B,B]
         n = 2

         Output = 8
         Schedule:
         A B idle A B idle A B
        */

        System.out.println("\n===== Task Scheduler =====");

        char[] tasks1 = {'A', 'A', 'A', 'B', 'B', 'B'};
        int cooldown1 = 2;
        // Expected: 8

        char[] tasks2 = {'A', 'A', 'A', 'B', 'B', 'B'};
        int cooldown2 = 0;
        // Expected: 6

        char[] tasks3 = {'A', 'A', 'A', 'A', 'B', 'C', 'D', 'E'};
        int cooldown3 = 2;
        // Expected: 10

        char[] tasks4 = {'A'};
        int cooldown4 = 100;
        // Expected: 1


//        System.out.println("tasks:"+ tasks1+ "slots:" + leastInterval(tasks1, cooldown1));
        System.out.println("tasks:" + tasks2 + "slots:" + leastInterval(tasks2, cooldown2));
        System.out.println("tasks:" + tasks3 + "slots:" + leastInterval(tasks3, cooldown3));
        System.out.println("tasks:" + tasks4 + "slots:" + leastInterval(tasks4, cooldown4));


        //--------------------------------------------------
        // TEST 1 : NULL GRAPH
        //--------------------------------------------------

        Node clone1 = cloneGraph(null);

        System.out.println(clone1 == null); // true


        //--------------------------------------------------
        // TEST 2 : SINGLE NODE
        //--------------------------------------------------

        Node node1 = new Node(1);

        Node clone2 = cloneGraph(node1);

        System.out.println(clone2.val); // 1
        System.out.println(clone2 != node1); // true


        //--------------------------------------------------
        // TEST 3 : TWO NODE CYCLE
        //--------------------------------------------------

        Node a = new Node(1);
        Node b = new Node(2);

        a.neighbors.add(b);
        b.neighbors.add(a);

        Node clonedCycle = cloneGraph(a);

        System.out.println(clonedCycle.val); // 1
        System.out.println(clonedCycle != a); // true

        System.out.println(
                clonedCycle.neighbors.get(0).val
        ); // 2


        //--------------------------------------------------
        // TEST 4 : SELF LOOP
        //--------------------------------------------------

        Node self = new Node(5);

        self.neighbors.add(self);

        Node selfClone = cloneGraph(self);

        System.out.println(selfClone.val); // 5

        System.out.println(
                selfClone ==
                        selfClone.neighbors.get(0)
        ); // true


        //--------------------------------------------------
        // TEST 5 : SQUARE GRAPH
        //--------------------------------------------------

        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);

        n1.neighbors.add(n2);
        n1.neighbors.add(n4);

        n2.neighbors.add(n1);
        n2.neighbors.add(n3);

        n3.neighbors.add(n2);
        n3.neighbors.add(n4);

        n4.neighbors.add(n1);
        n4.neighbors.add(n3);

        Node cloned = cloneGraph(n1);

        System.out.println(cloned.val); // 1

        printGraph(cloned);

    }


    /*
     ============================================================
     METHOD TEMPLATES ONLY
     ============================================================
     */

    static int minMeetingRooms(int[][] intervals) {
        // TODO
        Comparator<int[]> comparator = new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[0] - o2[0] != 0) {
                    return o1[0] - o2[0];
                }
                return o1[1] - o2[1];
            }
        };
        int rooms = 1;
        Arrays.stream(intervals).sorted(comparator);
        for (int i = 1; i < intervals.length; i++) {
            if (doesOverrlap(intervals[i - 1], intervals[i])) {
                rooms++;
            }
        }
        return rooms;
    }

    static boolean doesOverrlap(int[] a, int[] b) {
        if (a[0] >= b[1]) {
            return false;
        }
        if (a[1] <= b[0]) {
            return false;
        }
        return true;
    }


    static List<int[]> employeeFreeTime(List<int[][]> schedule) {
        // TODO
        Comparator<int[]> comparator = new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[0] - o2[0] != 0) {
                    return o1[0] - o2[0];
                }
                return o1[1] - o2[1];
            }
        };

        List<int[]> times = new ArrayList<>();
        int min = 1;
        int max = 0;
        for (int i = 0; i < schedule.size(); i++) {
            for (int j = 0; j < schedule.get(i).length; j++) {
                times.add(schedule.get(i)[j]);
                min = Math.min(schedule.get(i)[j][0], min);
                max = Math.max(schedule.get(i)[j][1], max);
            }
        }
        List<int[]> result = new ArrayList<>();
        for (int i = min; i <= max - 1; i++) {
            result.add(new int[]{i, i + 1});
        }
        for (int i = 0; i < times.size(); i++) {
            int finalI = i;
            result = result.stream()
                    .filter(x -> !doesOverrlap(times.get(finalI), x))
                    .toList();
        }

        List<int[]> ans = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {

            if (i != 0 && ans.get(ans.size() - 1)[1] == result.get(i)[0]) {
                ans.get(ans.size() - 1)[1] = result.get(i)[1];
            } else {
                ans.add(result.get(i));
            }
        }
        return ans;
    }

    static List<Integer> mergeKLists(List<List<Integer>> lists) {
        lists = lists.stream().filter(l -> !l.isEmpty()).toList();
        PriorityQueue<List<Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.get(0)));
        pq.addAll(lists);
        List<Integer> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            var list = pq.poll();
            result.add(list.get(0));
            if (list.size() > 1) {
                pq.add(list.subList(1, list.size()));
            }
        }
        return result;
    }

    static int[] topKFrequent(int[] nums, int k) {
        // TODO
        Map<Integer, Integer> counter = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            var count = counter.getOrDefault(nums[i], 0);
            counter.put(nums[i], count + 1);
        }

        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(x -> -x.getValue()));
        pq.addAll(counter.entrySet());
        int[] ans = new int[k];
        for (int i = 0; i < k; i++) {
            ans[i] = Objects.requireNonNull(pq.poll()).getKey();
        }
        return ans;
    }

    static int leastInterval(char[] tasks, int n) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (int i = 0; i < tasks.length; i++) {
            freqMap.put(tasks[i], freqMap.getOrDefault(tasks[i], 0) + 1);
        }
        Comparator<Map.Entry<Character, Integer>> comparator = (o1, o2) -> o2.getValue() - o1.getValue();
        PriorityQueue<Map.Entry<Character, Integer>> pq = new PriorityQueue<>(comparator);
        pq.addAll(freqMap.entrySet());
        if (n == 0) {
            return tasks.length;
        }
        int len = 0;
        StringBuilder sb = new StringBuilder();
        int timeSlot;
        List<Map.Entry<Character, Integer>> pending = new ArrayList<>();
        while (!pq.isEmpty()) {
            timeSlot = n + 1;
            pending.clear();
            while (!pq.isEmpty() && timeSlot > 0) {
                var curr = pq.poll();
                sb.append(curr.getKey()).append(" ");
                timeSlot--;
                len++;
                curr.setValue(curr.getValue() - 1);
                if (curr.getValue() > 0) { //if it 1 it done now more than stillpending tasks
                    pending.add(curr);
                }
            }
            pq.addAll(pending);

            if (pq.isEmpty() && pending.isEmpty()) {
                //  System.out.println(sb.toString());
                break;
            }
            len = len + timeSlot;
            while (timeSlot > 0 && !pending.isEmpty()) {
                sb.append("idle").append(" ");
                timeSlot--;
            }
        }
        System.out.println(sb);
        return len;
    }


    static int leastInterval2(char[] tasks, int n) {

        int[] freq = new int[26];

        for (char task : tasks) {
            freq[task - 'A']++;
        }

        PriorityQueue<Integer> pq =
                new PriorityQueue<>(Collections.reverseOrder());

        for (int count : freq) {
            if (count > 0) {
                pq.offer(count);
            }
        }

        int time = 0;

        while (!pq.isEmpty()) {

            List<Integer> pending = new ArrayList<>();

            int idealSlot = n + 1;

            while (idealSlot > 0 && !pq.isEmpty()) {

                int count = pq.poll();

                if (--count > 0) {
                    pending.add(count);
                }

                time++;
                idealSlot--;
            }

            for (int count : pending) {
                pq.offer(count);
            }

            if (pq.isEmpty()) {
                break;
            }

            time += idealSlot;
        }

        return time;
    }

    static Node cloneGraph(Node node) {
        HashMap<Node, Node> nodeMap = new HashMap<>();

        Queue<Node> q = new ArrayDeque<>();
        if(node == null) {
            return node;
        }
        q.offer(node);
        while (!q.isEmpty()) {
            var curr = q.poll();
            if (!nodeMap.containsKey(curr)) {
                nodeMap.put(curr, new Node(curr.val));
                q.addAll(curr.neighbors);
            }
        } //all new nodes are created but there are no connections
        for (var entry : nodeMap.entrySet()) {
            var old = entry.getKey();
            var newNode = entry.getValue();
            for (var nei : old.neighbors) {
                newNode.neighbors.add(nodeMap.get(nei));
            }
        }

        return nodeMap.get(node);
    }

    // =====================================================
    // GRAPH NODE
    // =====================================================

    static class Node {

        int val;

        List<Node> neighbors;

        Node(int val) {
            this.val = val;
            this.neighbors = new ArrayList<>();
        }

    }

    // =====================================================
    // DEBUG UTILITY
    // =====================================================

    static void printGraph(Node node) {

        Set<Node> visited = new HashSet<>();

        Queue<Node> queue = new LinkedList<>();

        queue.offer(node);

        visited.add(node);

        while (!queue.isEmpty()) {

            Node curr = queue.poll();

            System.out.print(
                    curr.val + " -> "
            );

            for (Node neighbor : curr.neighbors) {

                System.out.print(
                        neighbor.val + " "
                );

                if (!visited.contains(neighbor)) {

                    visited.add(neighbor);

                    queue.offer(neighbor);
                }
            }

            System.out.println();
        }
    }

}