import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SecureCard secureCard = new SecureCard();
        FloorAccess floorAccess = new FloorAccess();
        RoomAccess roomAccess = new RoomAccess();

        floorAccess.grantAccess("Plub", "3 Floor");
        roomAccess.grantAccess("Plub", "Room 305");

        floorAccess.grantAccess("Fah", "5 Floor");
        roomAccess.grantAccess("Fah", "Room 502");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your PIN: ");
        String pin = scanner.nextLine();

        String customerName = secureCard.validatePin(pin);
        if (customerName != null) {
            System.out.println("Welcome, " + customerName);
            System.out.println(floorAccess.getAccess(customerName) + " and " + roomAccess.getAccess(customerName));
        } else {
            System.out.println("Invalid PIN. Access Denie");
        }

        scanner.close();
    }
}