package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name="containesNotIncludedBean", scope= BeanScope.PROTOTYPE)
public class ContainesNotIncludedBean {

    @Inject
    NotIncludedBean notIncludedBean;

    public void doSomething() {
        System.out.println("Try to use notIncludedBean");
        notIncludedBean.printSomething();
    }
}
