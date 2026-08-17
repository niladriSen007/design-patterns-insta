package creational.singleton;

public class EagerSingletonInitialization {
    private static final EagerSingletonInitialization instance = new EagerSingletonInitialization();

    private EagerSingletonInitialization() {}

    public static EagerSingletonInitialization getInstance() {
        return instance;
    }
}
