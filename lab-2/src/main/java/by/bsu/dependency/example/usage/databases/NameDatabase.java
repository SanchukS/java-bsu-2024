package by.bsu.dependency.example.usage.databases;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.PostConstruct;

import java.util.List;

@Bean(name="nameDatabase", scope= BeanScope.SINGLETON)
public class NameDatabase extends AbstractStringDatabase {
    @Override
    @PostConstruct
    void init() {
        data = List.of(
                "Sergey", "Dima", "Kolya", "Alex",
                "Masha", "Katya", "Olga", "Tanya"
        );
    }
}
