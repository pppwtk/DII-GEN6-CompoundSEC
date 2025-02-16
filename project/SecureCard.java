import java.util.HashMap;

class SecureCard {
    private HashMap<String, String> pinDatabase = new HashMap<>();

    public SecureCard() {
        pinDatabase.put("1111", "Plub");
        pinDatabase.put("2222", "Fah");
    }

    public String validatePin(String inputPin) {
        return pinDatabase.getOrDefault(inputPin, null);
    }
}
