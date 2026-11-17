package coach.lld.core.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Springtest {
}

class ApplicationContext {

    private final Map<Class<?>, Object> beans =
            new HashMap<>();

    public <T> T getBean(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        if (beans.containsKey(clazz)) {
            return (T) beans.get(clazz);
        }

        T bean = createBean(clazz);

        beans.put(clazz, bean);

        return bean;
    }

    private <T> T createBean(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<?> constructor =
                clazz.getConstructors()[0];

        Class<?>[] dependencies =
                constructor.getParameterTypes();

        Object[] args =
                new Object[dependencies.length];

        for (int i = 0; i < dependencies.length; i++) {
            args[i] = getBean(dependencies[i]);
        }

        return (T) constructor.newInstance(args);
    }
}
//-------------------#########----------------------//

class BeanLifeCycle {

    interface InitializingBean {
        void afterPropertiesSet();
    }

    interface DisposableBean {
        void destroy();
    }

    static class UserService
            implements InitializingBean, DisposableBean {

        public UserService() {
            System.out.println("UserService constructor");
        }

        @Override
        public void afterPropertiesSet() {
            System.out.println("UserService initialized");
        }

        @Override
        public void destroy() {
            System.out.println("UserService destroyed");
        }
    }

    static class ApplicationContext {

        private final Map<Class<?>, Object> singletonBeans =
                new HashMap<>();

        public <T> T getBean(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (singletonBeans.containsKey(clazz)) {
                return (T) singletonBeans.get(clazz);
            }
            var obj = createBean(clazz);

            if (obj instanceof InitializingBean bean) {
                bean.afterPropertiesSet();
            }
            singletonBeans.put(clazz, obj);
            return (T) obj;
        }

        private <T> T createBean(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {

            var constructor = clazz.getDeclaredConstructors()[0];
            Class<?>[] dep = constructor.getParameterTypes();
            Object[] args = new Object[dep.length];
            for (int i = 0; i < dep.length; i++) {
                args[i] = getBean(dep[i]);
            }

            return (T) constructor.newInstance(args);
        }

        public void close() {
            for (var entry : singletonBeans.entrySet()) {
                var obj = entry.getValue();
                if (obj instanceof DisposableBean) {
                    ((DisposableBean) obj).destroy();
                }
            }
            singletonBeans.clear();

        }
    }

    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        ApplicationContext context =
                new ApplicationContext();

        context.getBean(UserService.class);

        context.close();
    }
}
//----protypeCheck----------//


class ProtoType {

    enum Scope {
        SINGLETON,
        PROTOTYPE
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Component {
        Scope scope() default Scope.SINGLETON;
    }

    @Component(scope = Scope.SINGLETON)
    static class UserService {

        public UserService() {
            System.out.println("Creating UserService");
        }
    }

    @Component(scope = Scope.PROTOTYPE)
    static class ProductService {

        public ProductService() {
            System.out.println("Creating ProductService");
        }
    }

    static class ApplicationContext {

        private final Map<Class<?>, Object> singletonBeans =
                new HashMap<>();

        public <T> T getBean(Class<T> clazz)
                throws Exception {
            Object obj;
            if (getScope(clazz).equals(Scope.SINGLETON)) {
                if (singletonBeans.containsKey(clazz)) {
                    return (T) singletonBeans.get(clazz);
                } else {
                    obj = createBean(clazz);
                    singletonBeans.put(clazz, obj);
                }
            } else {
                obj = createBean(clazz);
            }
            if (obj instanceof BeanLifeCycle.InitializingBean bean) {
                bean.afterPropertiesSet();
            }

            return (T) obj;
        }

        private <T> T createBean(Class<T> clazz)
                throws Exception {

            Constructor<?> constructor =
                    clazz.getConstructors()[0];

            Class<?>[] dependencies =
                    constructor.getParameterTypes();

            Object[] args =
                    new Object[dependencies.length];

            for (int i = 0; i < dependencies.length; i++) {

                args[i] = getBean(dependencies[i]);

            }

            return (T) constructor.newInstance(args);
        }

        private Scope getScope(Class<?> clazz) {

            // TODO
            if (clazz.isAnnotationPresent(Component.class)) {
                var scope = clazz.getAnnotation(Component.class).scope();
                return scope;
            }

            return Scope.SINGLETON;
        }
    }

    public static void main(String[] args)
            throws Exception {

        ApplicationContext context =
                new ApplicationContext();

        UserService u1 =
                context.getBean(UserService.class);

        UserService u2 =
                context.getBean(UserService.class);

        System.out.println(
                "UserService same instance = "
                        + (u1 == u2));

        ProductService p1 =
                context.getBean(ProductService.class);

        ProductService p2 =
                context.getBean(ProductService.class);

        System.out.println(
                "ProductService same instance = "
                        + (p1 == p2));
    }
}

//----------conditionalOnpropertycheck-------//


class ConditionalTest {

    static class DataSource {

        private final String url;

        public DataSource(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    static class ApplicationContext {

        private final Properties properties;

        private final Map<Class<?>, Object> singletonBeans =
                new HashMap<>();

        public ApplicationContext(Properties properties) {
            this.properties = properties;
        }

        public <T> T getBean(Class<T> clazz) throws Exception {
            Object obj;
            if (clazz.equals(DataSource.class) && !singletonBeans.containsKey(clazz)) {
                obj = createDataSource();
            } else if (singletonBeans.containsKey(clazz)) {
                return (T) singletonBeans.get(clazz);
            } else {
                obj = createBean(clazz);
            }
            if (obj instanceof BeanLifeCycle.InitializingBean bean) {
                bean.afterPropertiesSet();
            }
            singletonBeans.put(clazz, obj);
            return (T) obj;
        }

        private <T> T createBean(Class<T> clazz)
                throws Exception {

            Constructor<?> constructor =
                    clazz.getConstructors()[0];

            Class<?>[] dependencies =
                    constructor.getParameterTypes();

            Object[] args =
                    new Object[dependencies.length];

            for (int i = 0; i < dependencies.length; i++) {

                args[i] = getBean(dependencies[i]);

            }

            return (T) constructor.newInstance(args);
        }

        private DataSource createDataSource() throws IllegalStateException {
            if (properties.containsKey("db.url")) {
                return new DataSource(properties.getProperty("db.url"));
            }

            throw new IllegalStateException("");
        }
    }

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();

        properties.put(
                "db.url",
                "jdbc:postgresql://localhost:5432/test");

        ApplicationContext context =
                new ApplicationContext(properties);

        DataSource dataSource =
                context.getBean(DataSource.class);

        System.out.println(
                dataSource.getUrl());
    }
}