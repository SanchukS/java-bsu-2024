package by.bsu.dependency.context;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    protected ContextStatus status = ContextStatus.NOT_STARTED;

    @Override
    public boolean isRunning() {
        return status == ContextStatus.STARTED;
    }
}
