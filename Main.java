import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

interface AccessControl {
    boolean checkAccess(String pin);
}

abstract class Access implements AccessControl {
    protected String accessLevel;

    public Access(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public abstract boolean checkAccess(String pin);
}

class FloorAccess extends Access {
    private Map<String, Integer> pinToFloorMap;

    public FloorAccess() {
        super("Floor");
        pinToFloorMap = new HashMap<>();
        pinToFloorMap.put("1234", 8);
        pinToFloorMap.put("5678", 9);
        pinToFloorMap.put("0000", 10);
    }

    @Override
    public boolean checkAccess(String pin) {
        return pinToFloorMap.containsKey(pin);
    }

    public Integer getFloorByPIN(String pin) {
        return pinToFloorMap.get(pin);
    }
}

class RoomAccess extends Access {
    private Map<String, Integer> pinToRoomMap;

    public RoomAccess() {
        super("Room");
        pinToRoomMap = new HashMap<>();
        pinToRoomMap.put("1234", 806);
        pinToRoomMap.put("5678", 905);
        pinToRoomMap.put("0000", 101);
    }

    @Override
    public boolean checkAccess(String pin) {
        return pinToRoomMap.containsKey(pin);
    }

    public Integer getRoomByPIN(String pin) {
        return pinToRoomMap.get(pin);
    }
}

public class Main {
    public static void main(String[] args) {
        AccessControl floorAccess = new FloorAccess();
        AccessControl roomAccess = new RoomAccess();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your Card PIN: ");
        String enteredPIN = scanner.nextLine();

        Integer floor = ((FloorAccess) floorAccess).getFloorByPIN(enteredPIN);
        Integer room = ((RoomAccess) roomAccess).getRoomByPIN(enteredPIN);

        if (floor != null && room != null) {
            System.out.println("Floor access: " + floor);
            System.out.println("Room number: " + room);
        } else {
            System.out.println("Invalid Card PIN. Access denied.");
        }

        scanner.close();
    }
}
