package medifly.payment; 

public class PendingState implements PaymentState { 
    @Override
    public String getStatus(){ return "Pending"; } 
}
