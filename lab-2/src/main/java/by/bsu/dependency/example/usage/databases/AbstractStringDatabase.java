package by.bsu.dependency.example.usage.databases;

import java.util.List;

public abstract class AbstractStringDatabase implements StringDatabase {
    List<String> data;

    abstract void init();

    @Override
    public List<String> getData() {
        return data;
    }
}
