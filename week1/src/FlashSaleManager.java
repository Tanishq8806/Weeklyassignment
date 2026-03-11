import java.util.*;

public class FlashSaleManager {

    // productId -> stock count
    private HashMap<String, Integer> inventory = new HashMap<>();

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new HashMap<>();

    public FlashSaleManager() {
        inventory.put("IPHONE15_256GB", 100);
        waitingList.put("IPHONE15_256GB", new LinkedHashMap<>());
    }

    // check stock
    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    // purchase item (thread safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = inventory.getOrDefault(productId, 0);

        if (stock > 0) {

            inventory.put(productId, stock - 1);

            return "Success, " + (stock - 1) + " units remaining";
        }
        else {

            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

            int position = queue.size() + 1;

            queue.put(userId, position);

            return "Stock finished. Added to waiting list, position " + position;
        }
    }

    // display waiting list
    public void showWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        System.out.println("Waiting List:");

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() + " -> Position " + entry.getValue());
        }
    }

    public static void main(String[] args) {

        FlashSaleManager manager = new FlashSaleManager();

        String product = "IPHONE15_256GB";

        System.out.println("Stock Check:");
        System.out.println(product + " → " + manager.checkStock(product) + " units available");

        System.out.println();

        System.out.println(manager.purchaseItem(product, 12345));
        System.out.println(manager.purchaseItem(product, 67890));

        // simulate stock finished
        for (int i = 0; i < 100; i++) {
            manager.purchaseItem(product, 20000 + i);
        }

        System.out.println(manager.purchaseItem(product, 99999));

        manager.showWaitingList(product);
    }
}