package by.bsu.dependency.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import by.bsu.dependency.Exception.ApplicationContextAlreadyStartedException;
import by.bsu.dependency.Exception.ApplicationContextNotStartedException;
import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.Exception.NoSuchBeanDefinitionException;


public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {

    private final Map<String, Class<?>> beanDefinitions;
    private final Map<String, Object> beans = new HashMap<>();

    /**
     * ! Класс существует только для базового примера !
     * <br/>
     * Создает контекст, содержащий классы, переданные в параметре. Полагается на отсутсвие зависимостей в бинах,
     * а также на наличие аннотации {@code @Bean} на переданных классах.
     * <br/>
     * ! Контекст данного типа не занимается внедрением зависимостей !
     * <br/>
     * ! Создает только бины со скоупом {@code SINGLETON} !
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public HardCodedSingletonApplicationContext(Class<?>... beanClasses) {
        this.beanDefinitions = Arrays.stream(beanClasses).collect(
                Collectors.toMap(
                        beanClass -> beanClass.getAnnotation(Bean.class).name(),
                        Function.identity()
                )
        );
    }

    @Override
    public void start() {
        if (isRunning())
            throw new ApplicationContextAlreadyStartedException(this.getClass().getName());

        beans.clear();

        beanDefinitions.forEach((beanName, beanClass) -> beans.put(beanName, instantiateBean(beanClass)));
        status = ContextStatus.STARTED;
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return beanDefinitions.containsKey(name);
    }

    /**
     * В этой реализации отсутствуют проверки статуса контекста (запущен ли он).
     */
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

    /**
     * В этой реализации отсутствуют проверки статуса контекста (запущен ли он) и исключения в случае отсутствия бина
     */
    @Override
    public Object getBean(String name) {
        if (!isRunning())
            throw new ApplicationContextNotStartedException("Caused by getBean");

       if (!containsBean(name)) {
           throw new NoSuchBeanDefinitionException(name);
       }

       return beans.get(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(getBean(getBeanName(clazz)));
    }

    @Override
    public boolean isPrototype(String name) {
        if (!containsBeanDefinition(name))
            throw new NoSuchBeanDefinitionException(name);

        return false;
    }

    @Override
    public boolean isSingleton(String name) {
        if (!containsBeanDefinition(name))
            throw new NoSuchBeanDefinitionException(name);

        return true;
    }
}
