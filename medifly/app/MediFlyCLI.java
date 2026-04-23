package medifly.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import medifly.auth.LoginSystem;
import medifly.delivery.Delivery;
import medifly.delivery.DeliveryDrone;
import medifly.location.HKLocation;
import medifly.model.Customer;
import medifly.model.DrugStoreAdmin;
import medifly.model.User;
import medifly.payment.Payment;
import medifly.payment.PaymentFacade;
import medifly.payment.PaymentMethodEnum;
import medifly.pricing.ProductOffer;
import medifly.store.DrugStore;
import medifly.store.Order;
import medifly.store.Product;
import medifly.system.PharmacySystem;
import medifly.verification.Prescription;
import medifly.verification.VerificationFacade;
import medifly.verification.VerificationStateEnum;

public class MediFlyCLI {
    private final Scanner scanner = new Scanner(System.in);
    private final LoginSystem loginSystem = LoginSystem.getInstance();
    private final PharmacySystem pharmacySystem = PharmacySystem.getInstance();
    private final PaymentFacade paymentFacade = new PaymentFacade();
    private final VerificationFacade verificationFacade = new VerificationFacade();

    private User currentUser;
    private final List<Product> cart = new ArrayList<>();
    private Order currentOrder;
    private Payment currentPayment;

    public MediFlyCLI() {
        if (pharmacySystem.getDrugStores().isEmpty()) {
            pharmacySystem.addDrugStore(DrugStore.seedDefaultStore());
        }
    }

    public void start() {
        banner();
        boolean running = true;
        while (running) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Register Customer");
            System.out.println("2. Register Drug Store");
            System.out.println("3. Login");
            System.out.println("4. List registered drug stores");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> registerCustomer();
                case "2" -> registerDrugStore();
                case "3" -> login();
                case "4" -> listRegisteredDrugStores();
                case "5" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Goodbye from MediFly CLI.");
    }

    private void banner() {
        System.out.println("==============================");
        System.out.println("        MediFly CLI");
        System.out.println(" Hong Kong Delivery Price Demo");
        System.out.println("==============================");
    }

    private HKLocation promptForLocation(String prompt) {
        System.out.println(prompt);
        HKLocation.printOptions();
        System.out.print("Choose location number: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            HKLocation location = HKLocation.fromChoice(choice);
            if (location == null) System.out.println("Invalid location.");
            return location;
        } catch (NumberFormatException e) {
            System.out.println("Invalid location input.");
            return null;
        }
    }

    private void registerCustomer() {
        System.out.println("\n--- Register Customer ---");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email/Phone: ");
        String contact = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Delivery address: ");
        String address = scanner.nextLine().trim();
        HKLocation location = promptForLocation("Select your Hong Kong delivery location:");
        if (location == null) return;
        Customer customer = loginSystem.registerCustomer(name, contact, password, address, location);
        if (customer == null) {
            System.out.println("Customer registration failed: invalid input or account already exists.");
        } else {
            customer.register();
            System.out.println("Customer registered successfully at location: " + location.getDisplayName());
        }
    }

