import java.util.HashMap;
import java.util.Map;

public class TwoPointers {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        String s = "geeksforgeeks";
        System.out.println(longestUniqueSubstr(s));
    }


    //two pointer
    // i , j maintain max len
    private static int longestUniqueSubstr(String s) {
        int len = 0;
        Map<Character, Integer> map = new HashMap<>(); // char, last seen index
        int start, end;
        start = 0;
        end = 1;
        map.put(s.charAt(start), 0);
        while (end < s.length()) {
            start = Math.max(start, map.getOrDefault(s.charAt(end),0) + 1);
            len = Math.max(len, end - start + 1);
            map.put(s.charAt(end), end);
            end++;
        }
        return len;
    }
}
