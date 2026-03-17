import java.io.*;
import java.util.*;

// Booking class (Serializable)
class Booking implements Serializable {
    String bookingId;
    String roomType;
    int roomsBooked;

    public Booking(String bookingId, String roomType, int roomsBooked) {
        this.bookingId = bookingId;
        this.roomType = roomType;
        this.roomsBooked = roomsBooked;
    }

    public String toString() {
        return bookingId + " | " + roomType + " | " + roomsBooked;
    }
}

// Wrapper class to persist full system state
class HotelData implements Serializable {
    Map<String, Integer> inventory;
    Map<String, Booking> bookings;

    public HotelData(Map<String, Integer> inventory, Map<String, Booking> bookings) {
        this.inventory = inventory;
        this.bookings = bookings;
    }
}

// Persistence Service
class PersistenceService {
    private static final String FILE_NAME = "hotel_data.ser";

    // Save state
    public static void save(HotelData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(data);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Load state
    public static HotelData load() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No previous data found. Starting fresh.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("System state restored successfully.");
            return (HotelData) ois.readObject();
        } catch (Exception e) {
            System.out.println("Corrupted data. Starting fresh.");
            return null;
        }
    }
}

// Main Hotel System
class HotelSystem {
    Map<String, Integer> inventory = new HashMap<>();
    Map<String, Booking> bookings = new HashMap<>();

    public HotelSystem() {
        // Try restoring data
        HotelData data = PersistenceService.load();

        if (data != null) {
            this.inventory = data.inventory;
            this.bookings = data.bookings;
        } else {
            // Default state
            inventory.put("STANDARD", 5);
            inventory.put("DELUXE", 3);
            inventory.put("SUITE", 2);
        }
    }

    // Book room
    public void bookRoom(String id, String type, int count) {
        if (!inventory.containsKey(type)) {
            System.out.println("Invalid room type.");
            return;
        }

        int available = inventory.get(type);

        if (available < count) {
            System.out.println("Not enough rooms available.");
            return;
        }

        inventory.put(type, available - count);
        bookings.put(id, new Booking(id, type, count));

        System.out.println("Booking successful: " + id);
    }

    // Display data
    public void display() {
        System.out.println("\nInventory:");
        for (String key : inventory.keySet()) {
            System.out.println(key + ": " + inventory.get(key));
        }

        System.out.println("\nBookings:");
        for (Booking b : bookings.values()) {
            System.out.println(b);
        }
    }

    // Save before shutdown
    public void shutdown() {
        PersistenceService.save(new HotelData(inventory, bookings));
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HotelSystem system = new HotelSystem();

        System.out.println("=== Book My Stay App (Persistent Mode) ===");

        try {
            System.out.print("Enter Booking ID: ");
            String id = scanner.nextLine();

            System.out.print("Enter Room Type (STANDARD/DELUXE/SUITE): ");
            String type = scanner.nextLine().toUpperCase();

            System.out.print("Enter number of rooms: ");
            int count = Integer.parseInt(scanner.nextLine());

            system.bookRoom(id, type, count);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        system.display();

        // Save before exit
        system.shutdown();

        scanner.close();
    }
}