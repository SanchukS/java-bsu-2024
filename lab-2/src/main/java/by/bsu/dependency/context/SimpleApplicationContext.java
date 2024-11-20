package by.bsu.dependency.context;

import by.bsu.dependency.Exception.ApplicationContextAlreadyStartedException;
import by.bsu.dependency.Exception.ApplicationContextNotStartedException;
import by.bsu.dependency.Exception.InvalidInjectionException;
import by.bsu.dependency.Exception.NoSuchBeanDefinitionException;
import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

import java.util.*;

public class SimpleApplicationContext extends AbstractApplicationContext {
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
        super(List.of(beanClasses));
    }
}
