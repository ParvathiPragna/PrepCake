package coach.one;
/*
Problem: Accounts Merge

Description:

Given a list of accounts where each element accounts[i] is a list of strings,
where the first element accounts[i][0] is a name, and the rest of the elements
are emails representing emails of the account.

Now, we would like to merge these accounts.

Two accounts definitely belong to the same person if there is some common
email to both accounts. Note that even if two accounts have the same name,
they may belong to different people as people could have the same name.

After merging the accounts, return the accounts in the following format:

[
    name,
    sorted_email_1,
    sorted_email_2,
    ...
]

The returned accounts can be in any order.

----------------------------------------------------

Example 1:

Input:

[
    ["John","johnsmith@mail.com",
             "john_newyork@mail.com"],

    ["John","johnsmith@mail.com",
             "john00@mail.com"],

    ["Mary","mary@mail.com"],

    ["John","johnnybravo@mail.com"]
]

Output:

[
    ["John","john00@mail.com",
            "john_newyork@mail.com",
            "johnsmith@mail.com"],

    ["Mary","mary@mail.com"],

    ["John","johnnybravo@mail.com"]
]

----------------------------------------------------

Example 2:

Input:

[
    ["Alex","a@mail.com","b@mail.com"],
    ["Alex","b@mail.com","c@mail.com"]
]

Output:

[
    ["Alex","a@mail.com","b@mail.com","c@mail.com"]
]

----------------------------------------------------

Constraints:

1 <= accounts.length <= 1000
2 <= accounts[i].length <= 10
1 <= accounts[i][j].length <= 30

----------------------------------------------------

Expected Method Signature:

public static List<List<String>> accountsMerge(
        List<List<String>> accounts)

----------------------------------------------------

Includes:
- Sample tests
- Hidden tests
- Multiple connected components
- Duplicate emails
- Same name different users
----------------------------------------------------
*/

import java.util.*;

public class prac2 {

    // TODO: Implement solution


    private static void runTest(
            int testNo,
            List<List<String>> accounts,
            int expectedGroups) {

        List<List<String>> result = new Manager().
                accountsMerge(accounts);

        System.out.println(
                "Test " + testNo +
                        " | Expected Groups: " + expectedGroups +
                        " | Actual Groups: " + result.size() +
                        " | " +
                        (result.size() == expectedGroups
                                ? "PASS"
                                : "FAIL")

        );
        if (result.size() != expectedGroups) {
            System.out.println("result " + result + "|" + "expected " + expectedGroups);
        }
    }

