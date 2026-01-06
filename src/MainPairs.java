import java.util.*;

public class MainPairs {
    public static void main(String[] args) {
        int[][] arr = {{7, 8}, {1, 5}, {2, 4}, {4, 6}};
        ArrayList<int[]> res = mergeOverlap(arr);

        for (int[] interval : res) {
            System.out.println(interval[0] + " " + interval[1]);
        }

        int[] start = {900, 940, 950, 1100, 1500, 1800};
        int[] dep = {910, 1200, 1120, 1130, 1900, 2000};

        System.out.println("platforms: " + minPlatform(start, dep));
        int[] a = {1, 2, 4, 5, 7};
        int[] b = {5, 6, 3, 4, 8};
        Pair[] pairs = allPairs(9, a, b);
        System.out.println();
        for (int i = 0; i < pairs.length; i++) {
            System.out.println("(" + pairs[i].first + " , " + pairs[i].second + ")");
        }
    }

    /*
        publish this!!!
     */
    public static int minPlatform(int[] arr, int[] dep) {
        List<Blocker> times = new ArrayList<>();
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            times.add(new Blocker(arr[i], 1));
            times.add(new Blocker(dep[i], -1));
        }
        times.sort(Comparator.comparingInt(t -> t.time)); // all arrivals & dep events are sort based on time
        int max = 0;
        int curr = 0;
        for (int i = 0; i < 2 * n; i++) {
            curr += times.get(i).block;
            max = Math.max(max, curr);
        }
        return max;
    }

    static class Blocker {
        int time;
        int block;

        public Blocker(int time, int block) {
            this.block = block;
            this.time = time;
        }
    }

    static class Interval {
        int start;
        int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int[] toArray() {
            return new int[]{start, end};
        }
    }

    private static ArrayList<int[]> mergeOverlap(int[][] arr) {
        ArrayList<Interval> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(new Interval(arr[i][0], arr[i][1]));
        }
        Collections.sort(list, new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                return o1.start - o2.start;
            }
        });

        for (int i = 0; i < list.size() - 1; ) {
            if (intervalsOverlap(list.get(i), list.get(i + 1))) {
                var interval = new Interval(Math.min(list.get(i).start, list.get(i + 1).start), Math.max(list.get(i).end, list.get(i + 1).end));
                list.remove(i + 1);
                list.remove(i);
                list.add(i, interval);
            } else {
                i++;
            }
        }
        ArrayList<int[]> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i).toArray());
        }
        return result;
    }

    private static boolean intervalsOverlap(Interval i1, Interval i2) { //i1.start <= i2.start since sorted

        if (i1.end < i2.start) {
            return false;
        }
        return true;
    }

    public static Pair[] allPairs(int target, int[] arr1, int[] arr2) {
        // Your code goes here
        List<Pair> li = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < arr2.length; i++) {
            map.put(arr2[i], map.getOrDefault(arr2[i], 0) + 1);
        }
        for (int i = 0; i < arr1.length; i++) {
            if (map.containsKey(target - arr1[i])) {
                li.add(new Pair(arr1[i], target - arr1[i]));
            }
        }
        li.sort(Comparator.comparingInt(o1 -> o1.first));

        return li.toArray(new Pair[0]);
    }

    static class Pair {
        int first, second;

        Pair(int a, int b) {
            this.first = a;
            this.second = b;
        }
    }
}
