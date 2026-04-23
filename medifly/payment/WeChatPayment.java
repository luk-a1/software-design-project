package medifly.payment; 

public class WeChatPayment implements PaymentType { 
    @Override
    public String getTypeName(){ return "WeChat"; } 
}
