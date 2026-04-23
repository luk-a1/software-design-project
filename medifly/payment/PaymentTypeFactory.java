package medifly.payment;

public class PaymentTypeFactory {
    public PaymentType create(PaymentMethodEnum type) {
        PaymentType out = null;
        switch (type) {
            case CARD:
                out = new CardPayment();
                break;
            case OCTOPUS:
                out = new OctopusPayment();
                break;
            case WECHAT:
                out = new WeChatPayment();
                break;
        };

        return out;
    }
}
