import java.util.ArrayList;
/**
 * RunLocalTestCase.java
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Arjun Anilkumar
 * @version April 6th, 2025
 */
public class RunLocalTestCase {
    public static void main(String[] args) {
        MainDatabase database = new MainDatabase();

        UserManager userManager = database.getUserManager();
        ItemManager itemManager = database.getItemManager();
        MessageManager messageManager = database.getMessageManager();

        System.out.println("Starting comprehensive test...");

        // Step 1: Add multiple users
        System.out.println("\nAdding users...");
        User user1 = new User("user1", "password1", 100.0);
        User user2 = new User("user2", "password2", 200.0);
        User user3 = new User("user3", "password3", 300.0);
        userManager.addUser(user1);
        userManager.addUser(user2);
        userManager.addUser(user3);

        // Verify users were added
        assert userManager.getUser("user1") != null : "User1 not added!";
        assert userManager.getUser("user2") != null : "User2 not added!";
        assert userManager.getUser("user3") != null : "User3 not added!";
        System.out.println("Users added successfully!");

        // Step 2: Add items for each user
        System.out.println("\nAdding items...");
        Item item1 = new Item("Laptop", "High-performance laptop", 999.99);
        item1.setSellerName("user1");
        Item item2 = new Item("Phone", "Latest smartphone", 499.99);
        item2.setSellerName("user2");
        Item item3 = new Item("Tablet", "Lightweight tablet", 299.99);
        item3.setSellerName("user3");
        itemManager.addItem(item1);
        itemManager.addItem(item2);
        itemManager.addItem(item3);

        // Verify items were added
        ArrayList<Item> items = itemManager.searchItemsByName("Laptop");
        assert items.size() == 1 : "Laptop not added!";
        System.out.println("Items added successfully!");

        // Step 3: Perform transactions (buying items)
        System.out.println("\nPerforming transactions...");
        User buyer = userManager.getUser("user2");
        Item itemToBuy = itemManager.searchItemsByName("Laptop").get(0);
        assert buyer.getBalance() >= itemToBuy.getPrice() : "Buyer does not have enough balance!";
        userManager.updateUserBalance(buyer.getUsername(), buyer.getBalance() - itemToBuy.getPrice());
        User seller = userManager.getUser(itemToBuy.getSellerName());
        userManager.updateUserBalance(seller.getUsername(), seller.getBalance() + itemToBuy.getPrice());
        itemManager.deleteItemByNameAndSeller(itemToBuy.getName(), itemToBuy.getSellerName());
        System.out.println("Transaction completed successfully!");

        // Verify balances
        assert userManager.getUser("user2").getBalance() == 200.0 - 999.99 : "Buyer's balance not updated!";
        assert userManager.getUser("user1").getBalance() == 100.0 + 999.99 : "Seller's balance not updated!";
        System.out.println("Balances updated successfully!");

        // Step 4: Delete a user and their items
        System.out.println("\nDeleting user and their items...");
        userManager.deleteUser("user3");
        itemManager.deleteItemsBySeller("user3");

        // Verify user and items were deleted
        assert userManager.getUser("user3") == null : "User3 not deleted!";
        assert itemManager.searchItemsByName("Tablet").isEmpty() : "User3's items not deleted!";
        System.out.println("User and their items deleted successfully!");

        // Step 5: Test messaging
        System.out.println("\nTesting messaging...");
        messageManager.sendMessage("user1", "user2", "Is the phone still available?");

        // Verify message was added
        ArrayList<Message> messages = messageManager.getMessagesForUser("user2");
        assert messages.size() == 1 : "Message not added!";
        assert messages.get(0).getContent().equals("Is the phone still available?") : "Message content incorrect!";
        System.out.println("Messaging tested successfully!");

        // Step 6: Search for items
        System.out.println("\nTesting item search...");
        ArrayList<Item> searchResults = itemManager.searchItemsByName("Phone");
        assert searchResults.size() == 1 : "Item search by name failed!";

        searchResults = itemManager.searchItemsByPriceRange(400.0, 600.0);
        assert searchResults.size() == 1 : "Item search by price range failed!";

        System.out.println("Item search tested successfully!");

        System.out.println("\nAll tests passed successfully!");
    }
}