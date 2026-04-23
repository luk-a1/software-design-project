package medifly.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import medifly.location.HKLocation;

public class DrugStore {
    private final String storeId;
    private final String storeName;
    private final String address;
    private final HKLocation location;
    private final List<Product> catalogue = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    public DrugStore(String storeId, String name, String address, HKLocation location) {
        this.storeId = storeId;
        this.storeName = name;
        this.address = address;
        this.location = location;
    }

    public void addOrder(Order order) { orders.add(order); }
    public List<Product> getCatalogue() { return catalogue; }

    public int mergeProductsFromCsv(String filePath) throws IOException {
        List<Product> imported = readProductsFromCsv(filePath);
        int changedCount = 0;
        for (Product importedProduct : imported) {
            int existingIndex = findProductIndex(importedProduct.getProductId());
            if (existingIndex >= 0) {
                catalogue.set(existingIndex, importedProduct);
            } else {
                catalogue.add(importedProduct);
            }
            changedCount++;
        }
        return changedCount;
    }

    private int findProductIndex(String productId) {
        for (int i = 0; i < catalogue.size(); i++) {
            if (catalogue.get(i).getProductId().equalsIgnoreCase(productId)) {
                return i;
            }
        }
        return -1;
    }

    private List<Product> readProductsFromCsv(String filepath) throws IOException {
        Path path = resolveCsvPath(filepath);
        List<Product> imported = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                if (firstLine && line.toLowerCase().contains("productid")) {
                    firstLine = false;
                    continue;
                }

                firstLine = false;
                String[] parts = line.split(",");

                if (parts.length < 4)  
                    throw new IOException("Invalid CSV row: " + line);

                String productId = parts[0].trim();
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                boolean prescriptionRequired = Boolean.parseBoolean(parts[3].trim());
                imported.add(new Product(productId, name, price, prescriptionRequired, storeId, storeName));
            }
        }
        return imported;
    }

    private Path resolveCsvPath(String input) throws IOException {
        String cleaned = input.replace("\"", "").trim();
        Path[] attempts = new Path[] {
            Paths.get(cleaned),
            Paths.get(System.getProperty("user.dir"), cleaned),
            Paths.get(System.getProperty("user.dir"), "src", "medifly", cleaned),
            Paths.get(System.getProperty("user.dir"), "src", cleaned)
        };
        for (Path attempt : attempts) {
            if (Files.exists(attempt)) {
                return attempt;
            }
        }
        throw new IOException("CSV file not found. Input='" + input + "', workingDir='" + System.getProperty("user.dir") + "'");
    }

    public String getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
    public String getAddress() { return address; }
    public HKLocation getLocation() { return location; }

    public static DrugStore seedDefaultStore() {
        DrugStore store = new DrugStore("STORE-01", "MediFly Central Drug Store", "Queen's Road Central", HKLocation.CENTRAL);
        store.catalogue.add(new Product("P1", "Paracetamol", 30.0, false, store.storeId, store.storeName));
        store.catalogue.add(new Product("P2", "Vitamin C", 60.0, false, store.storeId, store.storeName));
        store.catalogue.add(new Product("P3", "Antibiotic A", 180.0, true, store.storeId, store.storeName));
        store.catalogue.add(new Product("P4", "Pain Relief Cream", 85.0, false, store.storeId, store.storeName));
        return store;
    }
}
