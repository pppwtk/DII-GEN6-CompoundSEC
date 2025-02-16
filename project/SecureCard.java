import java.util.HashMap;

class SecureCard {
    private HashMap<String, String> pinDatabase = new HashMap<>();
    private HashMap<String, String> roleDatabase = new HashMap<>();

    public SecureCard() {
        pinDatabase.put("1111", "Plub");
        roleDatabase.put("1111", "Admin");

        pinDatabase.put("2222", "Fah");
        roleDatabase.put("2222", "User");
    }

    public String validatePin(String inputPin) {
        return pinDatabase.getOrDefault(inputPin, null);
    }

    public String getUserRole(String inputPin) {
        return roleDatabase.getOrDefault(inputPin, "User");
    }
}