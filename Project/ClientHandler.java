import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Client Handler
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Shankh Gupta
 * @version April 17th, 2025
 */

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final UserManager userManager;
    private final ItemManager itemManager;
    private final MessageManager messageManager;
    private final RatingManager ratingManager;
    private final SoldItemManager soldItemManager;

    private String currentUser = null;

    public ClientHandler(Socket socket, UserManager userManager, ItemManager itemManager,
                         MessageManager messageManager, RatingManager ratingManager,
                         SoldItemManager soldItemManager) {
        this.clientSocket = socket;
        this.userManager = userManager;
        this.itemManager = itemManager;
        this.messageManager = messageManager;
        this.ratingManager = ratingManager;
        this.soldItemManager = soldItemManager;
    }

    private boolean exit = false;

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            out.println("Welcome to the Marketplace!");
            while (true) {
                out.println("\n1. Login\n2. Create Account\n3. Exit\n4. Delete Account \nEnter option:");
                String input = in.readLine();

                if (input == null || input.equals("3")) {
                    out.println("Goodbye!");
                    out.flush();
                    exit = true;
                    return;
                }

                switch (input) {
                    case "1":
                        out.println("Enter username:");
                        String username = in.readLine();
                        out.println("Enter password:");
                        String password = in.readLine();

                        User user = userManager.getUser(username);
                        if (user != null && user.login(username, password)) {
                            currentUser = username;
                            out.println("Login successful. Welcome " + username);
                            showMainMenu(in, out);
                        } else {
                            out.println("Invalid credentials.");
                        }
                        break;
                    case "2":
                        out.println("Enter new username:");
                        String newUser = in.readLine();
                        out.println("Enter password:");
                        String newPass = in.readLine();
                        out.println("Enter initial balance:");
                        double balance = Double.parseDouble(in.readLine());

                        if (userManager.getUser(newUser) == null) {
                            User created = new User(newUser, newPass, balance);
                            userManager.addUser(created);
                            out.println("Account created successfully.");
                        } else {
                            out.println("Username already exists.");
                        }
                        break;
                    case "4": // Delete Account
                        out.println("Enter your username:");
                        String delUser = in.readLine();
                        out.println("Enter password:");
                        String delPass = in.readLine();
                        User acc = userManager.getUser(delUser);
                        if (acc != null && acc.login(delUser, delPass)) {
                            userManager.deleteUser(delUser);
                            itemManager.deleteItemsBySeller(delUser);
                            out.println("Account deleted successfully.");
                        } else {
                            out.println("Invalid credentials.");
                        }
                        break;
                    default:
                        out.println("Invalid option.");
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket.");
            }
        }
    }

    private void showMainMenu(BufferedReader in, PrintWriter out) throws IOException {
        while (!exit) {
            out.println();
            out.println("1. List Item");
            out.println("2. Search Item");
            out.println("3. Buy Item");
            out.println("4. View Messages");
            out.println("5. Send Message");
            out.println("6. Rate Seller");
            out.println("7. View Seller Rating");
            out.println("8. View Sold Items");
            out.println("9. Search By Category");
            out.println("10. Search by Seller");
            out.println("11. Delete Listing");
            out.println("12. Logout");
            out.println("13. View Balance");
            out.println("Enter option:");
            out.flush();
            String input = in.readLine();
            switch (input) {
                case "1":
                    out.println("Enter item name:");
                    String name = in.readLine();
                    out.println("Enter description:");
                    String desc = in.readLine();

                    double price = -1; // Initialize with an invalid price
                    while (price < 0) { // Loop until a valid non-negative price is entered
                        out.println("Enter price:");
                        try {
                            price = Double.parseDouble(in.readLine());
                            if (price < 0) {
                                out.println("Price cannot be negative. Please enter a valid price.");
                            }
                        } catch (NumberFormatException e) {
                            out.println("Invalid input. Please enter a valid number for the price.");
                            price = -1; // Reset price to ensure loop continues
                        }
                    }

                    out.println("Enter category:");
                    String category = in.readLine();

                    Item item = new Item(name, desc, price, category);
                    item.setSellerName(currentUser);
                    itemManager.addItem(item);
                    out.println("Item listed.");
                    break;
                case "2":
                    out.println("Enter keyword to search:");
                    String keyword = in.readLine();
                    ArrayList<Item> results = itemManager.searchItemsByName(keyword);
                    if (results.isEmpty()) {
                        out.println("No items found.");
                    } else {
                        for (Item res : results) {
                            out.println(res.getName() + " - $" + res.getPrice() + " - Seller: " + res.getSeller());
                        }
                    }
                    break;
                case "3": // Buy Item
                    out.println("Enter item name to buy:");
                    String itemName = in.readLine();
                    ArrayList<Item> buyItems = itemManager.searchItemsByName(itemName);
                    if (buyItems.isEmpty()) {
                        out.println("Item not found.");
                        break;
                    }
                    Item selected = buyItems.get(0);
                    User buyer = userManager.getUser(currentUser);
                    User seller = userManager.getUser(selected.getSeller());

                    // Add check to prevent buying own item
                    if (currentUser.equals(selected.getSeller())) {
                        out.println("You cannot buy your own item.");
                        break;
                    }

                    if (buyer.getBalance() < selected.getPrice()) {
                        out.println("Insufficient balance.");
                        break;
                    }
                    buyer.setBalance(buyer.getBalance() - selected.getPrice());
                    seller.setBalance(seller.getBalance() + selected.getPrice());
                    userManager.saveUsers();
                    itemManager.removeItem(selected.getItemId());
                    soldItemManager.recordSale(selected);
                    out.println("Item purchased. Your new balance: $" + buyer.getBalance());
                    break;
                case "4":
                    ArrayList<Message> messages = messageManager.getMessagesForUser(currentUser);
                    if (messages.isEmpty()) {
                        out.println("No messages.");
                    } else {
                        for (Message m : messages) {
                            out.println("From: " + m.getSender() + " - " + m.getContent());
                        }
                    }
                    break;
                case "5":
                    out.println("Enter recipient username:");
                    String to = in.readLine();
                    out.println("Enter message content:");
                    String content = in.readLine();
                    messageManager.addMessage(new Message(currentUser, to, content));
                    out.println("Message sent.");
                    break;
                case "6":
                    out.println("Enter seller username:");
                    String sellerToRate = in.readLine();
                    out.println("Enter rating (1-5):");
                    int rating = Integer.parseInt(in.readLine());
                    ratingManager.addRating(sellerToRate, rating);
                    out.println("Rating submitted.");
                    break;
                case "7":
                    out.println("Enter seller username:");
                    String sellerToCheck = in.readLine();
                    double avg = ratingManager.getAverageRating(sellerToCheck);
                    out.println(avg == 0.0 ? "No ratings." : "Average rating: " + String.format("%.2f", avg));
                    break;
                case "8":
                    ArrayList<Item> soldItems = soldItemManager.getSoldItemsBySeller(currentUser);
                    if (soldItems.isEmpty()) {
                        out.println("No sold items.");
                    } else {
                        for (Item si : soldItems) {
                            out.println(si.getName() + " - $" + si.getPrice());
                        }
                    }
                    break;
                case "9": // Search by Category
                    out.println("Enter category to search:");
                    String categorySearch = in.readLine();
                    ArrayList<Item> categoryItems = itemManager.searchItemsByName("");
                    for (Item items : categoryItems) {
                        if (items.getCategory() != null && items.getCategory().equalsIgnoreCase(categorySearch)) {
                            out.println(items.getName() + " - $" + items.getPrice() + " - Seller: " + items.getSeller());
                        }
                    }
                    break;
                case "10": // Search by Seller
                    out.println("Enter seller username:");
                    String sellerUsername = in.readLine();
                    ArrayList<Item> sellerItems = itemManager.searchItemsByName("");
                    for (Item items : sellerItems) {
                        if (items.getSeller().equals(sellerUsername)) {
                            out.println(items.getName() + " - $" + items.getPrice() + " - Category: " + items.getCategory());
                        }
                    }
                    break;
                case "11": // Handle Delete Listing
                    out.println("Enter the name of the item you want to delete:");
                    String deleteItemName = in.readLine();
                    // Check if item exists and belongs to the current user before deleting
                    ArrayList<Item> userItems = itemManager.searchItemsByName(deleteItemName);
                    boolean found = false;
                    for (Item userItem : userItems) {
                        if (userItem.getSeller().equals(currentUser) && userItem.getName().equalsIgnoreCase(deleteItemName)) {
                            itemManager.deleteItemByNameAndSeller(deleteItemName, currentUser);
                            out.println("Item deleted successfully!");
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        out.println("Item not found or you do not own this item.");
                    }
                    break;
                case "12": // Updated case for Logout
                    currentUser = null;
                    out.println("Logged out.");
                    exit = true;
                    return;
                case "13":
                    User user = userManager.getUser(currentUser);
                    if (user != null) {
                        double balance = user.getBalance();
                        out.printf("Your current balance is: $%.2f%n", balance);
                    } else {
                        out.println("Error retrieving user information.");
                    }
                    break;
                default:
                    out.println("Invalid option.");
            }
        }
    }
}