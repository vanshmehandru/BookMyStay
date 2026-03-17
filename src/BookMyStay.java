import java.util.*;

// Custom Exception
class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

// Booking class
class Booking {
    String bookingId;
    String roomType;
    int roomsBooked;
    boolean isCancelled;

    public Booking(String bookingId, String roomType, int roomsBooked) {
        this.bookingId = bookingId;
        this.roomType = roomType;
        this.roomsBooked = roomsBooked;
        this.isCancelled = false;
    }
}

// Hotel System
class HotelSystem {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Booking> bookings = new HashMap<>();
    private Stack<String> rollbackStack = new Stack<>();

    public HotelSystem() {
        inventory.put("STANDARD", 5);
        inventory.put("DELUXE", 3);
        inventory.put("SUITE", 2);
    }

    // Book rooms
    public void bookRoom(String bookingId, String roomType, int count) throws BookingException {
        validateRoomType(roomType);

        if (count <= 0) {
            throw new BookingException("Invalid room count.");
        }

        int available = inventory.get(roomType);
        if (available < count) {
            throw new BookingException("Not enough rooms available.");
        }

        inventory.put(roomType, available - count);
        bookings.put(bookingId, new Booking(bookingId, roomType, count));

        System.out.println("Booking successful. ID: " + bookingId);
    }

    // Cancel booking with rollback
    public void cancelBooking(String bookingId) throws BookingException {
        if (!bookings.containsKey(bookingId)) {
            throw new BookingException("Booking does not exist.");
        }

        Booking booking = bookings.get(bookingId);

        if (booking.isCancelled) {
            throw new BookingException("Booking already cancelled.");
        }

        // LIFO rollback tracking
        rollbackStack.push(bookingId);

        // Restore inventory
        inventory.put(booking.roomType,
                inventory.get(booking.roomType) + booking.roomsBooked);

        // Mark cancelled
        booking.isCancelled = true;

        System.out.println("Booking cancelled successfully: " + bookingId);
    }

    // Validate room type
    private void validateRoomType(String roomType) throws BookingException {
        if (!inventory.containsKey(roomType)) {
            throw new BookingException("Invalid room type.");
        }
    }

    // Display inventory
    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + ": " + inventory.get(type));
        }
    }

    // Display rollback stack
    public void showRollbackStack() {
        System.out.println("\nRollback Stack (Recent cancellations): " + rollbackStack);
    }
}

// Main class
public class BookMyStay {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HotelSystem system = new HotelSystem();

        try {
            System.out.println("=== Book My Stay App ===");

            // Booking
            System.out.print("Enter Booking ID: ");
            String id = scanner.nextLine();

            System.out.print("Enter Room Type (STANDARD/DELUXE/SUITE): ");
            String type = scanner.nextLine().toUpperCase();

            System.out.print("Enter number of rooms: ");
            int count = Integer.parseInt(scanner.nextLine());

            system.bookRoom(id, type, count);

            // Cancellation
            System.out.print("\nEnter Booking ID to cancel: ");
            String cancelId = scanner.nextLine();

            system.cancelBooking(cancelId);

        } catch (BookingException e) {
            System.out.println("Error: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Invalid number input.");

        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

        // System remains stable
        system.displayInventory();
        system.showRollbackStack();

        scanner.close();
    }
}