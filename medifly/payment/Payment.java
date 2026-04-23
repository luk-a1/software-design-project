package medifly.payment;

import java.util.ArrayList;
import java.util.List;

public class Payment {
    private final String orderId;
    private final double amount;
    private final PaymentType paymentType;
    private PaymentState state;
    private final List<PaymentObserver> observers = new ArrayList<>();

    public Payment(String orderId, double amount, PaymentType paymentType) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.state = new PendingState();
    }

    public PaymentState getState() { return state; }
    public void setState(PaymentState state) { this.state = state; notifyObservers(); }
    public void attach(PaymentObserver observer) { observers.add(observer); }
    public void notifyObservers() { for (PaymentObserver o : observers) o.onPaymentCompleted(this); }
    public boolean isCompleted() { return "Completed".equals(state.getStatus()); }
    public double getAmount() { return amount; }
}
