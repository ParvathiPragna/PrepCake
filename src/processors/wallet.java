package processors;

import java.util.*;
import java.util.stream.Collectors;

public class wallet {

    /*
    ===========================================================
    DIGITAL WALLET SYSTEM
    ===========================================================

    LEVEL 1
    --------
    CREATE_USER
    DEPOSIT
    TRANSFER
    BALANCE

    LEVEL 2
    --------
    GET_TRANSACTIONS

    LEVEL 3
    --------
    TOP_SPENDERS

    LEVEL 4
    --------
    GET_CASHBACK
    APPLY_CASHBACK

    ===========================================================
    IMPLEMENT:

    static String[] process(String[][] operations)

    ===========================================================
    */

    public static void main(String[] args) {

        testLevel1Basic();
        testLevel1Edge();

        testLevel2Basic();
        testLevel2Edge();

        testLevel3Basic();
        testLevel3TieBreaker();

        testLevel4Basic();
        testLevel4Edge();
    }

    // ==================================================
    // LEVEL 1
    // ==================================================

    static void testLevel1Basic() {

        String[][] input = {

                {"CREATE_USER", "Alice"}, {"CREATE_USER", "Bob"},

                {"DEPOSIT", "Alice", "500"},

                {"TRANSFER", "Alice", "Bob", "200"},

                {"BALANCE", "Alice"}, {"BALANCE", "Bob"}};

        String[] expected = {

                "true", "true",

                "500",

                "success",

                "300", "200"};

        runTest("LEVEL 1 - BASIC", input, expected);
    }

    static void testLevel1Edge() {

        String[][] input = {

                {"CREATE_USER", "Alice"}, {"CREATE_USER", "Alice"},

                {"TRANSFER", "Alice", "Bob", "100"},

                {"BALANCE", "UNKNOWN"}};

        String[] expected = {

                "true", "false",

                "invalid_request",

                ""};

        runTest("LEVEL 1 - EDGE", input, expected);
    }

    // ==================================================
    // LEVEL 2
    // ==================================================

    static void testLevel2Basic() {

        String[][] input = {

                {"CREATE_USER", "Alice"}, {"CREATE_USER", "Bob"},

                {"DEPOSIT", "Alice", "500"}, {"DEPOSIT", "Bob", "500"},

                {"TRANSFER", "Alice", "Bob", "100"}, {"TRANSFER", "Bob", "Alice", "50"},

                {"GET_TRANSACTIONS", "Alice"}, {"GET_TRANSACTIONS", "Bob"}};

        String[] expected = {

                "true", "true",

                "500", "500",

                "success", "success",

                "2", "2"};

        runTest("LEVEL 2 - BASIC", input, expected);
    }

    static void testLevel2Edge() {

        String[][] input = {

                {"CREATE_USER", "Alice"},

                {"GET_TRANSACTIONS", "UNKNOWN"}};

        String[] expected = {

                "true",

                ""};

        runTest("LEVEL 2 - EDGE", input, expected);
    }

    // ==================================================
    // LEVEL 3
    // ==================================================

    static void testLevel3Basic() {

        String[][] input = {

                {"CREATE_USER", "Alice"}, {"CREATE_USER", "Bob"}, {"CREATE_USER", "Carl"},

                {"DEPOSIT", "Alice", "1000"}, {"DEPOSIT", "Bob", "1000"},

                {"TRANSFER", "Alice", "Carl", "500"}, {"TRANSFER", "Bob", "Carl", "300"},

                {"TOP_SPENDERS", "3"}};

        String[] expected = {

                "true", "true", "true",

                "1000", "1000",

                "success", "success",

                "Alice(500),Bob(300),Carl(0)"};

        runTest("LEVEL 3 - BASIC", input, expected);
    }

    static void testLevel3TieBreaker() {

        String[][] input = {

                {"CREATE_USER", "Bob"}, {"CREATE_USER", "Alice"},

                {"DEPOSIT", "Bob", "1000"}, {"DEPOSIT", "Alice", "1000"},

                {"TRANSFER", "Bob", "Alice", "500"}, {"TRANSFER", "Alice", "Bob", "500"},

                {"TOP_SPENDERS", "2"}};

        String[] expected = {

                "true", "true",

                "1000", "1000",

                "success", "success",

                "Alice(500),Bob(500)"};

        runTest("LEVEL 3 - TIE BREAKER", input, expected);
    }

    // ==================================================
    // LEVEL 4
    // ==================================================

    static void testLevel4Basic() {

        String[][] input = {

                {"CREATE_USER", "Alice"}, {"CREATE_USER", "Bob"},

                {"DEPOSIT", "Alice", "1000"},

                {"TRANSFER", "Alice", "Bob", "105"},

                {"GET_CASHBACK", "Alice"},

                {"APPLY_CASHBACK"},

                {"BALANCE", "Alice"}};

        String[] expected = {

                "true", "true",

                "1000",

                "success",

                "2",

                "success",

                "897"};

        runTest("LEVEL 4 - BASIC", input, expected);
    }