    public static void main(String[] args) {

        // Sample Test 1
        runTest(
                1,
                Arrays.asList(
                        Arrays.asList(
                                "John",
                                "johnsmith@mail.com",
                                "john_newyork@mail.com"
                        ),
                        Arrays.asList(
                                "John",
                                "johnsmith@mail.com",
                                "john00@mail.com"
                        ),
                        Arrays.asList(
                                "Mary",
                                "mary@mail.com"
                        ),
                        Arrays.asList(
                                "John",
                                "johnnybravo@mail.com"
                        )
                ),
                3
        );

        // Sample Test 2
        runTest(
                2,
                Arrays.asList(
                        Arrays.asList(
                                "Alex",
                                "a@mail.com",
                                "b@mail.com"
                        ),
                        Arrays.asList(
                                "Alex",
                                "b@mail.com",
                                "c@mail.com"
                        )
                ),
                1
        );

        // Hidden Test: Single account
        runTest(
                3,
                Arrays.asList(
                        Arrays.asList(
                                "Bob",
                                "bob@mail.com"
                        )
                ),
                1
        );

        // Hidden Test: No overlap
        runTest(
                4,
                Arrays.asList(
                        Arrays.asList(
                                "John",
                                "a@mail.com"
                        ),
                        Arrays.asList(
                                "John",
                                "b@mail.com"
                        )
                ),
                2
        );

        // Hidden Test: Chain merge
        runTest(
                5,
                Arrays.asList(
                        Arrays.asList(
                                "John",
                                "a@mail.com",
                                "b@mail.com"
                        ),
                        Arrays.asList(
                                "John",
                                "b@mail.com",
                                "c@mail.com"
                        ),
                        Arrays.asList(
                                "John",
                                "c@mail.com",
                                "d@mail.com"
                        )
                ),
                1
        );

        // Hidden Test: Same name different people
        runTest(
                6,
                Arrays.asList(
                        Arrays.asList(
                                "David",
                                "d1@mail.com"
                        ),
                        Arrays.asList(
                                "David",
                                "d2@mail.com"
                        )
                ),
                2
        );

        // Hidden Test: Multiple groups
        runTest(
                7,
                Arrays.asList(
                        Arrays.asList(
                                "A",
                                "a1@mail.com",
                                "a2@mail.com"
                        ),
                        Arrays.asList(
                                "A",
                                "a2@mail.com",
                                "a3@mail.com"
                        ),
                        Arrays.asList(
                                "B",
                                "b1@mail.com"
                        ),
                        Arrays.asList(
                                "B",
                                "b2@mail.com"
                        )
                ),
                3
        );

        // Hidden Test: Duplicate emails in same account
        runTest(
                8,
                Arrays.asList(
                        Arrays.asList(
                                "Tom",
                                "t@mail.com",
                                "t@mail.com"
                        )
                ),
                1
        );

        // Hidden Test: Large connected component
        runTest(
                9,
                Arrays.asList(
                        Arrays.asList("X", "x1@mail.com", "x2@mail.com"),
                        Arrays.asList("X", "x2@mail.com", "x3@mail.com"),
                        Arrays.asList("X", "x3@mail.com", "x4@mail.com"),
                        Arrays.asList("X", "x4@mail.com", "x5@mail.com")
                ),
                1
        );

        // Hidden Test: Shared email across far accounts
        runTest(
                10,
                Arrays.asList(
                        Arrays.asList(
                                "Alice",
                                "a@mail.com",
                                "b@mail.com"
                        ),
                        Arrays.asList(
                                "Alice",
                                "c@mail.com"
                        ),
                        Arrays.asList(
                                "Alice",
                                "d@mail.com",
                                "a@mail.com"
                        )
                ),
                2
        );
    }
}

class Account {
    String name;
    Set<String> mails;

    Account(String name, Set<String> mails) {
        this.mails = mails;
        this.name = name;
    }

    boolean matches(String testMail) {
        if (mails.contains(testMail)) {
            return true;
        }
        return false;
    }

    void mergeAccount(Account account) {
        if (name.equals(account.name)) {
            this.mails.addAll(account.mails);
        }
    }


    List<String> result() {
        List<String> result = new ArrayList<>();
        result.add(name);
        result.addAll(mails);
        return result;
    }
}

class Manager {
    public List<List<String>> accountsMerge(
            List<List<String>> accounts) {
        Map<String, List<Integer>> mailMap = new HashMap<>();  //maildId -> 1,2,3 -> mailId ->{john, Allof(1,2,3)} forEvery1->
        for (int k = 0; k < accounts.size(); k++) {
            for (int i = 1; i < accounts.get(k).size(); i++) {
                var mail = accounts.get(k).get(i);
                var list = mailMap.getOrDefault(mail, new ArrayList<>());
                list.add(k);
                mailMap.put(mail, list);
            }
        }

        List<List<String>> result = new ArrayList<>();
        boolean[] visited = new boolean[accounts.size()];
        for (int k = 0; k < accounts.size(); k++) {
            if (!visited[k]) {
                var list = new ArrayList<String>();
                var set = new HashSet<String>();
                Queue<Integer> q = new ArrayDeque<>();
                q.add(k);
                while (!q.isEmpty()) {
                    var curr = q.poll();
                    var mails = accounts.get(curr).subList(1, accounts.get(curr).size());
                    for (var mail : mails) {
                        for (var index : mailMap.getOrDefault(mail, new ArrayList<>())) {
                            if (!visited[index]) {
                                q.add(index);
                            }
                        }
                    }
                    set.addAll(mails);
                    visited[curr] = true;
                }
                list.addAll(set.stream().sorted().toList());
                result.add(list);
            }
        }
        return result;
    }
}

