package medifly.pricing;

import medifly.store.Product;

public class ProductOffer {
    private final Product product;
    private final double travelCost;
    private final double totalCost;

    public ProductOffer(Product product, double travelCost) {
        this.product = product;
        this.travelCost = travelCost;
        this.totalCost = product.getPrice() + travelCost;
    }

    public Product getProduct() { return product; }
    public double getTravelCost() { return travelCost; }
    public double getTotalCost() { return totalCost; }
}
