package by.bsu.dependency.example.usageExample.humanHead;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.usageExample.Randomizer;
import by.bsu.dependency.example.usageExample.exception.NoEarsException;
import lombok.Getter;

@Bean(name="ears", scope= BeanScope.PROTOTYPE)
public class Ears {
    @Inject
    Randomizer randomizer;

    private boolean exist;

    @PostConstruct
    public void init(Randomizer randomizer) {
        this.randomizer = randomizer;
        exist = (this.randomizer.nextInt(0, 1) == 1);
    }

    public boolean getExist() {
        return exist;
    }

    public String overhearSomething(String object) {
        if (exist) {
            return "Ears hear nothing from " + object;
        } else {
            throw new NoEarsException("NO EARS");
        }
    }
}