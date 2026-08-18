package structural.adapter;

interface PaymentProcessor {

    void processPayment(double amount, String currency);

    boolean isPaymentSuccessful();

    String getTransactionId();
}

class InHousePaymentProcessor implements PaymentProcessor {

    private String transactionId;
    private boolean paymentSuccessful;

    @Override
    public void processPayment(double amount, String currency) {
        System.out.println("InHouseProcessor: Processing " + amount + " " + currency);
        transactionId = "TXN_" + System.currentTimeMillis();
        paymentSuccessful = true;
        System.out.println("InHouseProcessor: Success. Txn ID: " + transactionId);
    }

    @Override
    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }
}

// Now you need to integrate with legacy payment gateway
class LegacyPaymentProcessor {
    private long transactionReferenceNumber;
    private boolean isPaymentSuccessful;

    public void executeTransaction(double totalAmount, String currency) {
        System.out.println("LegacyGateway: Executing " + currency + " " + totalAmount);
        transactionReferenceNumber = System.nanoTime();
        isPaymentSuccessful = true;
        System.out.println("LegacyGateway: Done. Ref: " + transactionReferenceNumber);
    }

    public boolean checkStatus(long ref) {
        System.out.println("LegacyGateway: Checking status for ref: " + ref);
        return isPaymentSuccessful;
    }

    public long getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }
}

// Creating the adapter to make LegacyPaymentProcessor compatible with PaymentProcessor
class LegacyPaymentAdapter implements PaymentProcessor {
    private final LegacyPaymentProcessor legacyProcessor;
    private long currentRef;

    public LegacyPaymentAdapter(LegacyPaymentProcessor legacyProcessor) {
        this.legacyProcessor = legacyProcessor;
    }

    @Override
    public void processPayment(double amount, String currency) {
        System.out.println("Adapter: Translating processPayment() for " + amount + " " + currency);
        legacyProcessor.executeTransaction(amount, currency);
        currentRef = legacyProcessor.getTransactionReferenceNumber(); // Store for later use
    }

    @Override
    public boolean isPaymentSuccessful() {
        return legacyProcessor.checkStatus(currentRef);
    }

    @Override
    public String getTransactionId() {
        return "LEGACY_TXN_" + currentRef;
    }
}

class CheckoutService {
    private final PaymentProcessor paymentProcessor;

    public CheckoutService(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public void checkout(double amount, String currency) {
        System.out.println("Checkout: Processing order for $" + amount + " " + currency);
        paymentProcessor.processPayment(amount, currency);
        if (paymentProcessor.isPaymentSuccessful()) {
            System.out.println("Checkout: Order successful! Txn: "
                    + paymentProcessor.getTransactionId());
        } else {
            System.out.println("Checkout: Order failed.");
        }
    }
}

public class EcommerceSystem {
    static void main() {
        // Modern processor
        PaymentProcessor processor = new InHousePaymentProcessor();
        CheckoutService modernCheckout = new CheckoutService(processor);
        System.out.println("--- Using Modern Processor ---");
        modernCheckout.checkout(199.99, "USD");

        // Legacy gateway through adapter
        System.out.println("\n--- Using Legacy Gateway via Adapter ---");
        LegacyPaymentProcessor legacy = new LegacyPaymentProcessor();
        processor = new LegacyPaymentAdapter(legacy);
        CheckoutService legacyCheckout = new CheckoutService(processor);
        legacyCheckout.checkout(75.50, "USD");
    }
}
