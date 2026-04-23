package medifly.store;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import medifly.delivery.Delivery;
import medifly.location.DeliveryCostCalculator;
import medifly.location.HKLocation;
import medifly.verification.Prescription;
import medifly.verification.VerificationObserver;

public class Order implements VerificationObserver {
    private final List<Product> products = new ArrayList<>();
    private final String orderId;
    private final DrugStore drugStore;
    private final double medicineSubtotal;
    private final double travelCost;
    private final double price;
    private final boolean prescriptionRequired;
    private boolean prescriptionApproved;
    private Delivery delivery;
    private Prescription prescription;

    public Order(DrugStore drugStore, List<Product> products, HKLocation customerLocation) {
        this.orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.drugStore = drugStore;
        this.products.addAll(products);
        this.medicineSubtotal = products.stream().mapToDouble(Product::getPrice).sum();
        this.travelCost = new DeliveryCostCalculator().getTravelCost(drugStore.getLocation(), customerLocation);
        this.price = medicineSubtotal + travelCost;
        this.prescriptionRequired = products.stream().anyMatch(Product::isPrescriptionRequired);
        this.prescriptionApproved = !this.prescriptionRequired;
        drugStore.addOrder(this);
    }

    public double getMedicineSubtotal() { return medicineSubtotal; }
    public double getTravelCost() { return travelCost; }
    public double getPrice() { return price; }
    public DrugStore getDrugStore() { return drugStore; }
    public String getOrderId() { return orderId; }
    public Delivery getDelivery() { return delivery; }
    public void setDelivery(Delivery delivery) { this.delivery = delivery; }
    public boolean checkPrescriptionRequirement() { return prescriptionRequired; }
    public boolean hasValidPrescription() { return !prescriptionRequired || prescriptionApproved; }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
        this.prescription.attach(this);
    }

    @Override
    public void onPrescriptionStatusChange(Prescription prescription) {
        this.prescriptionApproved = "Verified".equals(prescription.getStatus().getState());
        System.out.println("Order " + orderId + " prescription updated: approved=" + prescriptionApproved);
    }
}
