package by.bsu.dependency.context;

import by.bsu.dependency.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoScanApplicationContextTest {

    private AutoScanApplicationContext context;

    @BeforeEach
    public void init() {
        context = new AutoScanApplicationContext("by.bsu.dependency.example");
    }

    @Test
    public void allBeansFoundTest() {
        context.start();

        assertThat(context.containsBean(FirstBean.class)).isTrue();
        assertThat(context.containsBean(OtherBean.class)).isTrue();
        assertThat(context.containsBean(FirstPrototypeBean.class)).isTrue();
        assertThat(context.containsBean(SecondPrototypeBean.class)).isTrue();
        assertThat(context.containsBean(ContainsNotIncludedBean.class)).isTrue();
    }

    @Test
    public void notBeansNotFoundTest() {
        context.start();

        assertThat(context.containsBean(NotAnnotatedBean.class)).isFalse();
        assertThat(context.containsBean(NotIncludedBean.class)).isFalse();
    }


}
