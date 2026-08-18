package behavioural.strategy.solution;

interface PaymentStrategy {
    void pay(int amount);
}

class CreditCardPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(int amount) {
        System.out.println("Processing credit card payment of " + amount);
    }
}

class UPIPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(int amount) {
        System.out.println("Processing UPI payment of " + amount);
    }
}

class BitcoinPaymentStrategy implements PaymentStrategy {
    @Override
    public void pay(int amount) {
        System.out.println("Processing Bitcoin payment of " + amount);
    }
}

class PaymentProcessor {
    private PaymentStrategy paymentStrategy;

    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(int amount) {
        paymentStrategy.pay(amount);
    }
}

public class GoodPaymentProcessor {
    static void main() {
        PaymentProcessor paymentProcessor = new PaymentProcessor(new CreditCardPaymentStrategy());
        paymentProcessor.processPayment(100);

        paymentProcessor.setPaymentStrategy(new UPIPaymentStrategy());
        paymentProcessor.processPayment(200);

        paymentProcessor.setPaymentStrategy(new BitcoinPaymentStrategy());
        paymentProcessor.processPayment(300);
    }
}
