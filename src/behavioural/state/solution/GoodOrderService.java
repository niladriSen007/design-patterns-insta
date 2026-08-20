package behavioural.state.solution;

interface OrderState {
    void pay(Order order);

    void ship(Order order);

    void deliver(Order order);

    void cancel(Order order);
}

class Order{
    private OrderState orderState;

    public Order(OrderState orderState){
        this.orderState = orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public void pay() {
        orderState.pay(this);
    }

    public void ship() {
        orderState.ship(this);
    }

    public void deliver() {
        orderState.deliver(this);
    }

    public void cancel() {
        orderState.cancel(this);
    }
}

class NewOrderState implements OrderState {
    @Override
    public void pay(Order order) {
        System.out.println("Order is paid");
        order.setOrderState(new PaidOrderState());
    }

    @Override
    public void ship(Order order) {
        throw new IllegalStateException("Order is not paid yet");
    }

    @Override
    public void deliver(Order order) {
        throw new IllegalStateException("Order is not paid yet");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order is cancelled");
        order.setOrderState(new CancelledOrderState());
    }
}

class PaidOrderState implements OrderState {
    @Override
    public void pay(Order order) {
        throw new IllegalStateException("Order is already paid");
    }

    @Override
    public void ship(Order order) {
        System.out.println("Order is shipped");
        order.setOrderState(new ShippedOrderState());
    }

    @Override
    public void deliver(Order order) {
        throw new IllegalStateException("Order is not shipped yet");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("Order is cancelled");
        order.setOrderState(new CancelledOrderState());
    }
}

class ShippedOrderState implements OrderState {
    @Override
    public void pay(Order order) {
        throw new IllegalStateException("Order is already paid");
    }

    @Override
    public void ship(Order order) {
        throw new IllegalStateException("Order is already shipped");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("Order is delivered");
        order.setOrderState(new DeliveredOrderState());
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Order is already shipped and yet to be delivered");
    }
}


class DeliveredOrderState implements OrderState {
    @Override
    public void pay(Order order) {
        throw new IllegalStateException("Order is already delivered");
    }

    @Override
    public void ship(Order order) {
        throw new IllegalStateException("Order is already delivered");
    }

    @Override
    public void deliver(Order order) {
        throw new IllegalStateException("Order is already delivered");
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Order is already delivered");
    }
}

class CancelledOrderState implements OrderState {
    @Override
    public void pay(Order order) {
        throw new IllegalStateException("Order is cancelled");
    }

    @Override
    public void ship(Order order) {
        throw new IllegalStateException("Order is cancelled");
    }

    @Override
    public void deliver(Order order) {
        throw new IllegalStateException("Order is cancelled");
    }

    @Override
    public void cancel(Order order) {
        throw new IllegalStateException("Order is already cancelled");
    }
}

public class GoodOrderService {
    static void main() {
        Order order = new Order(new NewOrderState());
        order.pay();
        order.ship();
        order.deliver();
        order.cancel(); // This will throw an exception because the order is already delivered
    }
}
