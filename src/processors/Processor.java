package processors;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;


public class Processor {

    public static void main(String[] args) {
        // Example input: each query is [command, timestamp, arg1, arg2, ...]
        String[][] queries = {
                {"CREATE_ACCOUNT", "1", "alice"},
                {"DEPOSIT", "2", "alice", "100"},
                {"CREATE_ACCOUNT", "3", "bob"},
                {"TRANSFER", "4", "alice", "bob", "30"},
                {"TOP_SPENDERS", "6", "1"},
                {"SCHEDULE_PAYMENT", "7", "alice", "20", "3"},
                {"GET_BALANCE", "8", "alice", "5"},
                {"CANCEL_PAYMENT", "9", "alice", "payment1"},
                {"MERGE_ACCOUNTS", "10", "alice", "bob"}
        };

        Bank bank = new Bank();
        String[] results = bank.processQueries(queries);

        // Print results line by line
        for (String res : results) {
            System.out.println(res);
        }
    }
}

class Bank {
    PriorityQueue<Transcation> pq = new PriorityQueue<Transcation>(Comparator.comparingInt(t -> t.time));
    HashMap<String, Transcation> paymentMap = new HashMap<>();
    HashMap<String, Account> accounts = new HashMap<>();
    int transcId = 1;

    String[] processQueries(String[][] queries) {
        String[] output = new String[queries.length];

        for (int i = 0; i < queries.length; i++) {
            var cmd = queries[i][0];
            var time = queries[i][1];
            var source = queries[i][2];
            while (!pq.isEmpty() && pq.peek().time <= Integer.valueOf(time)) {
                executeTrascation(pq.poll());
            }
            switch (cmd) {
                case "CREATE_ACCOUNT":
                    output[i] = createAccount(source, time);
                    break;
                case "DEPOSIT":
                    output[i] = depositToAccount(source, time, queries[i][3]);
                    break;
                case "TRANSFER":
                    output[i] = transferToAccount(source, time, queries[i][4], queries[i][3]);
                    break;
                case "SCHEDULE_PAYMENT":
                    output[i] = schedulePayment(time, source, queries[i][3], queries[i][4]);
                    break;
                case "CANCEL_PAYMENT":
                    output[i] = cancelPayment(queries[i][3]);
                    break;
                case "GET_BALANCE":
                    output[i] = getBalanceAtTime(source, queries[i][3]);
                    break;
                case "TOP_SPENDERS":
                    output[i] = topSpenders(queries[i][1]);
                    break;
                case "MERGE_ACCOUNTS":
                    output[i] = mergeAccounts(source,queries[i][3],time);
                    break;
            }
        }
        return output;
    }

    void executeTrascation(Transcation t) {
        if (t.isPending()) {
            var acc = accounts.get(t.target);
            acc.credit(String.valueOf(t.amount), String.valueOf(t.time));
            t.completed();
        }
    }

    String createAccount(String name, String time) {
        if (accounts.containsKey(name)) {
            return "false";
        }
        var acc = new Account(name, time);
        accounts.put(name, acc);
        return "true";
    }

    String depositToAccount(String name, String time, String amount) {
        if (!accounts.containsKey(name)) {
            return "false";
        }
        var acc = accounts.get(name);
        return acc.credit(amount, time);
    }

    String transferToAccount(String source, String time, String amount, String target) {
        if (!accounts.containsKey(source) || !accounts.containsKey(target)) {
            return "false";
        }
        var acc = accounts.get(source);
        var balance = acc.debit(amount, time);
        if (balance.equals("-1")) {
            return "false";
        }
        accounts.get(target).credit(amount, time);
        return balance;
    }

    String topSpenders(String num) {
        int n = Integer.parseInt(num);
        Comparator<Account> comparator = Comparator.comparing(Account::getSpent).reversed().thenComparing(acc -> acc.name);
        return accounts.values()
                .stream()
                .sorted(comparator)
                .limit(n)
                .map(Account::parseSpent)
                .collect(Collectors.joining(","));
    }

    String schedulePayment(String time, String target, String amount, String delay) {
        if (!accounts.containsKey(target)) {
            return "false";
        }
        var transcation = new Transcation(time, delay, amount, target);
        pq.add(transcation);
        var paymentId = "payment" + transcId;
        ++transcId;
        paymentMap.put(paymentId, transcation);
        return paymentId;
    }

    String cancelPayment(String paymentId) {
        if (paymentMap.containsKey(paymentId)) {
            var t = paymentMap.get(paymentId);
            return String.valueOf(t.cancelled());
        }
        return "false";
    }

    String getBalanceAtTime(String account, String timeAt) {
        if (accounts.containsKey(account)) {
            var acc = accounts.get(account);
            var val=  acc.getBalance(timeAt);
            return val;
        }
        return "0";
    }

    String mergeAccounts(String source, String target , String time) {
        if(accounts.containsKey(source) && accounts.containsKey(target)) {
            accounts.get(target).mergeAccount(accounts.get(source),time);
            accounts.remove(accounts.get(source));
            return "true";
        }
        return "false";
    }
}

class Account {
    String name;
    Integer balance;
    Integer spent;
    Map<Integer, Integer> balanceMap;

    Account(String name, String time) {
        this.name = name;
        this.balanceMap = new HashMap<>();
        this.balance = 0;
        this.spent = 0;
        balanceMap.put(Integer.valueOf(time), balance);
    }

    Integer getSpent() {
        return this.spent;
    }

    String getName() {
        return this.name;
    }

    String credit(String amount, String time) {
        var money = Integer.valueOf(amount);
        this.balance = this.balance + money;
        balanceMap.put(Integer.valueOf(time), balance);
        return String.valueOf(balance);
    }

    String debit(String amount, String time) {
        var money = Integer.valueOf(amount);
        if (balance < money) {
            return "-1";
        }
        this.balance = this.balance - money;
        this.spent = this.spent + money;
        balanceMap.put(Integer.valueOf(time), balance);
        return String.valueOf(balance);
    }

    String getBalance(String time) {
        var t = Integer.valueOf(time);
        if (balanceMap.containsKey(t)) {
            return String.valueOf(balanceMap.get(t));
        }
        var close_time = balanceMap.keySet()
                .stream()
                .filter(key -> key < t)
                .max(Integer::compareTo)
                .orElse(0);
        System.out.println(close_time+ "--");
        if (close_time == 0) {
            return "0";
        }
        return String.valueOf(balanceMap.get(close_time));
    }

    void mergeAccount(Account acc, String time) {
        this.balance = this.balance + acc.balance;
        this.spent = this.spent + acc.spent;
        balanceMap.put(Integer.valueOf(time), this.balance);
    }

    String parseSpent() {
        return name + "(" + spent + ")";
    }
}

class Transcation {
    Integer time;
    Integer amount;
    String target;
    Status status;

    enum Status {PENDING, COMPLETED, CANCELLED}


    public Transcation(String time, String delay, String amount, String target) {
        this.time = Integer.valueOf(time) + Integer.valueOf(delay);
        this.amount = Integer.valueOf(amount);
        this.target = target;
        this.status = Status.PENDING;
    }

    public boolean isPending() {
        return status.equals(Status.PENDING);
    }

    public void completed() {
        this.status = Status.COMPLETED;
    }

    public boolean cancelled() {
        if (this.status == Status.PENDING) {
            this.status = Status.CANCELLED;
            return true;
        }
        return false;
    }
}