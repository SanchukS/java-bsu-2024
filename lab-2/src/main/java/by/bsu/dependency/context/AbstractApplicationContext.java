package by.bsu.dependency.context;

import by.bsu.dependency.Exception.*;
import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    protected ContextStatus status = ContextStatus.NOT_STARTED;
    protected final Map<String, Class<?>> beanDefinitions;
    protected final Map<String, Object> singletonBeans = new HashMap<>();

    protected AbstractApplicationContext(List<Class<?>> beanClasses) {
        if (beanClasses == null)
            throw new NullPointerException("beanClasses cannot be null");

        beanDefinitions = beanClasses.stream()
                .collect(Collectors.toMap(
                        this::getBeanName,
                        Function.identity())
                );
    }

    @Override
    public boolean isRunning() {
        return status == ContextStatus.STARTED;
    }

    @Override
    public void start() {
        if (isRunning())
            throw new ApplicationContextAlreadyStartedException("");

        singletonBeans.clear();

        beanDefinitions.forEach(
                (name, beanClass) -> {
                    if (isSingleton(name))
                        singletonBeans.put(name, instantiateBean(beanClass));
                }
        );

        singletonBeans.values().forEach(this::injectDependencies);

        status = ContextStatus.STARTED;
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return beanDefinitions.containsKey(name);
    }

    @Override
    public boolean containsBean(String name) {
        if (!isRunning())
            throw new ApplicationContextNotStartedException("");

        return containsBeanDefinition(name);
    }

    @Override
    public boolean containsBean(Class<?> clazz) {
        return containsBean(getBeanName(clazz));
    }

    @Override
    public Object getBean(String name) {
        if (!isRunning())
            throw new ApplicationContextNotStartedException("Caused by getBean");

        return generateBean(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(getBean(getBeanName(clazz)));
    }

    @Override
    public boolean isPrototype(String name) {
        if (!containsBeanDefinition(name)) {
            throw new NoSuchBeanDefinitionException("Bean " + name + " not found");
        }

        var beanClass = beanDefinitions.get(name);
        return beanClass.isAnnotationPresent(Bean.class) &&
                beanClass.getAnnotation(Bean.class).scope() == BeanScope.PROTOTYPE;
    }

    @Override
    public boolean isSingleton(String name) {
        return !isPrototype(name);
    }

    protected <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new InstantiationBeanException(e.getMessage());
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

    private Object generateBean(String beanName) {
        if (!containsBeanDefinition(beanName))
            throw new NoSuchBeanDefinitionException("Bean " + beanName + " not found");

        Object bean;
        if (isSingleton(beanName)) {
            bean = singletonBeans.get(beanName);
        } else {
            bean = instantiateBean(beanDefinitions.get(beanName));
            injectDependencies(bean);
        }
        return bean;
    }

    protected void injectDependencies(Object bean) {

        injectPostConstructDependencies(bean);

        Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .filter(field -> isNullField(field, bean))
                .forEach(field -> {
                    var fieldType = field.getType();
                    String fieldBeanName = getBeanName(fieldType);

                    Object injectionBean = generateBean(fieldBeanName);

                    field.setAccessible(true);
                    try {
                        field.set(bean, injectionBean);
                    } catch (IllegalAccessException e) {
                        throw new InvalidInjectionException(e.getMessage());
                    }
                });
    }

    private boolean isNullField(Field field, Object bean) {
        field.setAccessible(true);
        boolean result;
        try {
            result = (field.get(bean) == null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void injectPostConstructDependencies(Object bean) {
        Arrays.stream(bean.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))
                .forEach(
                        method -> {
                            Object[] arguments = Arrays.stream(method.getParameterTypes())
                                    .map(clazz -> generateBean(getBeanName(clazz)))
                                    .toArray();
                            method.setAccessible(true);
                            try {
                                method.invoke(bean, arguments);
                            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }
}
