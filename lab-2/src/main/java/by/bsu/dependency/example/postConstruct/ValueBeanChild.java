package by.bsu.dependency.example.postConstruct;

import by.bsu.dependency.annotation.Bean;

@Bean(name="valueBeanChild")
public class ValueBeanChild extends ValueBeanParent {
    @Override
    public String getValue() {
        return "child";
    }
}
