package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    protected ContextStatus status = ContextStatus.NOT_STARTED;

    @Override
    public boolean isRunning() {
        return status == ContextStatus.STARTED;
    }

    protected <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> String getBeanName(Class<T> beanClass) {
        String name;
        if (beanClass.isAnnotationPresent(Bean.class)) {
            name = beanClass.getAnnotation(Bean.class).name();
        } else {
            name = beanClass.getSimpleName();
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        return name;
    }
}
