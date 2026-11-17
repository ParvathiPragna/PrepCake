package coach.lld.core.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpringJPA {
}


class EntityCheck {

    static class User {

        private final Long id;
        private String name;

        public User(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class EntityKey {

        private final Class<?> entityClass;
        private final Object id;

        public EntityKey(Class<?> entityClass, Object id) {
            this.entityClass = entityClass;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EntityKey entityKey = (EntityKey) o;
            return Objects.equals(entityClass, entityKey.entityClass) && Objects.equals(id, entityKey.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entityClass, id);
        }

    }

    static class EntityManager {

        // First Level Cache (Persistence Context)
        private final Map<EntityKey, Object> persistenceContext =
                new HashMap<>();

        // Simulated DB
        private final Map<Long, User> database =
                new HashMap<>();

        public EntityManager() {

            database.put(
                    1L,
                    new User(1L, "John"));

            database.put(
                    2L,
                    new User(2L, "Alice"));
        }

        public <T> T find(Class<T> clazz, Object id) {

            // TODO

            // 1. Build EntityKey
            // 2. Check persistenceContext
            // 3. If found, return cached entity
            // 4. Else load from database
            // 5. Put into persistenceContext
            // 6. Return entity

            var entityKey = new EntityKey(clazz, id);
            if(persistenceContext.containsKey(entityKey)){
                return (T) persistenceContext.get(entityKey);
            }
            else {
                var record = database.get(id);
                persistenceContext.put(entityKey, record);
                return (T) record;
            }
        }

        public void clear() {
            persistenceContext.clear();
        }

        public void detach(Class<?> clazz, Object id) {
            var entityKey = new EntityKey(clazz, id);
            persistenceContext.remove(entityKey);
        }

        public int cacheSize() {
            return persistenceContext.size();
        }
    }

    public static void main(String[] args) {

        EntityManager em =
                new EntityManager();

        User u1 =
                em.find(User.class, 1L);

        User u2 =
                em.find(User.class, 1L);

        System.out.println(
                "u1 == u2 -> " + (u1 == u2));

        System.out.println(
                "Cache Size -> " + em.cacheSize());

        em.clear();

        User u3 =
                em.find(User.class, 1L);

        System.out.println(
                "u1 == u3 -> " + (u1 == u3));

        System.out.println(
                "Cache Size -> " + em.cacheSize());
    }
}
