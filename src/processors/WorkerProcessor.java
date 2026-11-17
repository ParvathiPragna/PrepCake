package processors;

import java.util.*;

public class WorkerProcessor {

    public static void main(String[] args) {

        runBasicTest();
        runSalaryOverlapTest();
        runMergeTest();
    }

    private static void runBasicTest() {

        String[][] operations = {{"ADD_WORKER", "Mia", "Engineer", "10"}, {"REGISTER", "Mia", "10"}, {"REGISTER", "Mia", "20"}, {"GET", "Mia"}};

        String[] expected = {"true", "registered", "registered", "10"};

        runTest("Basic Worker Test", operations, expected);
    }

    private static void runSalaryOverlapTest() {

        String[][] operations = {{"ADD_WORKER", "Alex", "Engineer", "10"}, {"SET_OVERTIME_RULE", "Alex", "5"}, {"REGISTER", "Alex", "10"}, {"REGISTER", "Alex", "20"}, {"CALC_SALARY", "Alex", "12", "18"}};

        String[] expected = {"true", "success", "registered", "registered", "90"};

        runTest("Salary Overlap Test", operations, expected);
    }

    private static void runMergeTest() {

        String[][] operations = {{"ADD_WORKER", "A", "DEV", "10"}, {"ADD_WORKER", "B", "DEV", "20"},

                {"REGISTER", "A", "10"}, {"REGISTER", "A", "20"},

                {"REGISTER", "B", "30"}, {"REGISTER", "B", "40"},

                {"MERGE_WORKERS", "A", "B"}, {"GET", "A"}};

        String[] expected = {"true", "true", "registered", "registered", "registered", "registered", "success", "20"};

        runTest("Merge Test", operations, expected);
    }

    private static void runTest(String name, String[][] operations, String[] expected) {

        WorkManager wm = new WorkManager();

        String[] actual = wm.process(operations);

        boolean passed = Arrays.equals(expected, actual);

        System.out.println((passed ? "✅ " : "❌ ") + name);

        if (!passed) {
            System.out.println("Expected : " + Arrays.toString(expected));

            System.out.println("Actual   : " + Arrays.toString(actual));
        }
    }
}

class WorkManager {

    private final Map<String, Worker> workers = new HashMap<>();

    public String[] process(String[][] operations) {

        String[] result = new String[operations.length];

        for (int i = 0; i < operations.length; i++) {

            String[] op = operations[i];

            switch (op[0]) {

                case "ADD_WORKER":
                    result[i] = addWorker(op[1], op[2], Integer.parseInt(op[3]));
                    break;

                case "REGISTER":
                    result[i] = register(op[1], Integer.parseInt(op[2]));
                    break;

                case "GET":
                    result[i] = get(op[1]);
                    break;

                case "SET_OVERTIME_RULE":
                    result[i] = setOvertimeRule(op[1], Integer.parseInt(op[2]));
                    break;

                case "CALC_SALARY":
                    result[i] = calcSalary(op[1], Integer.parseInt(op[2]), Integer.parseInt(op[3]));
                    break;

                case "MERGE_WORKERS":
                    result[i] = mergeWorkers(op[1], op[2]);
                    break;

                default:
                    result[i] = "";
            }
        }

        return result;
    }

    private String addWorker(String id, String position, int rate) {

        if (workers.containsKey(id)) {
            return "false";
        }

        workers.put(id, new Worker(id, position, rate));

        return "true";
    }

    private String register(String workerId, int timestamp) {

        Worker worker = workers.get(workerId);

        if (worker == null) {
            return "invalid_request";
        }

        if (worker.openSession == null) {

            worker.openSession = new Session(timestamp, worker.rate, worker.threshold);

        } else {

            worker.openSession.end = timestamp;

            worker.sessions.add(worker.openSession);

            worker.openSession = null;
        }

        return "registered";
    }

    private String get(String workerId) {

        Worker worker = workers.get(workerId);

        if (worker == null) {
            return "";
        }

        int workedTime = worker.sessions.stream().mapToInt(Session::duration).sum();

        return String.valueOf(workedTime);
    }

    private String setOvertimeRule(String workerId, int threshold) {

        Worker worker = workers.get(workerId);

        if (worker == null) {
            return "invalid_request";
        }

        worker.threshold = threshold;

        return "success";
    }

    private String calcSalary(String workerId, int queryStart, int queryEnd) {

        Worker worker = workers.get(workerId);

        if (worker == null) {
            return "";
        }

        int totalSalary = worker.sessions.stream().mapToInt(session -> calculateSessionSalary(session, queryStart, queryEnd)).sum();

        return String.valueOf(totalSalary);
    }

    private int calculateSessionSalary(Session session, int queryStart, int queryEnd) {

        int overlapStart = Math.max(session.start, queryStart);

        int overlapEnd = Math.min(session.end, queryEnd);

        if (overlapStart >= overlapEnd) {
            return 0;
        }

        int rate = session.compensationAtStart;

        Integer threshold = session.thresholdAtStart;

        if (threshold == null) {

            return (overlapEnd - overlapStart) * rate;
        }

        int trigger = session.start + threshold;

        if (overlapEnd <= trigger) {

            return (overlapEnd - overlapStart) * rate;
        }

        if (overlapStart >= trigger) {

            return (overlapEnd - overlapStart) * 2 * rate;
        }

        int regular = trigger - overlapStart;

        int overtime = overlapEnd - trigger;

        return regular * rate + overtime * 2 * rate;
    }

    private String mergeWorkers(String worker1Id, String worker2Id) {

        if (worker1Id.equals(worker2Id)) {
            return "invalid_request";
        }

        Worker w1 = workers.get(worker1Id);

        Worker w2 = workers.get(worker2Id);

        if (w1 == null || w2 == null) {
            return "invalid_request";
        }

        if (w1.openSession != null || w2.openSession != null) {

            return "invalid_request";
        }

        w1.sessions.addAll(w2.sessions);

        workers.remove(worker2Id);

        return "success";
    }
}

class Worker {

    String id;
    String position;

    int rate;

    Integer threshold;

    Session openSession;

    List<Session> sessions = new ArrayList<>();

    Worker(String id, String position, int rate) {

        this.id = id;
        this.position = position;
        this.rate = rate;
    }
}

class Session {

    int start;
    int end;

    int compensationAtStart;

    Integer thresholdAtStart;

    Session(int start, int compensationAtStart, Integer thresholdAtStart) {

        this.start = start;
        this.compensationAtStart = compensationAtStart;
        this.thresholdAtStart = thresholdAtStart;
    }

    int duration() {
        return end - start;
    }
}