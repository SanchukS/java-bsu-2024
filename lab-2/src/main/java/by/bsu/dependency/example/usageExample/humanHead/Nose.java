package by.bsu.dependency.example.usageExample.humanHead;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.usageExample.Randomizer;
import lombok.Getter;

@Bean(name="NOSE", scope= BeanScope.PROTOTYPE)
public class Nose {
    @Inject
    Randomizer randomizer;

    @Getter
    private int size;

    @PostConstruct
    private void init(Randomizer randomizer) {
        this.randomizer = randomizer;
        size = this.randomizer.nextInt(0, 100);
    }

    public String sniffSomething(String object) {
        return "Nose sniffs " + size + "smells from " + object;
    }
}
