package by.bsu.dependency.example.postConstruct;

import by.bsu.dependency.annotation.Bean;

@Bean(name="valueBeanParent")
public class ValueBeanParent {
    public String getValue() {
        return "parent";
    }
}
