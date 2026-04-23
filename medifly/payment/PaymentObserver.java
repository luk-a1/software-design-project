package medifly.payment; 

public interface PaymentObserver { 
    void onPaymentCompleted(Payment payment); 
}
