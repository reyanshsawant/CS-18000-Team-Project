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
            out.println("14. View My Listings");
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
                            User sellerForItem = userManager.getUser(res.getSellerName());
                            double avgRating = (sellerForItem != null) ? sellerForItem.getAverageRating() : 0.0;
                            String ratingStr = (avgRating == 0.0) ? "No ratings" : String.format("%.1f/5 stars", avgRating);
                            out.println(res.getName() + " - $" + res.getPrice() + 
                                        " - Seller: " + res.getSellerName() + " (" + ratingStr + ")" +
                                        " - Category: " + (res.getCategory() != null ? res.getCategory() : "N/A"));
                        }
                    }
                    break;
                case "3":
                    out.println("Enter item name to buy:");
                    String itemName = in.readLine();
                    ArrayList<Item> buyItems = itemManager.searchItemsByName(itemName);
                    if (buyItems.isEmpty()) {
                        out.println("Item not found.");
                        break;
                    }
                    Item selected = buyItems.get(0);
                    User buyer = userManager.getUser(currentUser);
                    User seller = userManager.getUser(selected.getSellerName());

                    if (seller == null) {
                        out.println("Error: Seller not found.");
                        break;
                    }
                    if (currentUser.equals(selected.getSellerName())) {
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
                    soldItemManager.recordSale(selected, buyer.getUsername());
                    itemManager.removeItem(selected.getItemId());
                    
                    out.println("Item purchased successfully. Your new balance: $" + String.format("%.2f", buyer.getBalance()));

                    out.println("Would you like to rate the seller '" + seller.getUsername() + "'? (yes/no)");
                    out.println("Enter choice:");
                    out.flush();
                   
                    String rateChoice = in.readLine();
                    if (rateChoice != null && rateChoice.trim().equalsIgnoreCase("yes")) {
                        int ratingValue = -1;
                        while (ratingValue < 1 || ratingValue > 5) {
                            out.println("Enter rating (1-5 stars):");
                            out.flush();
                            try {
                                String ratingInput = in.readLine();
                                if (ratingInput == null) throw new IOException("Client disconnected during rating.");
                                ratingValue = Integer.parseInt(ratingInput);
                                if (ratingValue < 1 || ratingValue > 5) {
                                    out.println("Invalid rating. Please enter a number between 1 and 5.");
                                }
                            } catch (NumberFormatException e) {
                                out.println("Invalid input. Please enter a number.");
                                ratingValue = -1;
                            } catch (IOException ioe) {
                                 System.out.println("Client disconnected while entering rating.");
                                 throw ioe;
                            }
                        }
                        if (!soldItemManager.hasBuyerPurchasedFromSeller(currentUser, seller.getUsername())) {
                             out.println("Rating failed: You must purchase from this seller to rate them.");
                        } else {
                            seller.addRating(ratingValue);
                            userManager.saveUsers();
                            out.println("Thank you for your rating!");
                        }
                    } else {
                         out.println("Skipping rating.");
                    }
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
                    out.println("Enter seller username to rate:");
                    String sellerToRate = in.readLine();
                    User userToRate = userManager.getUser(sellerToRate);

                    if (userToRate == null) {
                        out.println("Seller not found.");
                        break;
                    }
                    if (currentUser.equals(sellerToRate)) {
                        out.println("You cannot rate yourself.");
                        break;
                    }
                    
                    if (!soldItemManager.hasBuyerPurchasedFromSeller(currentUser, sellerToRate)) {
                        out.println("Rating failed: You must purchase from a seller before rating them.");
                        break;
                    }
                    
                    int rating = -1;
                    while (rating < 1 || rating > 5) {
                         out.println("Enter rating (1-5 stars):");
                         try {
                            rating = Integer.parseInt(in.readLine());
                             if (rating < 1 || rating > 5) {
                                 out.println("Invalid rating. Please enter a number between 1 and 5.");
                             }
                         } catch (NumberFormatException e) {
                             out.println("Invalid input. Please enter a number.");
                             rating = -1;
                         }
                    }

                    userToRate.addRating(rating);
                    userManager.saveUsers();
                    out.println("Rating submitted successfully for " + sellerToRate + ".");
                    break;
                case "7":
                    out.println("Enter seller username to view rating:");
                    String sellerToCheck = in.readLine();
                    User userToCheck = userManager.getUser(sellerToCheck);

                    if (userToCheck == null) {
                        out.println("Seller not found.");
                    } else {
                        double avg = userToCheck.getAverageRating();
                        int numRatings = userToCheck.getNumberOfRatings();
                        out.println("Seller: " + sellerToCheck);
                        if (numRatings == 0) {
                            out.println("Average Rating: No ratings yet.");
                        } else {
                            out.println(String.format("Average Rating: %.1f/5 stars (from %d ratings)", avg, numRatings));
                        }
                    }
                    break;
                case "8":
                    ArrayList<Item> soldItems = soldItemManager.getSoldItemsBySeller(currentUser);
                    if (soldItems.isEmpty()) {
                        out.println("You have not sold any items yet.");
                    } else {
                        out.println("--- Your Sold Items ---");
                        for (Item si : soldItems) {
                             out.println(si.getName() + " - Price: $" + String.format("%.2f", si.getPrice()) +
                                         " - Category: " + (si.getCategory() != null ? si.getCategory() : "N/A"));
                        }
                        out.println("-----------------------");
                    }
                    break;
                case "9":
                    out.println("Enter category to search:");
                    String categorySearch = in.readLine();
                    ArrayList<Item> categoryItems = itemManager.searchItemsByCategory(categorySearch);
                    if (categoryItems.isEmpty()) {
                        out.println("No items found in category: " + categorySearch);
                    } else {
                        out.println("--- Items in Category: " + categorySearch + " ---");
                        for (Item catItem : categoryItems) {
                            User sellerForItem = userManager.getUser(catItem.getSellerName());
                            double avgRating = (sellerForItem != null) ? sellerForItem.getAverageRating() : 0.0;
                            String ratingStr = (avgRating == 0.0) ? "No ratings" : String.format("%.1f/5 stars", avgRating);
                            out.println(catItem.getName() + " - $" + catItem.getPrice() + 
                                        " - Seller: " + catItem.getSellerName() + " (" + ratingStr + ")");
                        }
                         out.println("---------------------------------");
                    }
                    break;
                case "10":
                    out.println("Enter seller username to search for:");
                    String sellerQuery = in.readLine();
                    
                    ArrayList<User> matchedSellers = userManager.searchUsers(sellerQuery);
                    if (matchedSellers.isEmpty()) {
                         out.println("No sellers found matching '" + sellerQuery + "'.");
                    } else {
                        out.println("--- Matching Sellers ---");
                        for (User matchedSeller : matchedSellers) {
                            double avgRating = matchedSeller.getAverageRating();
                            int numRatings = matchedSeller.getNumberOfRatings();
                            String ratingStr = (numRatings == 0) ? "No ratings yet" : String.format("%.1f/5 stars (%d ratings)", avgRating, numRatings);
                            out.println("Username: " + matchedSeller.getUsername() + " - Rating: " + ratingStr);
                        }
                         out.println("------------------------");
                    }
                    break;
                case "11":
                    out.println("Enter the name of the item you want to delete:");
                    String deleteItemName = in.readLine();
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
                case "12":
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
                case "14":
                    if (currentUser == null) {
                        out.println("Error: Not logged in.");
                        break;
                    }
                    ArrayList<Item> myListings = itemManager.getItemsBySeller(currentUser);
                    if (myListings.isEmpty()) {
                        out.println("You have no active listings.");
                    } else {
                        out.println("--- Your Active Listings ---");
                        for (Item myItem : myListings) {
                            out.println(myItem.getName() + " - Price: $" + String.format("%.2f", myItem.getPrice()) +
                                        " - Category: " + (myItem.getCategory() != null ? myItem.getCategory() : "N/A"));
                        }
                        out.println("---------------------------");
                    }
                    break;
                default:
                    out.println("Invalid option.");
            }
        }
    }
}