package medifly.payment; 

public class CardPayment implements PaymentType { 
    @Override
    public String getTypeName(){ return "Card"; } 
}
