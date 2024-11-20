package by.bsu.dependency.example.usage;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.usage.databases.NameDatabase;

import java.util.List;
import java.util.Random;

@Bean(name="random", scope= BeanScope.SINGLETON)
public class Randomizer {
    private Random random;

    @Inject
    private NameDatabase database;

    @PostConstruct
    private void init() {
        random = new Random();
    }

    public int nextInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public String nextName() {
        List<String> data = database.getData();
        return data.get(random.nextInt(data.size()));
    }
}
