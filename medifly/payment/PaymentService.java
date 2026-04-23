package medifly.payment;

public class PaymentService {
    public Payment createPayment(String orderId, double amount, PaymentType type) { return new Payment(orderId, amount, type); }
    public void execute(Payment payment) { payment.setState(new CompletedState()); }
    public void fail(Payment payment) { payment.setState(new FailedState()); }
}
