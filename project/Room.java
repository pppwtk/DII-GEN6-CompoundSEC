public class Room {
    private int roomId;
    private boolean isAvailable;
    private String password;

    public Room(int roomId, boolean isAvailable, String password) {
        this.roomId = roomId;
        this.isAvailable = isAvailable;
        this.password = password;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}