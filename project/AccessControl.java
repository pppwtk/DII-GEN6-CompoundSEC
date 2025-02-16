import java.util.HashMap;

interface AccessControl {
    void grantAccess(String id, String location);
    String getAccess(String id);
}

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

class RoomAccess implements AccessControl {
    private HashMap<String, String> accessMap = new HashMap<>();

    @Override
    public void grantAccess(String id, String room) {
        accessMap.put(id, room);
    }

    @Override
    public String getAccess(String id) {
        return accessMap.getOrDefault(id, "No Room Access");
    }
}
