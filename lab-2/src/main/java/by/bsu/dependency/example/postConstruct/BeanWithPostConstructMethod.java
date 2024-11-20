package by.bsu.dependency.example.postConstruct;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name="beanWithPostConstructMethod", scope= BeanScope.PROTOTYPE)
public class BeanWithPostConstructMethod {
    @Inject
    public ValueBeanParent valueBeanChild;

    @Inject
    public ValueBeanParent valueBeanParent;

    @PostConstruct
    public void init(ValueBeanChild valueBeanChild) {
        this.valueBeanChild = valueBeanChild;
    }
}
