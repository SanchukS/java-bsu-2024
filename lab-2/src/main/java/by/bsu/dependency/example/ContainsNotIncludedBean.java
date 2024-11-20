package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name="containsNotIncludedBean", scope= BeanScope.PROTOTYPE)
public class ContainsNotIncludedBean {

    @Inject
    NotIncludedBean notIncludedBean;

    public void doSomething() {
        System.out.println("Try to use notIncludedBean");
        notIncludedBean.printSomething();
    }
}
