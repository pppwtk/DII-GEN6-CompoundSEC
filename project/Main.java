import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SecureCard secureCard = new SecureCard();
        FloorAccess floorAccess = new FloorAccess();
        RoomAccess roomAccess = new RoomAccess();

        floorAccess.grantAccess("Plub", "Floor 3");
        roomAccess.grantAccess("Plub", "Room 305");

        floorAccess.grantAccess("Fah", "Floor 5");
        roomAccess.grantAccess("Fah", "Room 502");

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter your PIN: ");
        String pin = scanner.nextLine();

        String customerName = secureCard.validatePin(pin);
        String role = secureCard.getUserRole(pin);

        if (customerName != null) {
            System.out.println("\nWelcome, " + customerName);

            if (role.equals("Admin")) {
                Admin admin = new Admin(customerName, floorAccess, roomAccess);
                admin.performSpecialAction();
            } else {
                RegularUser user = new RegularUser(customerName, floorAccess, roomAccess);
                user.performSpecialAction();
            }
        } else {
            System.out.println("Invalid PIN. Access denied");
        }

        scanner.close();
    }
}