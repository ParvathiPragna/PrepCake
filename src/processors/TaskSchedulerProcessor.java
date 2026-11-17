package processors;

import java.util.*;

public class TaskSchedulerProcessor {
    public static void main(String[] args) {
        /*
         Constraints:
         - ADD_TASK taskId duration dependencies
         - RUN currentTime
         - COMPLETE taskId currentTime
         - GET_ORDER
         - GET_STATUS taskId
         */

        String[][] queries = {
                {"ADD_TASK", "A", "3", ""},          // no deps
                {"ADD_TASK", "B", "2", "A"},         // depends on A
                {"ADD_TASK", "C", "4", "A,B"},       // depends on A and B
                {"GET_ORDER"},                    // valid order
                {"RUN", "0"},                      // start A
                {"COMPLETE", "A", "3"},             // complete A
                {"RUN", "3"},                      // start B
                {"COMPLETE", "B", "5"},             // complete B
                {"RUN", "5"},                      // start C
                {"GET_STATUS", "C"}                // should be RUNNING
        };

        // Expected outputs for validation
        String[] expected = {
                "true",       // add A
                "true",       // add B
                "true",       // add C
                "A,B,C",      // order
                "true",       // run A
                "true",       // complete A
                "true",       // run B
                "true",       // complete B
                "true",       // run C
                "RUNNING"     // status of C
        };

        TaskScheduler scheduler = new TaskScheduler(); // implement separately
        String[] results = scheduler.processQueries(queries);

        System.out.println("=== Test Results ===");
        boolean allPass = true;
        for (int i = 0; i < queries.length; i++) {
            System.out.println(Arrays.toString(queries[i]) + " -> " + results[i] +
                    " | expected: " + expected[i]);
            if (!Objects.equals(results[i], expected[i])) {
                allPass = false;
            }
        }
        System.out.println(allPass ? "All test cases PASSED" : "Some test cases FAILED");
    }
}

class TaskScheduler {

    Map<String, Task> taskMap = new HashMap<>();

    public String[] processQueries(String[][] queries) {
        String[] output = new String[queries.length];
        for (int i = 0; i < queries.length; i++) {
            output[i] = switch (queries[i][0]) {
                case "ADD_TASK" -> addTask(queries[i][1], Integer.valueOf(queries[i][2]), queries[i][3]);
                case "RUN" -> runTask(queries[i][1]);
                case "COMPLETE" -> completeTask(queries[i][1], queries[i][2]);
              //  case "GET_ORDER" -> getOrderOfTasks();
                case "GET_STATUS" -> getTaskStatus(queries[i][1]);
                default -> "false";
            };
        }
        return output;
    }

//    private String getOrderOfTasks() {
//        List<Task>
//        return null;
//    }

    private String getTaskStatus(String taskId) {
        if(taskMap.containsKey(taskId)) {
            return taskMap.get(taskId).status.name();
        }
        return "false";
    }

    private String completeTask(String taskId, String t) {
        var time = Integer.valueOf(t);
        var task = taskMap.get(taskId);
        return String.valueOf(task.markComplete(time));
    }

    private String runTask(String time) {
        var eligible = taskMap.values()
                .stream()
                .filter(Task::isRunnable)
                .toList();
        if(eligible.isEmpty()) {
            return "false";
        }
        eligible.forEach(task -> task.run(Integer.valueOf(time)));
        return "true";
    }

    private String addTask(String taskId, Integer duration, String dependecyStr) {
        if (!taskMap.containsKey(taskId)) {
            String[] depTasks = dependecyStr.split(",");
            var taskList = Arrays.stream(depTasks)
                    .map(taskStr -> taskMap.get(taskStr))
                    .toList();
            taskMap.put(taskId, new Task(taskId, duration, taskList));
            return "true";
        }
        return "false";
    }

}

class Task {
    String taskId;
    Integer duration;
    List<Task> dependencies;
    Status status;

    Integer startTime;

    enum Status {PENDING, RUNNING, COMPLETED}

    Task(String taskId, Integer duration, List<Task> dependencies) {
        this.taskId = taskId;
        this.duration = duration;
        this.dependencies = dependencies;
        this.status = Status.PENDING;
    }

    boolean isRunnable() {
        return dependencies.stream().allMatch(t -> t.status.equals(Status.COMPLETED));
    }

    boolean run(Integer time) {
        if (status.equals(Status.PENDING)) {
            status = Status.RUNNING;
            startTime = time;
            return true;
        }
        return false;
    }

    boolean markComplete(Integer time) {
        if (status.equals(Status.RUNNING) && startTime + duration <= time) {
            status = Status.COMPLETED;
            return true;
        }
        return false;
    }


}