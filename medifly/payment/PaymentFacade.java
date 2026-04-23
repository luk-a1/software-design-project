package medifly.payment;

public class PaymentFacade {
    private final PaymentService paymentService = new PaymentService();
    private final PaymentVerifier verifier = new PaymentVerifier();
    private final PaymentTypeFactory paymentTypeFactory = new PaymentTypeFactory();

    public Payment makePayment(String orderId, double amount, PaymentMethodEnum method) {
        PaymentType type = paymentTypeFactory.create(method);
        Payment payment = paymentService.createPayment(orderId, amount, type);
        if (verifier.verify(payment)) paymentService.execute(payment); else paymentService.fail(payment);
        return payment;
    }
}
