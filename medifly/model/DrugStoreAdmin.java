package medifly.model;

import medifly.store.DrugStore;

public class DrugStoreAdmin extends User {
    private final String employeeId;
    private final DrugStore managedStore;

    public DrugStoreAdmin(String userId, String name, String contactInformation, String employeeId, DrugStore managedStore) {
        super(userId, name, contactInformation);
        this.employeeId = employeeId;
        this.managedStore = managedStore;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public DrugStore getManagedStore() {
        return managedStore;
    }
}
