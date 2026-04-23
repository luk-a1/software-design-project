package medifly.payment; 

public class PaymentVerifier { 
    public boolean verify(Payment payment){ return payment.getAmount() > 0; } 
}
