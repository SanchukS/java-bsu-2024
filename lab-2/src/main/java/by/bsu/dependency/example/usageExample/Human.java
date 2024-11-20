package by.bsu.dependency.example.usageExample;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.usageExample.humanHead.Head;

@Bean(name="human", scope= BeanScope.PROTOTYPE)
public class Human {
    private String name;
    private int age;

    @Inject
    private Head head;

    @Inject
    private Randomizer randomizer;

    @PostConstruct
    public void init(Randomizer randomizer) {
        this.randomizer = randomizer;
        age = this.randomizer.nextInt(0, 100);
        name = this.randomizer.nextName();
    }

    public void useHead() {
        head.saySomething();
        head.sniffSomething("Flowers");
        head.lookAtSomething("Sun");
        head.overhearSomething("Stone");
    }

    @Override
    public String toString() {
        return "Name: " + name +
                "\nAge: " + age +
                "\nHead:\n" + head;
    }
}
