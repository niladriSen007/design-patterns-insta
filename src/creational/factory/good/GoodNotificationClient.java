package creational.factory.good;

interface Notification {
    void sendNotification();
}

class EmailNotification implements Notification {
    @Override
    public void sendNotification() {
        System.out.println("Email Notification");
    }
}

class SMSNotification implements Notification {
    @Override
    public void sendNotification() {
        System.out.println("SMS Notification");
    }
}

class PushNotification implements Notification {
    @Override
    public void sendNotification() {
        System.out.println("Push Notification");
    }
}

interface NotificationFactory {
    Notification createNotification();

    default void sendNotification() {
        createNotification().sendNotification();
    }
}

class EmailNotificationFactory implements NotificationFactory {
    @Override
    public Notification createNotification() {
        return new EmailNotification();
    }
}

class SMSNotificationFactory implements NotificationFactory {
    @Override
    public Notification createNotification() {
        return new SMSNotification();
    }
}

class PushNotificationFactory implements NotificationFactory {
    @Override
    public Notification createNotification() {
        return new PushNotification();
    }
}

public class GoodNotificationClient {
    static void main() {
        NotificationFactory factory = new EmailNotificationFactory();
        factory.sendNotification();

        factory = new SMSNotificationFactory();
        factory.sendNotification();

        factory = new PushNotificationFactory();
        factory.sendNotification();
    }
}
