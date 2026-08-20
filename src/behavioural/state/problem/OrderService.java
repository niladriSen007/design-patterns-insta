package behavioural.state.problem;

class Order {
    // Lets consider teh status of the order can be one of the following: "NEW", "PAID", "SHIPPED", "DELIVERED"
    private String status;

    public Order(String status) {
        this.status = status;
    }

    public void pay() {
        if (status.equals("PAID")) {
            throw new IllegalStateException("Order is already paid");
        } else if (status.equals("SHIPPED")) {
            throw new IllegalStateException("Order is already paid now it is shipped");
        } else if (status.equals("DELIVERED")) {
            throw new IllegalStateException("Order is already delivered");
        } else {
            status = "PAID";
            System.out.println("Order is paid");
        }
    }

    public void ship() {
        if (status.equals("NEW")) {
            throw new IllegalStateException("Order is not paid yet");
        } else if (status.equals("SHIPPED")) {
            throw new IllegalStateException("Order is already shipped and yet to be delivered");
        } else if (status.equals("DELIVERED")) {
            throw new IllegalStateException("Order is already delivered");
        } else {
            status = "SHIPPED";
            System.out.println("Order is shipped");
        }
    }

    public void cancel() {
        if (status.equals("DELIVERED")) {
            throw new IllegalStateException("Order is already delivered");
        }
        else {
            status = "CANCELLED";
            System.out.println("Order is cancelled");
        }
    }

    public void deliver() {
        if (status.equals("DELIVERED")) {
            throw new IllegalStateException("Order is already delivered");
        } else if (status.equals("PAID")) {
            throw new IllegalStateException("Order is yet to be shipped");
        } else if (status.equals("NEW")) {
            throw new IllegalStateException("Order is not yet paid");
        } else {
            status = "DELIVERED";
            System.out.println("Order is delivered");
        }
    }
}


public class OrderService {
    static void main(String[] args) {
        Order order = new Order("NEW");
        order.pay();
        order.deliver(); // This will throw an exception because the order is not yet shipped
        order.ship();
        order.cancel(); // This will throw an exception because the order is already delivered
    }
}
