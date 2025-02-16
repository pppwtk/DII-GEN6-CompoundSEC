import java.util.Scanner;

class Admin extends AbstractUser {
    private FloorAccess floorAccess;
    private RoomAccess roomAccess;

    public Admin(String name, FloorAccess floorAccess, RoomAccess roomAccess) {
        super(name, "Admin");
        this.floorAccess = floorAccess;
        this.roomAccess = roomAccess;
    }

    @Override
    public void performSpecialAction() {
        System.out.println("\nAdmin menu");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. View user access");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            if (choice == 1) {
                System.out.print("Enter user name to view access: ");
                String user = scanner.nextLine();
                System.out.println("\nAccess details");
                System.out.println("Floor: " + floorAccess.getAccess(user));
                System.out.println("Room: " + roomAccess.getAccess(user));

            } else if (choice == 2) {
                System.out.println("Log out...");
                break;
            } else {
                System.out.println("Invalid choice");
            }
        }
    }
}