package processors;

import java.util.*;
import java.util.stream.Collectors;

public class FileProcessor {
    public static void main(String[] args) {
        // Example input: each query is [command, arg1, arg2, ...]
        String[][] queries = {
                {"ADD_USER", "alice", "100"},
                {"ADD_FILE", "alice", "/docs/report.txt", "50"},
                {"COPY_FILE", "/docs/report.txt", "/docs/report_copy.txt"},
                {"FIND_FILES", "/docs", ".txt"},
                {"COMPRESS_FILE", "alice", "/docs/report.txt"},
                {"DECOMPRESS_FILE", "alice", "/docs/report.txt.COMPRESSED"},
        };

        FileSystem fs = new FileSystem();
        String[] results = fs.processQueries(queries);

        // Print results line by line
        for (String res : results) {
            System.out.println(res);
        }
    }
}

// Core orchestrator
class FileSystem {
    Map<String, User> users = new HashMap<>();
    Map<String, FileEntry> files = new HashMap<>();

    public String[] processQueries(String[][] queries) {
        String[] output = new String[queries.length];
        for (int i = 0; i < queries.length; i++) {
            String cmd = queries[i][0];
            switch (cmd) {
                case "ADD_USER":
                    output[i] = addUser(queries[i][1], Integer.valueOf(queries[i][2]));
                    break;
                case "ADD_FILE":
                    output[i] = addFile(queries[i][1], queries[i][2], Integer.valueOf(queries[i][3]));
                    break;
                case "COPY_FILE":
                    output[i] = copyFile(queries[i][1], queries[i][2]);
                    break;
                case "FIND_FILES":
                    output[i] = findFiles(queries[i][1], queries[i][2]);
                    break;
                case "COMPRESS_FILE":
                    output[i] = compressFile(queries[i][1], queries[i][2]);
                    break;
                case "DECOMPRESS_FILE":
                    output[i] = decompressFile(queries[i][1], queries[i][2]);
                    break;
                default:
                    output[i] = "false"; // fallback
            }
        }
        return output;
    }

    private String decompressFile(String userName, String path) {
        var user = users.get(userName);
        var file = files.get(path);
        if (user != null && file != null) {
            if (file.user.equals(userName)) {
                //lets de-compress
                var initialSize = file.size;
                if (file.decompressFile()) {
                    if(user.total_capacity >= user.avail_capacity + initialSize - file.size){
                        user.avail_capacity = user.avail_capacity + initialSize - file.size;

                        files.remove(path);
                        files.put(file.path, file);
                        return "true";
                    }
                    else {
                        file.compressFile();
                    }
                }
            }
        }
        return "false";
    }

    private String compressFile(String userName, String path) {
        var user = users.get(userName);
        var file = files.get(path);
        if (user != null && file != null) {
            if (file.user.equals(userName)) {
                //lets compress
                var initialSize = file.size;
                if (file.compressFile()) {
                    files.remove(path);
                    files.put(file.path, file);
                    user.avail_capacity = user.avail_capacity + initialSize - file.size;
                    return "true";
                }
            }
        }
        return "false";
    }

    private String findFiles(String prefix, String suffix) {

        return files.keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .filter(key -> key.endsWith(suffix))
                .map(path -> files.get(path))
                .sorted(Comparator.comparingInt(FileEntry::getSize).reversed().thenComparing(f->f.path))
                .map(fileEntry -> fileEntry.path)
                .collect(Collectors.joining(","));
    }

    private String copyFile(String path, String destPath) {
        if (files.containsKey(path) && !files.containsKey(destPath)) {
            var file = files.get(path);
            var destFile = new FileEntry(destPath, file.size);
            if (file.isCompressed.equals(destFile.isCompressed)) {
                if (users.get(file.user).addFile(destFile)) {
                    files.put(destPath,destFile);
                    return "true";
                }
            }
        }
        return "false";
    }

    private String addFile(String userName, String path, Integer size) {
        if (users.containsKey(userName) && !files.containsKey(path) && size > 0) {
            var user = users.get(userName);
            var fileEntry = new FileEntry(path, size);
            if (!fileEntry.isCompressed && user.addFile(fileEntry)) {
                files.put(path, fileEntry);
                return "true";
            }
        }
        return "false";
    }

    private String addUser(String s, Integer cap) {
        if (!users.containsKey(s)) {
            users.put(s, new User(s, cap));
            return "true";
        }
        return "false";
    }
}

class User {
    String name;
    Integer total_capacity;
    Integer avail_capacity;
    List<FileEntry> fileEntries;

    User(String name, Integer total_capacity) {
        this.name = name;
        this.total_capacity = total_capacity;
        this.avail_capacity = total_capacity;
        this.fileEntries = new ArrayList<>();
    }

    boolean addFile(FileEntry fileEntry) {
        if (fileEntry.size <= avail_capacity) {
            fileEntry.user = name;
            this.fileEntries.add(fileEntry);
            this.avail_capacity = this.avail_capacity - fileEntry.size;
            return true;
        }
        return false;
    }


}

class FileEntry {
    String path;
    Integer size;
    Boolean isCompressed;

    Integer fileSize;
    String user;

    FileEntry(String path, Integer size) {
        this.path = path;
        this.size = size;
        this.fileSize = size;
        this.isCompressed = path.endsWith(".COMPRESSED");
    }

    boolean compressFile() {
        if (!isCompressed) {
            this.path = this.path + ".COMPRESSED";
            this.size = Math.ceilDiv(this.size, 2);
            isCompressed = true;
            return true;
        }
        return false;
    }

    Integer getSize() {
        return this.size;
    }
    boolean decompressFile() {
        if(isCompressed) {
            this.path = this.path.replace(".COMPRESSED","");
            this.size = this.fileSize;
            isCompressed = false;
            return true;
        }
        return false;
    }
}