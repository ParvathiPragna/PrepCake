package coach.lld.core.postgress;

import java.util.*;

public class mvcc {

    static class RowVersion {

        private final int value;

        // transaction that created this version
        private final long createdTxId;

        // transaction that deleted/replaced it
        // Long.MAX_VALUE means still active
        private long deletedTxId;

        public RowVersion(int value, long createdTxId, long deletedTxId) {

            this.value = value;
            this.createdTxId = createdTxId;
            this.deletedTxId = deletedTxId;
        }

        public int getValue() {
            return value;
        }

        public long getCreatedTxId() {
            return createdTxId;
        }

        public long getDeletedTxId() {
            return deletedTxId;
        }

        public void setDeletedTxId(long deletedTxId) {
            this.deletedTxId = deletedTxId;
        }

        @Override
        public String toString() {
            return "RowVersion{" + "value=" + value + ", createdTxId=" + createdTxId + ", deletedTxId=" + deletedTxId + '}';
        }
    }

    static class Transaction {

        private final long txId;

        // Snapshot visible when transaction starts
        private final long snapshotTxId;

        public Transaction(long txId, long snapshotTxId) {

            this.txId = txId;
            this.snapshotTxId = snapshotTxId;
        }

        public long getTxId() {
            return txId;
        }

        public long getSnapshotTxId() {
            return snapshotTxId;
        }

        @Override
        public String toString() {
            return "TX-" + txId;
        }
    }

    static class Database {

        private long nextTxId = 1;

        // One row, many versions
        private final List<RowVersion> versions = new ArrayList<>();

        public Database() {

            versions.add(new RowVersion(100, 1, Long.MAX_VALUE));

            nextTxId = 2;
        }

        public Transaction beginTransaction() {

            // TODO
            var transcx = new Transaction(nextTxId, nextTxId - 1);
            nextTxId++;

            return transcx;
        }

        public Integer read(Transaction tx) {

            // TODO

            /*
             * Find newest visible version.
             *
             * A version is visible if:
             *
             * createdTxId <= snapshotTxId
             *
             * AND
             *
             * deletedTxId > snapshotTxId
             */

            Optional<Integer> value = versions
                    .stream()
                    .filter(rv -> rv.createdTxId <= tx.snapshotTxId)
                    .filter(row -> row.deletedTxId > tx.snapshotTxId)
                    .max(Comparator.comparingLong(RowVersion::getCreatedTxId))
                    .map(rowVersion -> rowVersion.value);

            return value.get();
        }

        public void update(Transaction tx, int newValue) {

            // TODO

            /*
             * Find current active version.
             *
             * Mark it deleted by tx.
             *
             * Create new version
             * with createdTxId = tx.getTxId()
             */
            var rowVersion = versions
                    .stream()
                    .filter(rv -> rv.deletedTxId == Long.MAX_VALUE)
                    .findFirst();
            rowVersion.get().setDeletedTxId(tx.txId);
            versions.add(new RowVersion(newValue, tx.txId, Long.MAX_VALUE));

        }

        public void printVersions() {

            System.out.println("\nVersions:");

            for (RowVersion version : versions) {
                System.out.println(version);
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        Database db = new Database();

        Transaction t1 = db.beginTransaction();

        System.out.println("T1 reads = " + db.read(t1));

        Transaction t2 = db.beginTransaction();

        db.update(t2, 200);

        System.out.println("T2 updated value to 200");

        System.out.println("T1 reads again = " + db.read(t1));

        db.printVersions();
    }
}


class Index {

    static class User {

        private final int id;
        private final String name;

        public User(
                int id,
                String name) {

            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    static class UserTable {

        // Simulated Heap Table
        private final Map<Integer, User> rows =
                new HashMap<>();

        // Simulated B-Tree Index
        private final TreeMap<Integer, Integer> index =
                new TreeMap<>();

        public void insert(User user) {
            var rowId = getRowId(user.id);
            index.put(user.id, rowId);
            rows.put(rowId,user);
        }

        public User findById(int id) {
            return rows.get(index.get(id));
        }

        public void printIndex() {

            System.out.println(index);
        }

        int getRowId(int id) {
            return  id;
        }
    }

    public static void main(String[] args) {

        UserTable table =
                new UserTable();

        table.insert(
                new User(10, "Alice"));

        table.insert(
                new User(20, "Bob"));

        table.insert(
                new User(30, "Charlie"));

        System.out.println(
                table.findById(20));

        table.printIndex();
    }
}