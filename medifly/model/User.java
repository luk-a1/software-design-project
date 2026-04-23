package medifly.model;

public abstract class User {
    protected String userId;
    protected String name;
    protected String contactInformation;

    protected User(String userId, String name, String contactInformation) {
        this.userId = userId;
        this.name = name;
        this.contactInformation = contactInformation;
    }

    public String getName() {
        return name;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void login() {
        System.out.println(name + " logged in.");
    }

    public void logout() {
        System.out.println(name + " logged out.");
    }

    public void register() {
        System.out.println(name + " registered.");
    }
}
