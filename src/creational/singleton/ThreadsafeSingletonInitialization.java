package creational.singleton;

public class ThreadsafeSingletonInitialization {
    private static ThreadsafeSingletonInitialization instance;

    private ThreadsafeSingletonInitialization() {}

    public static synchronized ThreadsafeSingletonInitialization getInstance() {
        if (instance == null) {
            instance = new ThreadsafeSingletonInitialization();
        }
        return instance;
    }
}
