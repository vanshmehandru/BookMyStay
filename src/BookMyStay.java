import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Hotel Inventory Manager
class HotelInventory {
    private Map<String, Integer> rooms;

    public HotelInventory() {
        rooms = new HashMap<>();
        rooms.put("STANDARD", 5);
        rooms.put("DELUXE", 3);
        rooms.put("SUITE", 2);
    }

    // Validate room type
    public void validateRoomType(String roomType) throws InvalidBookingException {
        if (!rooms.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }
    }

    // Validate availability
    public void validateAvailability(String roomType, int count) throws InvalidBookingException {
        int available = rooms.get(roomType);
        if (count <= 0) {
            throw new InvalidBookingException("Booking count must be greater than 0.");
        }
        if (available < count) {
            throw new InvalidBookingException("Not enough rooms available. Available: " + available);
        }
    }

    // Book room
    public void bookRoom(String roomType, int count) throws InvalidBookingException {
        // Fail-fast validations
        validateRoomType(roomType);
        validateAvailability(roomType, count);

        // Safe state update
        rooms.put(roomType, rooms.get(roomType) - count);

        System.out.println("Booking successful! " + count + " " + roomType + " room(s) booked.");
    }

    // Display inventory
    public void displayInventory() {
        System.out.println("\nCurrent Room Availability:");
        for (String type : rooms.keySet()) {
            System.out.println(type + ": " + rooms.get(type));
        }
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HotelInventory inventory = new HotelInventory();

        System.out.println("=== Welcome to Book My Stay App ===");

        try {
            System.out.print("Enter Room Type (STANDARD/DELUXE/SUITE): ");
            String roomType = scanner.nextLine().toUpperCase();

            System.out.print("Enter number of rooms: ");
            int count = Integer.parseInt(scanner.nextLine());

            // Attempt booking
            inventory.bookRoom(roomType, count);

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Booking Failed: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number.");

        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
        }

        // System continues safely
        inventory.displayInventory();
        scanner.close();
    }
}