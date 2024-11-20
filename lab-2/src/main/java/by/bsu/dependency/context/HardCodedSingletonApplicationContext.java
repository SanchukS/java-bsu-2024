package by.bsu.dependency.context;

import java.util.List;

import by.bsu.dependency.Exception.ApplicationContextAlreadyStartedException;
import by.bsu.dependency.Exception.NotSingletonException;
import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;


public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {

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
        super(returnIfSingleton(beanClasses));
    }

    private static List<Class<?>> returnIfSingleton(Class<?>[] beanClasses) {
        for (var beanClass : beanClasses) {
            if (beanClass.isAnnotationPresent(Bean.class) &&
                    beanClass.getAnnotation(Bean.class).scope() == BeanScope.PROTOTYPE)
                throw new NotSingletonException("");
        }

        return List.of(beanClasses);
    }

    @Override
    public void start() {
        if (isRunning())
            throw new ApplicationContextAlreadyStartedException(this.getClass().getName());

        singletonBeans.clear();

        beanDefinitions.forEach((beanName, beanClass) -> singletonBeans.put(beanName, instantiateBean(beanClass)));
        status = ContextStatus.STARTED;
    }
}
