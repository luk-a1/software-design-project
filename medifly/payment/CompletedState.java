package medifly.payment; 

public class CompletedState implements PaymentState { 
    @Override
    public String getStatus(){ return "Completed"; } 
}
