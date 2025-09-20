import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainPairs {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        int[][] arr = {{7, 8}, {1, 5}, {2, 4}, {4, 6}};
        ArrayList<int[]> res = mergeOverlap(arr);

        for (int[] interval : res) {
            System.out.println(interval[0] + " " + interval[1]);
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
            return new int[] {start, end};
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


}
