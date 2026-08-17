package creational.factory.bad;

interface Notification {
    void sendNotification(String message);
}

class EmailNotification implements Notification {
    @Override
    public void sendNotification(String message) {
        System.out.println("Email Notification");
    }
}

class SMSNotification implements Notification {
    @Override
    public void sendNotification(String message) {
        System.out.println("SMS Notification");
    }
}

class BadNotificationService {
    public static Notification sendNotification(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SMSNotification();
            default -> throw new IllegalArgumentException("Invalid type");
        };
    }
}

public class BadNotificationClient {
    static void main() {
        Notification badNotificationService = BadNotificationService.sendNotification("EMAIL");
        badNotificationService.sendNotification("Hi");
    }
}
