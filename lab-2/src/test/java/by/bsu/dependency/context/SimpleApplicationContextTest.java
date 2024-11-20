package by.bsu.dependency.context;

import by.bsu.dependency.Exception.ApplicationContextNotStartedException;
import by.bsu.dependency.Exception.NoSuchBeanDefinitionException;
import by.bsu.dependency.example.*;
import by.bsu.dependency.example.postConstruct.BeanWithPostConstructMethod;
import by.bsu.dependency.example.postConstruct.ValueBeanChild;
import by.bsu.dependency.example.postConstruct.ValueBeanParent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleApplicationContextTest {

    private SimpleApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(
                FirstBean.class,
                OtherBean.class,
                FirstPrototypeBean.class,
                SecondPrototypeBean.class,
                NotAnnotatedBean.class,
                ContainsNotIncludedBean.class,
                ValueBeanParent.class,
                ValueBeanChild.class,
                BeanWithPostConstructMethod.class
        );
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean(FirstBean.class)
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
        assertThat(applicationContext.containsBean("FirstBean")).isFalse();
        assertThat(applicationContext.containsBean("notAnnotatedBean")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();

        assertThat(applicationContext.containsBean("valueBeanChild")).isTrue();
        assertThat(applicationContext.containsBean("valueBeanParent")).isTrue();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanByNameReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("notAnnotatedBean")).isNotNull().isInstanceOf(NotAnnotatedBean.class);
        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("firstPrototypeBean")).isNotNull().isInstanceOf(FirstPrototypeBean.class);
        assertThat(applicationContext.getBean("secondPrototypeBean")).isNotNull().isInstanceOf(SecondPrototypeBean.class);
    }

    @Test
    void testGetBeanByTypeReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean(NotAnnotatedBean.class)).isNotNull().isInstanceOf(NotAnnotatedBean.class);
        assertThat(applicationContext.getBean(FirstBean.class)).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean(FirstPrototypeBean.class)).isNotNull().isInstanceOf(FirstPrototypeBean.class);
        assertThat(applicationContext.getBean(SecondPrototypeBean.class)).isNotNull().isInstanceOf(SecondPrototypeBean.class);
    }

    @Test
    void testInjectedDependencies() throws NoSuchFieldException, IllegalAccessException {
        applicationContext.start();

        Object bean = applicationContext.getBean("secondPrototypeBean");
        assertThat(bean).isNotNull().isInstanceOf(SecondPrototypeBean.class);

        Field singletonField = SecondPrototypeBean.class.getDeclaredField("singletonBean");
        Field prototypeField = SecondPrototypeBean.class.getDeclaredField("firstPrototypeBean");

        singletonField.setAccessible(true);
        prototypeField.setAccessible(true);

        assertThat(singletonField.get(bean)).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(prototypeField.get(bean)).isNotNull().isInstanceOf(FirstPrototypeBean.class);
    }

    @Test
    void testPostConstructDependenciesInjection() {
        applicationContext.start();

        BeanWithPostConstructMethod bean = applicationContext.getBean(BeanWithPostConstructMethod.class);
        assertThat(bean).isNotNull();
        assertThat(bean.valueBeanChild).isNotNull().isInstanceOf(ValueBeanChild.class);
        assertThat(bean.valueBeanParent).isNotNull().isInstanceOf(ValueBeanParent.class);
        assertThat(bean.valueBeanChild.getValue()).isEqualTo("child");
        assertThat(bean.valueBeanParent.getValue()).isEqualTo("parent");
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean(ContainsNotIncludedBean.class)
        );
    }

    @Test
    void testIsSingletonReturns() {
        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
        assertThat(applicationContext.isSingleton("notAnnotatedBean")).isTrue();
        assertThat(applicationContext.isSingleton("firstPrototypeBean")).isFalse();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        assertThat(applicationContext.isPrototype("firstBean")).isFalse();
        assertThat(applicationContext.isPrototype("notAnnotatedBean")).isFalse();
        assertThat(applicationContext.isPrototype("firstPrototypeBean")).isTrue();
    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }
}
