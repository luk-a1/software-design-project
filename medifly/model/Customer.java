package medifly.model;

import java.util.ArrayList;

import medifly.delivery.Delivery;
import medifly.delivery.DeliveryObserver;
import medifly.location.HKLocation;
import medifly.payment.Payment;
import medifly.payment.PaymentFacade;
import medifly.payment.PaymentMethodEnum;
import medifly.payment.PaymentObserver;
import medifly.store.DrugStore;
import medifly.store.Order;
import medifly.store.Product;
import medifly.verification.Prescription;
import medifly.verification.VerificationObserver;

public class Customer extends User implements DeliveryObserver, PaymentObserver, VerificationObserver {
    private final String deliveryAddress;
    private final HKLocation location;

    public Customer(String userId, String name, String contactInformation, String deliveryAddress, HKLocation location) {
        super(userId, name, contactInformation);
        this.deliveryAddress = deliveryAddress;
        this.location = location;
    }

    public Payment makePayment(String orderId, PaymentMethodEnum method, double amount, PaymentFacade facade) {
        return facade.makePayment(orderId, amount, method);
    }

    public Order makeOrder(ArrayList<Product> products, DrugStore store) {
        return new Order(store, products, location);
    }

    public Prescription submitPrescription(String orderId, String fileName) {
        System.out.println("Prescription submitted for order " + orderId + ": " + fileName);
        return new Prescription(orderId + "-PRESC", fileName);
    }

    public void trackDelivery(Delivery delivery) {
        System.out.println("Delivery mission: " + delivery.getMissionId());
        System.out.println("Delivery state: " + delivery.getStateName());
        System.out.println("Drone status: " + delivery.getDrone().getStatus());
    }

    public void confirmPackageReceipt() {
        System.out.println("Customer confirmed package receipt.");
    }

    @Override
    public void deliveryStatusChanged() {
        System.out.println("[Notification] Delivery status changed for customer " + name + ".");
    }

    @Override
    public void onPaymentCompleted(Payment payment) {
        System.out.println("[Notification] Payment status changed to: " + payment.getState().getStatus());
    }

    @Override
    public void onPrescriptionStatusChange(Prescription prescription) {
        System.out.println("[Notification] Prescription status changed: " + prescription.getStatus().getState());
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public HKLocation getLocation() {
        return location;
    }
}
