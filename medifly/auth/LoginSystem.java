package medifly.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import medifly.location.HKLocation;
import medifly.model.Customer;
import medifly.model.DrugStoreAdmin;
import medifly.model.User;
import medifly.store.DrugStore;
import medifly.system.PharmacySystem;

public class LoginSystem {
    private static final LoginSystem INSTANCE = new LoginSystem();
    private final Map<String, User> usersByContact = new HashMap<>();
    private final Map<String, String> passwordHashes = new HashMap<>();

    private LoginSystem() {}

    public static LoginSystem getInstance() { return INSTANCE; }

    public Customer registerCustomer(String name, String contact, String password, String address, HKLocation location) {
        if (invalidCommon(name, contact, password) || isBlank(address) || location == null) return null;
        String key = normalized(contact);
        if (usersByContact.containsKey(key)) return null;
        Customer customer = new Customer(UUID.randomUUID().toString(), name, contact, address, location);
        storeUser(customer, password);
        return customer;
    }

    public DrugStoreAdmin registerDrugStore(String name, String address, String contact, String password, HKLocation location, String csvPath) throws Exception {
        if (invalidCommon(name, contact, password) || isBlank(address) || isBlank(csvPath) || location == null) return null;
        String key = normalized(contact);
        if (usersByContact.containsKey(key)) return null;

        String storeId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        DrugStore store = new DrugStore(storeId, name, address, location);
        store.mergeProductsFromCsv(csvPath);
        DrugStoreAdmin admin = new DrugStoreAdmin(UUID.randomUUID().toString(), name + " Manager", contact, storeId, store);
        storeUser(admin, password);
        PharmacySystem.getInstance().addDrugStore(store);
        return admin;
    }

    public User login(String contact, String password) {
        if (isBlank(contact) || isBlank(password)) return null;
        String key = normalized(contact);
        if (!usersByContact.containsKey(key)) return null;
        String expectedHash = passwordHashes.get(key);
        return expectedHash.equals(hash(password)) ? usersByContact.get(key) : null;
    }

    private void storeUser(User user, String password) {
        String key = normalized(user.getContactInformation());
        usersByContact.put(key, user);
        passwordHashes.put(key, hash(password));
    }

    private boolean invalidCommon(String name, String contact, String password) {
        return isBlank(name) || isBlank(contact) || isBlank(password) || password.length() < 6;
    }

    private String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
