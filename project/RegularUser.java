class RegularUser extends AbstractUser {
    private FloorAccess floorAccess;
    private RoomAccess roomAccess;

    public RegularUser(String name, FloorAccess floorAccess, RoomAccess roomAccess) {
        super(name, "User");
        this.floorAccess = floorAccess;
        this.roomAccess = roomAccess;
    }

    @Override
    public void performSpecialAction() {
        System.out.println("\nUser Access");
        System.out.println("Floor: " + floorAccess.getAccess(name));
        System.out.println("Room: " + roomAccess.getAccess(name));
    }
}