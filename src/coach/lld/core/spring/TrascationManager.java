package coach.lld.core.spring;

import java.util.ArrayList;
import java.util.List;

public class TrascationManager {
}

class TranscxManager {

    static class User {

        private final long id;
        private final String name;

        public User(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" + "id=" + id + ", name='" + name + '\'' + '}';
        }
    }

    static class TransactionManager {
        private boolean transactionActive;

        public void begin() {
            transactionActive = true;
        }

        public void commit() {
            transactionActive = false;
        }

        public void rollback() {
            transactionActive = false;
        }

        public boolean isTransactionActive() {
            return transactionActive;
        }
    }

    static class UserRepository {

        // Simulated Database Table
        private final List<User> database = new ArrayList<>();

        // Uncommitted Changes
        private final List<User> pendingInserts = new ArrayList<>();

        private final TransactionManager transactionManager;

        public UserRepository(TransactionManager transactionManager) {

            this.transactionManager = transactionManager;
        }

        public void save(User user) {
            if (user == null) {
                throw new RuntimeException();
            }
            if (transactionManager.isTransactionActive()) {
                pendingInserts.add(user);
            } else {
                database.add(user);
            }
        }

        public void commit() {
            database.addAll(pendingInserts);
            pendingInserts.clear();
        }

        public void rollback() {
            pendingInserts.clear();
        }

        public int count() {
            return database.size();
        }

        public void printDatabase() {

            System.out.println(database);
        }
    }

    static class UserService {

        private final TransactionManager tm;
        private final UserRepository repository;

        public UserService(TransactionManager tm, UserRepository repository) {

            this.tm = tm;
            this.repository = repository;
        }

        public void createUserSuccess() {

            // TODO

            // begin
            // save
            // commit
            var user = new User(1L, "user1");
            tm.begin();
            repository.save(user);
            repository.commit();
            tm.commit();

        }

        public void createUserFailure() {
            tm.begin();
            try {
                repository.save(null);
            }
            catch (Exception ex) {
                repository.rollback();
                tm.rollback();
            }
        }
    }

    public static void main(String[] args) {

        TransactionManager tm = new TransactionManager();

        UserRepository repository = new UserRepository(tm);

        UserService service = new UserService(tm, repository);

        System.out.println("Initial Count = " + repository.count());

        service.createUserSuccess();

        System.out.println("After Success = " + repository.count());

        service.createUserFailure();

        System.out.println("After Failure = " + repository.count());

        repository.printDatabase();
    }
}