    static void testLevel4Edge() {

        String[][] input = {

                {"CREATE_USER", "Alice"}, {"CREATE_USER", "Bob"},

                {"DEPOSIT", "Alice", "49"},

                {"TRANSFER", "Alice", "Bob", "49"},

                {"GET_CASHBACK", "Alice"}};

        String[] expected = {

                "true", "true",

                "49",

                "success",

                "0"};

        runTest("LEVEL 4 - EDGE", input, expected);
    }

    // ==================================================
    // TEST HARNESS
    // ==================================================

    static void runTest(String testName, String[][] input, String[] expected) {

        WalletManager manager = new WalletManager();

        String[] actual = manager.process(input);

        boolean passed = Arrays.equals(expected, actual);

        System.out.println((passed ? "✅ " : "❌ ") + testName);

        if (!passed) {

            System.out.println("Expected: " + Arrays.toString(expected));

            System.out.println("Actual  : " + Arrays.toString(actual));
        }
    }
}


class WalletManager {

    Map<String, UserW> users = new HashMap<>();

    String[] process(String[][] operations) {
        String[] output = new String[operations.length];
        for (int i = 0; i < operations.length; i++) {
            output[i] = switch (operations[i][0]) {
                case "CREATE_USER" -> createUser(operations[i][1]);
                case "DEPOSIT" -> deposit(operations[i][1], Integer.parseInt(operations[i][2]));
                case "TRANSFER" -> transfer(operations[i][1], operations[i][2], Integer.parseInt(operations[i][3]));
                case "BALANCE" -> getBalance(operations[i][1]);
                case "GET_TRANSACTIONS" -> getTranscations(operations[i][1]);
                case "TOP_SPENDERS" -> getTopSpenders(Integer.parseInt(operations[i][1]));
                case "GET_CASHBACK" -> getCashBack(operations[i][1]);
                case "APPLY_CASHBACK" -> applyCashBack();
                default -> "false";
            };
        }
        return output;
    }

    private String applyCashBack() {
        users.values()
                .stream()
                .forEach(UserW::applyCashback);
        return "success";
    }

    private String getCashBack(String userId) {
        if (users.containsKey(userId)) {
            return String.valueOf(users.get(userId).getCashback());
        }
        return "invalid_request";
    }

    private String getTopSpenders(int limit) {
        if(users.size() < limit) {
            return "invalid_request";
        }
        return users.values()
                .stream()
                .sorted(Comparator.comparingInt(UserW::getSpent).reversed().thenComparing(user -> user.userId))
                .limit(limit)
                .map(UserW::parseSpent)
                .collect(Collectors.joining(","));
    }

    private String getTranscations(String userId) {
        if (users.containsKey(userId)) {
            return String.valueOf(users.get(userId).transcations.size());
        }
        return "";
    }

    private String getBalance(String userId) {
        if (users.containsKey(userId)) {
            return String.valueOf(users.get(userId).getBalance());
        }
        return "";
    }

    private String transfer(String source, String dest, int amount) {
        if (users.containsKey(source) && users.containsKey(dest)) {
            TranscationW transcationW = new TranscationW(source, dest, amount);
            if (users.get(source).debit(transcationW)) {
                users.get(dest).credit(transcationW);
                return "success";
            }
        }
        return "invalid_request";
    }

    private String deposit(String userId, int amount) {
        if (users.containsKey(userId)) {
            return String.valueOf(users.get(userId).deposit(amount));
        }
        return "failure";
    }

    private String createUser(String userId) {
        if (!users.containsKey(userId)) {
            users.put(userId, new UserW(userId));
            return "true";
        }
        return "false";
    }
}

class UserW {
    String userId;
    Integer balance;

    Integer spent;
    Integer cashback;
    List<TranscationW> transcations;

    UserW(String userId) {
        this.userId = userId;
        this.balance = 0;
        this.spent = 0;
        this.cashback = 0;
        transcations = new ArrayList<>();
    }

    void credit(TranscationW transcationW) {
        transcations.add(transcationW);
        deposit(transcationW.amount);
    }

    Integer deposit(Integer amount) {
        this.balance += amount;
        return this.balance;
    }

    boolean debit(TranscationW transcationW) {
        if (balance < transcationW.amount) {
            return false;
        }
        balance -= transcationW.amount;
        this.spent+=transcationW.amount;
        transcations.add(transcationW);
        cashback += (int)Math.floor(transcationW.amount * 0.02);
        return true;
    }

    Integer getBalance() {
        return this.balance;
    }

    Integer getSpent() {
        return  this.spent;
    }

    String parseSpent() {
        return userId + "(" + this.spent + ")";
    }

    Integer getCashback() {
        return cashback;
    }
    void applyCashback() {
        this.balance+=cashback;
        cashback=0;
    }
}


class TranscationW {
    String src;
    String dest;

    Integer amount;

    TranscationW(String src, String dest, Integer amount) {
        this.src = src;
        this.dest = dest;
        this.amount = amount;
    }
}