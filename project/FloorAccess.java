import java.util.HashMap;

class FloorAccess implements AccessControl {
    private HashMap<String, String> accessMap = new HashMap<>();

    @Override
    public void grantAccess(String id, String floor) {
        accessMap.put(id, floor);
    }

    @Override
    public String getAccess(String id) {
        return accessMap.getOrDefault(id, "No Floor Access");
    }
}