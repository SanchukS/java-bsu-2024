package by.bsu.dependency.example.usage.humanHead;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name="head", scope= BeanScope.PROTOTYPE)
public class Head {
    @Inject
    private Mouth mouth;

    @Inject
    private Ears ears;

    @Inject
    private Eyes eyes;

    @Inject
    private Nose nose;

    public void saySomething() {
        System.out.println(mouth.saySomething());
    }

    public void overhearSomething(String object) {
        System.out.println(ears.overhearSomething(object));
    }

    public void sniffSomething(String object) {
        System.out.println(nose.sniffSomething(object));
    }

    public void lookAtSomething(String object) {
        System.out.println(eyes.lookAtSomething(object));
    }

    @Override
    public String toString() {
        return "Mouth volume: " + mouth.getVolume() +
                "\nEyes count: " + eyes.getCount() +
                "\nNose size: " + nose.getSize() +
                "\nEars " + (ears.getEarsExistence() ? "exist" : "don't exist");
    }
}
