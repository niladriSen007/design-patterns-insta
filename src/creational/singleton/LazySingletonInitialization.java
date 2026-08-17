package creational.singleton;

public class LazySingletonInitialization {
    // Holds the shared instance but not created yet
    private static LazySingletonInitialization lazyInitializationSingletonInstance;

    // Private constructor prevents creating objects from outside the class
    private LazySingletonInitialization() {
    }

    public static LazySingletonInitialization getInstance() {
        if (lazyInitializationSingletonInstance == null) lazyInitializationSingletonInstance = new LazySingletonInitialization();

        return lazyInitializationSingletonInstance;
    }
}