    private void registerDrugStore() {
        System.out.println("\n--- Register Drug Store ---");
        System.out.print("Store name: ");
        String storeName = scanner.nextLine().trim();
        System.out.print("Store address: ");
        String storeAddress = scanner.nextLine().trim();
        HKLocation location = promptForLocation("Select the drug store location:");
        if (location == null) return;
        System.out.print("Drug store contact (email/phone): ");
        String contact = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.println("Expected CSV columns: productId,name,price,prescriptionRequired");
        System.out.print("Inventory CSV file path: ");
        String csvPath = scanner.nextLine().trim();
        try {
            DrugStoreAdmin admin = loginSystem.registerDrugStore(storeName, storeAddress, contact, password, location, csvPath);
            if (admin == null) {
                System.out.println("Drug store registration failed: invalid input or account already exists.");
                return;
            }
            admin.register();
            System.out.println("Drug store registered successfully.");
            System.out.println("Registered store: " + admin.getManagedStore().getStoreName() + " @ " + admin.getManagedStore().getLocation().getDisplayName());
            System.out.println("Imported products: " + admin.getManagedStore().getCatalogue().size());
        } catch (Exception e) {
            System.out.println("Drug store registration failed: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Email/Phone: ");
        String contact = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        User user = loginSystem.login(contact, password);
        if (user == null) {
            System.out.println("Login failed. Check your credentials.");
            return;
        }
        this.currentUser = user;
        currentUser.login();
        System.out.println("Welcome, " + currentUser.getName() + "!");
        if (user instanceof Customer customer) {
            System.out.println("Customer delivery location: " + customer.getLocation().getDisplayName());
            customerMenu(customer);
        } else if (user instanceof DrugStoreAdmin admin) {
            System.out.println("Managed store location: " + admin.getManagedStore().getLocation().getDisplayName());
            drugStoreMenu(admin);
        }
    }

    private void listRegisteredDrugStores() {
        System.out.println("\n--- Registered Drug Stores ---");
        List<DrugStore> stores = pharmacySystem.getDrugStores();
        for (int i = 0; i < stores.size(); i++) {
            DrugStore store = stores.get(i);
            System.out.printf("%d. %s | %s | zone: %s | products: %d%n", i + 1, store.getStoreName(), store.getAddress(), store.getLocation().getDisplayName(), store.getCatalogue().size());
        }
    }

    private void customerMenu(Customer customer) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. Browse all products (personalized cheapest offer)");
            System.out.println("2. Add product to cart");
            System.out.println("3. View cart");
            System.out.println("4. Create order from cart");
            System.out.println("5. Submit prescription for current order");
            System.out.println("6. Pay for current order");
            System.out.println("7. Track delivery");
            System.out.println("8. Simulate next delivery update");
            System.out.println("9. Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> browseAllProducts(customer);
                case "2" -> addToCart(customer);
                case "3" -> viewCart(customer);
                case "4" -> createOrder(customer);
                case "5" -> submitPrescription(customer);
                case "6" -> makePayment(customer);
                case "7" -> trackDelivery(customer);
                case "8" -> simulateDeliveryUpdate(customer);
                case "9" -> loggedOut();
                default -> System.out.println("Invalid option.");
            }
            if ("9".equals(choice)) loggedIn = false;
        }
    }

    private void drugStoreMenu(DrugStoreAdmin admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== Drug Store Menu ===");
            System.out.println("1. View my inventory");
            System.out.println("2. Upload / merge inventory CSV");
            System.out.println("3. View all registered drug stores");
            System.out.println("4. Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewStoreInventory(admin.getManagedStore());
                case "2" -> uploadInventory(admin);
                case "3" -> listRegisteredDrugStores();
                case "4" -> loggedOut();
                default -> System.out.println("Invalid option.");
            }
            if ("4".equals(choice)) loggedIn = false;
        }
    }

    private void browseAllProducts(Customer customer) {
        System.out.println("\n--- Personalized Cheapest Product Offers ---");
        List<ProductOffer> offers = pharmacySystem.getBestOffersForCustomer(customer);
        if (offers.isEmpty()) {
            System.out.println("No products available in the system.");
            return;
        }
        for (int i = 0; i < offers.size(); i++) {
            ProductOffer offer = offers.get(i);
            Product p = offer.getProduct();
            DrugStore store = pharmacySystem.findStoreById(p.getStoreId());
            System.out.printf("%d. %s | medicine HKD %.2f | travel HKD %.2f | total HKD %.2f | store: %s (%s) | Rx: %s%n",
                    i + 1, p.getName(), p.getPrice(), offer.getTravelCost(), offer.getTotalCost(), p.getStoreName(), store.getLocation().getDisplayName(), p.isPrescriptionRequired());
        }
    }

    private void addToCart(Customer customer) {
        browseAllProducts(customer);
        List<ProductOffer> offers = pharmacySystem.getBestOffersForCustomer(customer);
        if (offers.isEmpty()) return;
        System.out.print("Select product number: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            ProductOffer selectedOffer = pharmacySystem.getBestOfferByIndex(customer, choice - 1);
            if (selectedOffer == null) {
                System.out.println("Invalid product selection.");
                return;
            }
            Product selected = selectedOffer.getProduct();
            if (!cart.isEmpty() && !cart.get(0).getStoreId().equals(selected.getStoreId())) {
                System.out.println("For this CLI demo, one order can only contain products from a single drug store.");
                return;
            }
            cart.add(selected);
            System.out.println(selected.getName() + " from " + selected.getStoreName() + " added to cart.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void viewCart(Customer customer) {
        System.out.println("\n--- Cart ---");
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        double subtotal = 0;
        for (int i = 0; i < cart.size(); i++) {
            Product p = cart.get(i);
            subtotal += p.getPrice();
            System.out.printf("%d. %s | HKD %.2f | Store: %s%n", i + 1, p.getName(), p.getPrice(), p.getStoreName());
        }
        DrugStore store = pharmacySystem.findStoreById(cart.get(0).getStoreId());
        double travelCost = pharmacySystem.calculateTravelCost(store, customer);
        System.out.printf("Medicine subtotal: HKD %.2f%n", subtotal);
        System.out.printf("Travel cost (%s -> %s): HKD %.2f%n", store.getLocation().getDisplayName(), customer.getLocation().getDisplayName(), travelCost);
        System.out.printf("Combined total if ordered now: HKD %.2f%n", subtotal + travelCost);
    }

    private void createOrder(Customer customer) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Add products first.");
            return;
        }
        DrugStore store = pharmacySystem.findStoreById(cart.get(0).getStoreId());
        currentOrder = customer.makeOrder(new ArrayList<>(cart), store);
        cart.clear();
        System.out.println("Order created: " + currentOrder.getOrderId());
        System.out.println("Drug store: " + currentOrder.getDrugStore().getStoreName());
        System.out.printf("Medicine subtotal: HKD %.2f%n", currentOrder.getMedicineSubtotal());
        System.out.printf("Travel cost: HKD %.2f%n", currentOrder.getTravelCost());
        System.out.printf("Order total: HKD %.2f%n", currentOrder.getPrice());
        System.out.println("Prescription required: " + currentOrder.checkPrescriptionRequirement());
    }

    private void submitPrescription(Customer customer) {
        if (currentOrder == null) {
            System.out.println("Create an order first.");
            return;
        }
        if (!currentOrder.checkPrescriptionRequirement()) {
            System.out.println("This order does not require a prescription.");
            return;
        }
        System.out.print("Enter prescription document/file name: ");
        String doc = scanner.nextLine().trim();
        Prescription p = customer.submitPrescription(currentOrder.getOrderId(), doc);
        p.attach(customer);
        currentOrder.setPrescription(p);
        verificationFacade.verifyPrescription(doc, VerificationStateEnum.VERIFIED, p);
        System.out.println("Prescription submitted and approved for demo flow.");
    }

    private void makePayment(Customer customer) {
        if (currentOrder == null) {
            System.out.println("Create an order first.");
            return;
        }
        if (currentOrder.checkPrescriptionRequirement() && !currentOrder.hasValidPrescription()) {
            System.out.println("Prescription approval required before payment.");
            return;
        }
        if (currentPayment != null && currentPayment.isCompleted()) {
            System.out.println("Order is already paid.");
            return;
        }
        System.out.println("Choose payment method: 1=Card, 2=Octopus, 3=WeChat");
        String choice = scanner.nextLine().trim();
        PaymentMethodEnum method = switch (choice) {
            case "1" -> PaymentMethodEnum.CARD;
            case "2" -> PaymentMethodEnum.OCTOPUS;
            case "3" -> PaymentMethodEnum.WECHAT;
            default -> null;
        };
        if (method == null) {
            System.out.println("Invalid payment method.");
            return;
        }
        currentPayment = customer.makePayment(currentOrder.getOrderId(), method, currentOrder.getPrice(), paymentFacade);
        currentPayment.attach(customer);
        currentPayment.notifyObservers();
        if (currentPayment.isCompleted()) {
            Delivery delivery = new Delivery("MISSION-" + currentOrder.getOrderId(), new DeliveryDrone("DR-01"));
            delivery.attach(customer);
            currentOrder.setDelivery(delivery);
            System.out.println("Payment completed. Delivery mission created.");
        } else {
            System.out.println("Payment failed or cancelled.");
        }
    }

    private void trackDelivery(Customer customer) {
        if (currentOrder == null || currentOrder.getDelivery() == null) {
            System.out.println("No delivery available yet.");
            return;
        }
        customer.trackDelivery(currentOrder.getDelivery());
    }

    private void simulateDeliveryUpdate(Customer customer) {
        if (currentOrder == null || currentOrder.getDelivery() == null) {
            System.out.println("No delivery available yet. Complete payment first.");
            return;
        }
        Delivery delivery = currentOrder.getDelivery();
        switch (delivery.getStateName()) {
            case "PendingDispatch" -> delivery.dispatchDrone();
            case "ReadyForPickup" -> delivery.updateStatus();
            case "InTransit" -> {
                delivery.getDrone().updateLocation();
                delivery.updateStatus();
            }
            case "Finished" -> {
                customer.confirmPackageReceipt();
                System.out.println("Package receipt already confirmed. Drone returns to base.");
                delivery.getDrone().returnToBase();
            }
            default -> System.out.println("No further simulation step available.");
        }
    }

    private void uploadInventory(DrugStoreAdmin admin) {
        System.out.println("\n--- Upload / Merge Drug Store Inventory ---");
        System.out.println("Expected CSV columns: productId,name,price,prescriptionRequired");
        System.out.print("Enter CSV file path: ");
        String filePath = scanner.nextLine().trim();
        try {
            int merged = admin.getManagedStore().mergeProductsFromCsv(filePath);
            pharmacySystem.refreshAllProducts();
            System.out.println("Inventory merge complete. New products added or updated: " + merged);
        } catch (Exception e) {
            System.out.println("Upload failed: " + e.getMessage());
        }
    }

    private void viewStoreInventory(DrugStore store) {
        System.out.println("\n--- Inventory for " + store.getStoreName() + " ---");
        if (store.getCatalogue().isEmpty()) {
            System.out.println("No products available in this store.");
            return;
        }
        for (int i = 0; i < store.getCatalogue().size(); i++) {
            Product p = store.getCatalogue().get(i);
            System.out.printf("%d. %s | HKD %.2f | Rx: %s%n", i + 1, p.getName(), p.getPrice(), p.isPrescriptionRequired());
        }
    }

    private void loggedOut() {
        if (currentUser != null) currentUser.logout();
        currentUser = null;
        cart.clear();
        currentOrder = null;
        currentPayment = null;
        System.out.println("Logged out.");
    }
}
