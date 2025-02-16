import java.util.HashMap;

class RoomAccess implements AccessControl {
    private HashMap<String, String> accessMap = new HashMap<>();

    @Override
    public void grantAccess(String id, String room) {
        accessMap.put(id, room);
    }

    @Override
    public String getAccess(String id) {
        return accessMap.getOrDefault(id, "No room access");
    }
}