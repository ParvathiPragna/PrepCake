import java.util.HashMap;
import java.util.Map;

public class TwoPointers {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        String s = "geeksforgeeks";
        System.out.println(longestUniqueSubstr(s));
    }


    //two pointer
    private static int longestUniqueSubstr(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        int len = 1;
        Map<Character, Integer> map = new HashMap<>();
        int start = 0;
        map.put(s.charAt(0), 0);
        for (int end = 1; end < s.length(); end++) {
            start = Math.max(start, map.getOrDefault(s.charAt(end), -1) + 1);
            len = Math.max(len, end - start + 1);
            map.put(s.charAt(end), end);
        }

        return len;
    }
}
