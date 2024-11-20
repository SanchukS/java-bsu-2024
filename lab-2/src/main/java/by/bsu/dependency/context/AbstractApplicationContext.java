package by.bsu.dependency.context;

import by.bsu.dependency.Exception.ApplicationContextAlreadyStartedException;
import by.bsu.dependency.Exception.ApplicationContextNotStartedException;
import by.bsu.dependency.Exception.InvalidInjectionException;
import by.bsu.dependency.Exception.NoSuchBeanDefinitionException;
import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

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

        if (!containsBean(name))
            throw new NoSuchBeanDefinitionException("Bean " + name + " not found");

        if (isSingleton(name)) {
            return singletonBeans.get(name);
        } else {
            Object prototypeBean = instantiateBean(beanDefinitions.get(name));
            injectDependencies(prototypeBean);
            return prototypeBean;
        }
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

    protected void injectDependencies(Object bean) {
        if (bean == null)
            throw new NullPointerException("Bean is null");

        List<Object> injectionBeans = new ArrayList<>();

        Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEach(field -> {
                    var fieldType = field.getType();
                    String fieldBeanName = getBeanName(fieldType);

                    if (!containsBeanDefinition(fieldBeanName)) {
                        throw new NoSuchBeanDefinitionException(
                                "Bean " + fieldBeanName + " not found to inject"
                        );
                    }

                    Object injectionBean;

                    if (isSingleton(fieldBeanName)) {
                        injectionBean = singletonBeans.get(fieldBeanName);
                    } else {
                        injectionBean = instantiateBean(fieldType);
                        injectionBeans.add(injectionBean);
                    }

                    field.setAccessible(true);
                    try {
                        field.set(bean, injectionBean);
                    } catch (IllegalAccessException e) {
                        throw new InvalidInjectionException(e.getMessage());
                    }
                });

        injectionBeans.forEach(this::injectDependencies);
    }
}
