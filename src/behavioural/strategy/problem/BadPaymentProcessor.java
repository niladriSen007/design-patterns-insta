package behavioural.strategy.problem;

public class BadPaymentProcessor {

    static void processPayment(String type){

//        if(type.equals("CREDIT_CARD")){
//            System.out.println("Processing credit card payment");
//        } else if(type.equals("PAYPAL")){
//            System.out.println("Processing PayPal payment");
//        } else if(type.equals("BITCOIN")){
//            System.out.println("Processing Bitcoin payment");
//        } else {
//            throw new IllegalArgumentException("Invalid payment type");
//        }

        switch (type) {
            case "CREDIT_CARD" -> System.out.println("Processing credit card payment");
            case "PAYPAL" -> System.out.println("Processing PayPal payment");
            case "BITCOIN" -> System.out.println("Processing Bitcoin payment");
            default -> throw new IllegalArgumentException("Invalid payment type");
        }
    }

    static void main() {
        processPayment("CREDIT_CARD");
        processPayment("PAYPAL");
        processPayment("BITCOIN");
    }
}

