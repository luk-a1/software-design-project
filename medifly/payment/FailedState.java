package medifly.payment; 

public class FailedState implements PaymentState { 
    @Override
    public String getStatus(){ return "Failed"; } 
}
