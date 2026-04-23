package medifly.payment; 

public class OctopusPayment implements PaymentType { 
    @Override
    public String getTypeName(){ return "Octopus"; } 
}
