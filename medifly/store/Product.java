package medifly.store;

public class Product {
    private final String productId;
    private final String name;
    private final double price;
    private final boolean prescriptionRequired;
    private final String storeId;
    private final String storeName;

    public Product(String productId, String name, double price, boolean prescriptionRequired, String storeId, String storeName) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.prescriptionRequired = prescriptionRequired;
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
    public String getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
}
