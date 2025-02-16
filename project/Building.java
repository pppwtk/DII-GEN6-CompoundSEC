import java.util.HashMap;
import java.util.Map;

public class Building {
    private Map<Integer, String> roomAccessMap = new HashMap<>();

    public boolean validateAccess(String userId, int roomId) {
        return roomAccessMap.containsKey(roomId);
    }

    public void setRoomPassword(int roomId, String password) {
        roomAccessMap.put(roomId, password);
    }

    public boolean isRoomAvailable(int roomId) {
        return roomAccessMap.containsKey(roomId);
    }
}