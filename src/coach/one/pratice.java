package coach.one;/*
Problem: Word Ladder

Description:

A transformation sequence from word beginWord to word endWord using a
dictionary wordList is a sequence of words:

beginWord -> s1 -> s2 -> ... -> sk

such that:

1. Every adjacent pair differs by exactly one character.
2. Every si exists in wordList.
3. sk == endWord.

Return the length of the shortest transformation sequence from beginWord
to endWord.

Return 0 if no such sequence exists.

----------------------------------------------------

Example 1:

Input:
beginWord = "hit"
endWord = "cog"

wordList =
["hot","dot","dog","lot","log","cog"]

Output:
5

Explanation:

hit -> hot -> dot -> dog -> cog

Length = 5

----------------------------------------------------

Example 2:

Input:
beginWord = "hit"
endWord = "cog"

wordList =
["hot","dot","dog","lot","log"]

Output:
0

Explanation:

cog does not exist in wordList.

----------------------------------------------------

Constraints:

1 <= beginWord.length <= 10
beginWord.length == endWord.length
1 <= wordList.length <= 5000
wordList[i].length == beginWord.length
All words consist of lowercase English letters.
beginWord != endWord

----------------------------------------------------

Expected Method Signature:

public static int ladderLength(
        String beginWord,
        String endWord,
        List<String> wordList)

----------------------------------------------------

Includes:
- Sample tests
- Hidden-style edge cases
- Large branching cases
----------------------------------------------------
*/

import java.util.*;

public class pratice {

    // TODO: Implement solution
    public static int ladderLength(
            String beginWord,
            String endWord,
            List<String> wordList) {
        int level = 0;
        Set<String> visited = new HashSet<>();
        Queue<String> q = new ArrayDeque<>();
        q.add(beginWord);
        Queue<String> q2 = new ArrayDeque<>();
        if (beginWord.equals(endWord) || !wordList.contains(endWord)) {
            return 0;
        }
        while (!q.isEmpty()) {
            var curr = q.poll();
            q2.addAll(getNextWords(curr, wordList, visited));
            if (q.isEmpty()) {
                level++;
                q.addAll(q2);
                q2.clear();
                if (q.contains(endWord)) {
                    return level + 1;
                }
            }
        }

        return 0;
    }

    private static List<String> getNextWords(String word, List<String> wordList, Set<String> visited) {
        int len = word.length();
        int index = 0;
        List<String> results = new ArrayList<>();
        while (index < len) {
            var actual = word.charAt(index);
            char x;
            char[] temp = word.toCharArray();
            for (int i = 0; i <= 26; i++) {
                x = (char) ('a' + i);

                if (x != actual) {
                    temp[index] = x;
                    String newWord = String.valueOf(temp);
                    if (wordList.contains(newWord) && !visited.contains(newWord)) {
                        results.add(newWord);
                        visited.add(newWord);
                    }
                }
            }
            index++;
        }
        return results;
    }


    private static void runTest(
            int testNo,
            String beginWord,
            String endWord,
            List<String> wordList,
            int expected) {

        int actual = ladderLength(
                beginWord,
                endWord,
                wordList);

        System.out.println(
                "Test " + testNo +
                        " | Expected: " + expected +
                        " | Actual: " + actual +
                        " | " + (actual == expected ? "PASS" : "FAIL")
        );
    }

    public static void main(String[] args) {

        // Sample Test 1
        runTest(
                1,
                "hit",
                "cog",
                Arrays.asList(
                        "hot",
                        "dot",
                        "dog",
                        "lot",
                        "log",
                        "cog"
                ),
                5
        );

        // Sample Test 2
        runTest(
                2,
                "hit",
                "cog",
                Arrays.asList(
                        "hot",
                        "dot",
                        "dog",
                        "lot",
                        "log"
                ),
                0
        );

        // Hidden Test: Direct transformation
        runTest(
                3,
                "cat",
                "cot",
                Arrays.asList(
                        "cot"
                ),
                2
        );

        // Hidden Test: One possible path
        runTest(
                4,
                "red",
                "tax",
                Arrays.asList(
                        "ted",
                        "tex",
                        "red",
                        "tax",
                        "tad",
                        "den",
                        "rex",
                        "pee"
                ),
                4
        );

        // Hidden Test: End word missing
        runTest(
                5,
                "aaa",
                "bbb",
                Arrays.asList(
                        "aab",
                        "abb"
                ),
                0
        );

        // Hidden Test: Single valid chain
        runTest(
                6,
                "aaa",
                "ccc",
                Arrays.asList(
                        "aac",
                        "acc",
                        "ccc"
                ),
                4
        );

        // Hidden Test: Multiple shortest paths
        runTest(
                7,
                "lost",
                "cost",
                Arrays.asList(
                        "cost",
                        "most",
                        "host"
                ),
                2
        );

        // Hidden Test: No path despite end existing
        runTest(
                8,
                "abc",
                "xyz",
                Arrays.asList(
                        "xbc",
                        "xyc",
                        "xyz",
                        "abx"
                ),
                4
        );

        // Hidden Test: Isolated target
        runTest(
                9,
                "hit",
                "cog",
                Arrays.asList(
                        "cog"
                ),
                0
        );

        // Hidden Test: Longer chain
        runTest(
                10,
                "aaaa",
                "bbbb",
                Arrays.asList(
                        "baaa",
                        "bbaa",
                        "bbba",
                        "bbbb"
                ),
                5
        );
    }
}