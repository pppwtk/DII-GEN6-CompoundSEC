import java.util.HashMap;

public class UserManagement {
    private HashMap<String, String> userDatabase = new HashMap<>();

    public UserManagement() {
        userDatabase.put("Plub", "1111");
        userDatabase.put("Fah", "2222");
    }

    public boolean checkCredentials(String userId, String password) {
        return userDatabase.containsKey(userId) && userDatabase.get(userId).equals(password);
    }
}