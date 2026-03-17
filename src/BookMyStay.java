import java.util.*;

// Booking Request
class BookingRequest {
    String guestName;
    String roomType;
    int rooms;

    public BookingRequest(String guestName, String roomType, int rooms) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.rooms = rooms;
    }
}

// Shared Hotel Inventory
class HotelInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public HotelInventory() {
        inventory.put("STANDARD", 5);
        inventory.put("DELUXE", 3);
        inventory.put("SUITE", 2);
    }

    // Critical Section (Thread-safe)
    public synchronized boolean bookRoom(BookingRequest request) {
        int available = inventory.getOrDefault(request.roomType, 0);

        if (available >= request.rooms) {
            System.out.println(Thread.currentThread().getName() +
                    " processing " + request.guestName);

            // Simulate delay (to expose race conditions if not synchronized)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            inventory.put(request.roomType, available - request.rooms);

            System.out.println("Booking SUCCESS for " + request.guestName +
                    " | Rooms Booked: " + request.rooms +
                    " | Remaining: " + inventory.get(request.roomType));

            return true;
        } else {
            System.out.println("Booking FAILED for " + request.guestName +
                    " | Not enough rooms.");
            return false;
        }
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + ": " + inventory.get(type));
        }
    }
}

// Shared Booking Queue
class BookingQueue {
    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.add(request);
    }

    public synchronized BookingRequest getRequest() {
        return queue.poll(); // returns null if empty
    }
}

// Worker Thread
class BookingProcessor extends Thread {
    private BookingQueue queue;
    private HotelInventory inventory;

    public BookingProcessor(BookingQueue queue, HotelInventory inventory, String name) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    public void run() {
        while (true) {
            BookingRequest request;

            // Synchronized retrieval
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) {
                break; // No more requests
            }

            inventory.bookRoom(request);
        }
    }
}

// Main Class
public class BookMyStay {
    public static void main(String[] args) {

        HotelInventory inventory = new HotelInventory();
        BookingQueue queue = new BookingQueue();

        // Simulate multiple guest requests
        queue.addRequest(new BookingRequest("Guest1", "STANDARD", 2));
        queue.addRequest(new BookingRequest("Guest2", "STANDARD", 2));
        queue.addRequest(new BookingRequest("Guest3", "STANDARD", 2));
        queue.addRequest(new BookingRequest("Guest4", "DELUXE", 1));
        queue.addRequest(new BookingRequest("Guest5", "SUITE", 1));

        // Multiple threads (concurrent users)
        BookingProcessor t1 = new BookingProcessor(queue, inventory, "Thread-1");
        BookingProcessor t2 = new BookingProcessor(queue, inventory, "Thread-2");
        BookingProcessor t3 = new BookingProcessor(queue, inventory, "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final state
        inventory.displayInventory();
    }
}