package by.bsu.dependency.context;

import by.bsu.dependency.Exception.ApplicationContextNotStartedException;
import by.bsu.dependency.Exception.NoSuchBeanDefinitionException;
import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleApplicationContext extends AbstractApplicationContext {
    private final Map<String, Class<?>> singletonBeanDefinitions;
    private final Map<String, Class<?>> prototypeBeanDefinitions;
    private final Map<String, Object> singletonBeans = new HashMap<>();

    /**
     * Создает контекст, содержащий классы, переданные в параметре.
     * <br/>
     * Если на классе нет аннотации {@code @Bean}, имя бина получается из названия класса, скоуп бина по дефолту
     * считается {@code Singleton}.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public SimpleApplicationContext(Class<?>... beanClasses) {
        prototypeBeanDefinitions = Arrays.stream(beanClasses).filter(
                beanClass -> {
                    return beanClass.isAnnotationPresent(Bean.class) &&
                            beanClass.getAnnotation(Bean.class).scope() == BeanScope.PROTOTYPE;
                }
        ).collect(
                Collectors.toMap(
                        this::getBeanName,
                        Function.identity()
                )
        );
        singletonBeanDefinitions = Arrays.stream(beanClasses).filter(
                beanClass -> {
                    return !beanClass.isAnnotationPresent(Bean.class) ||
                            beanClass.getAnnotation(Bean.class).scope() == BeanScope.SINGLETON;
                }
        ).collect(
                Collectors.toMap(
                        this::getBeanName,
                        Function.identity()
                )
        );
    }

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты
     */
    @Override
    public void start() {
        status = ContextStatus.STARTED; // В начале или в конце?
        singletonBeanDefinitions.forEach(
                (name, beanClass) -> singletonBeans.put(name, instantiateBean(beanClass))
        );
        // injectDependencies
    }

    @Override
    public boolean containsBean(String name) {
        if (status == ContextStatus.NOT_STARTED)
            throw new ApplicationContextNotStartedException("");

        return singletonBeans.containsKey(name) ||
                prototypeBeanDefinitions.containsKey(name);
    }

    @Override
    public Object getBean(String name) {
        if (singletonBeans.containsKey(name)) {
            return singletonBeans.get(name);
        } else if (prototypeBeanDefinitions.containsKey(name)) {
            Object prototypeBean = instantiateBean(prototypeBeanDefinitions.get(name));
            injectDependencies(prototypeBean);
            return prototypeBean;
        }
        throw new NoSuchBeanDefinitionException("Bean " + name + " not found");
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return getBean(getBeanName(clazz));
    }

    @Override
    public boolean isPrototype(String name) {
        return prototypeBeanDefinitions.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return singletonBeanDefinitions.containsKey(name);
    }

    private <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void injectDependencies(T bean) {
        List<Field> injectFields = Arrays.stream(bean.getClass().getFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .toList();
        injectFields.stream()
                .forEach(field -> {
                    field.setAccessible(true);
                    var fieldType = field.getType();

                });
    }

    private <T> String getBeanName(Class<T> beanClass) {
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
