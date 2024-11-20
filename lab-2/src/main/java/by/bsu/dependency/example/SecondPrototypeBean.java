package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name="secondPrototypeBean", scope= BeanScope.PROTOTYPE)
public class SecondPrototypeBean {

    @Inject
    FirstPrototypeBean firstPrototypeBean;

    @Inject
    FirstBean singletonBean;

    void doSomethingWithFirst() {
        System.out.println("Trying to shake first prototype bean...");
        firstPrototypeBean.doSomething();
    }
}
