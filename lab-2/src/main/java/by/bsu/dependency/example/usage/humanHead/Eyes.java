package by.bsu.dependency.example.usage.humanHead;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.usage.Randomizer;

@Bean(name="eyes", scope= BeanScope.PROTOTYPE)
public class Eyes {
    @Inject
    Randomizer randomizer;

    private int count;

    @PostConstruct
    public void init(Randomizer randomizer) {
        this.randomizer = randomizer;
        count = this.randomizer.nextInt(0, 100);
    }

    public int getCount() {
        return count;
    }

    public String lookAtSomething(String object) {
        return "Eyes looks at " + object + " from " + count + " sides";
    }
}
