package medifly.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import medifly.location.DeliveryCostCalculator;
import medifly.model.Customer;
import medifly.pricing.ProductOffer;
import medifly.store.DrugStore;
import medifly.store.Product;

public class PharmacySystem {
    private static final PharmacySystem INSTANCE = new PharmacySystem();

    private final List<DrugStore> drugStores = new ArrayList<>();
    private final List<Product> allProducts = new ArrayList<>();
    private final DeliveryCostCalculator costCalculator = new DeliveryCostCalculator();

    private PharmacySystem() {}

    public static PharmacySystem getInstance() { return INSTANCE; }

    public void addDrugStore(DrugStore store) {
        if (store != null) {
            drugStores.add(store);
            refreshAllProducts();
        }
    }

    public void refreshAllProducts() {
        allProducts.clear();
        for (DrugStore store : drugStores) {
            allProducts.addAll(store.getCatalogue());
        }
    }

    public List<DrugStore> getDrugStores() { return Collections.unmodifiableList(drugStores); }
    public List<Product> getAllProducts() { return Collections.unmodifiableList(allProducts); }

    public DrugStore findStoreById(String storeId) {
        for (DrugStore store : drugStores) {
            if (store.getStoreId().equals(storeId)) return store;
        }
        return null;
    }

    public double calculateTravelCost(DrugStore store, Customer customer) {
        return costCalculator.getTravelCost(store.getLocation(), customer.getLocation());
    }

    public List<ProductOffer> getBestOffersForCustomer(Customer customer) {
        Map<String, ProductOffer> bestByName = new LinkedHashMap<>();
        for (Product product : allProducts) {
            DrugStore store = findStoreById(product.getStoreId());
            double travel = calculateTravelCost(store, customer);
            ProductOffer offer = new ProductOffer(product, travel);
            ProductOffer existing = bestByName.get(product.getName().toLowerCase());
            if (existing == null || offer.getTotalCost() < existing.getTotalCost()) {
                bestByName.put(product.getName().toLowerCase(), offer);
            }
        }
        return new ArrayList<>(bestByName.values());
    }

    public ProductOffer getBestOfferByIndex(Customer customer, int index) {
        List<ProductOffer> offers = getBestOffersForCustomer(customer);
        if (index < 0 || index >= offers.size()) return null;
        return offers.get(index);
    }
}
