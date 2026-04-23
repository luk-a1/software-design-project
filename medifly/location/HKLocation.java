package medifly.location;

public enum HKLocation {
    CENTRAL("Central"),
    MONG_KOK("Mong Kok"),
    TSIM_SHA_TSUI("Tsim Sha Tsui"),
    SHA_TIN("Sha Tin"),
    TSEUNG_KWAN_O("Tseung Kwan O");

    private final String displayName;

    HKLocation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static void printOptions() {
        HKLocation[] values = values();
        for (int i = 0; i < values.length; i++) {
            System.out.printf("%d. %s%n", i + 1, values[i].displayName);
        }
    }

    public static HKLocation fromChoice(int choice) {
        HKLocation[] values = values();
        if (choice < 1 || choice > values.length) {
            return null;
        }
        return values[choice - 1];
    }
}
