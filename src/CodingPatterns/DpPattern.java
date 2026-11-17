package CodingPatterns;

import java.util.HashMap;
import java.util.Map;

public class DpPattern {

    public static void main(String[] args) {
        // die hard (air - health +3 armour + 2; water health -5 armour -10 fire health -20 , armour + 5)
        System.out.println("Max amount of survived time: " + dieHard(55, 20));

    }

    private static int dieHard(int health, int armour) {
        int[][] state = new int[3][2];
        state[0] = new int[]{3, 2};
        state[1] = new int[]{-5, -10};
        state[2] = new int[]{-20, 5};

        Map<String, Integer> memo = new HashMap<>();
        int val1 = dieHardUtil(health, armour, memo, 0, 0, state);
        int val2 = dieHardUtil(health, armour, memo, 0, 1, state);
        int val3 = dieHardUtil(health, armour, memo, 0, 2, state);


        return Math.max(val1, Math.max(val3, val2));


    }


    private static int dieHardUtil(int health, int armour, Map<String, Integer> memo, int time, int state, int[][] arr) {

        if (health <= 0 || armour <= 0) {
            return time;
        }
        String key = String.format("%s-%s-%s", health, armour, state);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        health = health + arr[state][0];
        armour = armour + arr[state][1];


        int val = Math.max(dieHardUtil(health, armour, memo, time + 1, (state + 1) % 3, arr),
                dieHardUtil(health, armour, memo, time + 1, (state + 2) % 3, arr));

        memo.put(key, val);
        return val;
    }
}
