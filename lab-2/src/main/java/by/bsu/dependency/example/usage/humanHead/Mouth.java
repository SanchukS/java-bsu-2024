package by.bsu.dependency.example.usage.humanHead;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.usage.Randomizer;

@Bean(name="mouth", scope= BeanScope.PROTOTYPE)
public class Mouth {
    @Inject
    Randomizer randomizer;

    private int volume;

    @PostConstruct
    public void init(Randomizer randomizer) {
        this.randomizer = randomizer;
        volume = this.randomizer.nextInt(0, 100);
    }

    public int getVolume() {
        return volume;
    }

    public String saySomething() {
        return "Mouth says: I want to finish this... (with volume: " + volume + ")";
    }
}
