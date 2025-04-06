import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        ItemManager itemManager = new ItemManager();
        MessageManager messageManager = new MessageManager();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Marketplace!");

        // User authentication loop
        User currentUser = null;
        while (currentUser == null) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Log in");
            System.out.println("2. Create an account");
            System.out.println("3. Delete an account");
            System.out.println("4. Exit");
            System.out.print("Enter your option: ");
            int opt = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (opt) {
                case 1:
                    // Log in
                    System.out.print("Enter username: ");
                    String loginUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String loginPassword = scanner.nextLine();

                    User user = userManager.getUser(loginUsername);
                    if (user != null && user.login(loginUsername, loginPassword)) {
                        currentUser = user;
                        System.out.println("Login successful! Welcome, " + currentUser.getUsername() + "!");

                        // Check for messages addressed to the seller
                        ArrayList<Message> messages = messageManager.getMessagesForUser(currentUser.getUsername());
                        if (!messages.isEmpty()) {
                            System.out.println("\nYou have new messages:");
                            for (Message message : messages) {
                                System.out.println("From: " + message.getSender());
                                System.out.println("Message: " + message.getContent());
                                System.out.println();

                                // Prompt the seller to respond
                                System.out.print("Would you like to respond to this message? (yes/no): ");
                                String response = scanner.nextLine();
                                if (response.equalsIgnoreCase("yes")) {
                                    System.out.print("Enter your response: ");
                                    String replyContent = scanner.nextLine();
                                    Message reply = new Message(currentUser.getUsername(), message.getSender(), replyContent);
                                    messageManager.addMessage(reply);
                                    System.out.println("Response sent!");

                                    // Delete the original message after responding
                                    messageManager.deleteMessage(message);
                                    System.out.println("The original message has been cleared.");
                                }
                            }
                        } else {
                            System.out.println("No new messages.");
                        }
                    } else {
                        System.out.println("Invalid username or password. Please try again.");
                    }
                    break;

                case 2:
                    // Create an account
                    System.out.print("Enter username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String newPassword = scanner.nextLine();
                    System.out.print("Enter initial balance: ");
                    double initialBalance = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline character

                    if (userManager.getUser(newUsername) == null) {
                        User newUser = new User(newUsername, newPassword, initialBalance);
                        userManager.addUser(newUser);
                        System.out.println("Account created successfully! You can now log in.");
                    } else {
                        System.out.println("Username already exists. Please choose a different username.");
                    }
                    break;

                case 3:
                    // Delete an account
                    System.out.print("Enter username: ");
                    String deleteUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String deletePassword = scanner.nextLine();

                    User deleteUser = userManager.getUser(deleteUsername);
                    if (deleteUser != null && deleteUser.login(deleteUsername, deletePassword)) {
                        userManager.deleteUser(deleteUsername);
                        itemManager.deleteItemsBySeller(deleteUsername); // Delete all items listed by the user
                        System.out.println("Account and all associated items deleted successfully.");
                    } else {
                        System.out.println("Invalid username or password. Account deletion failed.");
                    }
                    break;

                case 4:
                    // Exit
                    System.out.println("Exiting the Marketplace. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        // Marketplace functionality loop
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. List an item");
            System.out.println("2. Delete an item");
            System.out.println("3. Search for items");
            System.out.println("4. Buy an item");
            System.out.println("5. Edit balance");
            System.out.println("6. Message a seller");
            System.out.println("7. View messages");
            System.out.println("8. Log out");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // List an item
                    System.out.print("Enter item name: ");
                    String itemName = scanner.nextLine();
                    System.out.print("Enter item description: ");
                    String itemDescription = scanner.nextLine();
                    System.out.print("Enter item price: ");
                    double itemPrice = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter picture file path (or leave blank): ");
                    String picturePath = scanner.nextLine();

                    Item newItem = new Item(itemName, itemDescription, itemPrice);
                    newItem.setSeller(currentUser.getUsername());
                    if (!picturePath.isEmpty()) {
                        newItem.setPicturePath(picturePath);
                    }
                    itemManager.addItem(newItem);
                    System.out.println("Item listed successfully!");
                    break;

                case 2:
                    // Delete an item
                    System.out.print("Enter the name of the item you want to delete: ");
                    String deleteItemName = scanner.nextLine();
                    itemManager.deleteItemByNameAndSeller(deleteItemName, currentUser.getUsername());
                    System.out.println("Item deleted successfully!");
                    break;

                case 3:
                    // Search for items
                    System.out.print("Enter the name of the item to search for: ");
                    String searchName = scanner.nextLine();
                    ArrayList<Item> items = itemManager.searchItemsByName(searchName);

                    if (items.isEmpty()) {
                        System.out.println("No items found with that name.");
                    } else {
                        System.out.println("Available items:");
                        for (Item item : items) {
                            System.out.println("- " + item.getName() + " ($" + item.getPrice() + ") - Seller: " + item.getSeller());
                            if (item.getPicturePath() != null) {
                                System.out.println("  Picture: " + item.getPicturePath());
                            }
                        }
                    }
                    break;

                case 4:
                    // Buy an item
                    System.out.print("Enter the name of the item you want to buy: ");
                    String buyItemName = scanner.nextLine();
                    ArrayList<Item> buyItems = itemManager.searchItemsByName(buyItemName);

                    if (buyItems.isEmpty()) {
                        System.out.println("No items found with that name.");
                        break;
                    }

                    System.out.println("Available items:");
                    for (int i = 0; i < buyItems.size(); i++) {
                        Item item = buyItems.get(i);
                        System.out.println((i + 1) + ". " + item.getName() + " - $" + item.getPrice() + " (Seller: " + item.getSeller() + ")");
                    }

                    System.out.print("Enter the number of the item you want to buy: ");
                    int itemChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (itemChoice < 1 || itemChoice > buyItems.size()) {
                        System.out.println("Invalid choice.");
                        break;
                    }

                    Item selectedItem = buyItems.get(itemChoice - 1);
                    if (currentUser.getBalance() < selectedItem.getPrice()) {
                        System.out.println("Insufficient balance to buy this item.");
                        break;
                    }

                    User seller = userManager.getUser(selectedItem.getSeller());
                    if (seller == null) {
                        System.out.println("Seller not found. Transaction failed.");
                        break;
                    }

                    // Perform the transaction
                    userManager.updateUserBalance(currentUser.getUsername(), currentUser.getBalance() - selectedItem.getPrice());
                    userManager.updateUserBalance(seller.getUsername(), seller.getBalance() + selectedItem.getPrice());
                    itemManager.removeItem(selectedItem.getItemId());
                    System.out.println("Purchase successful! You bought " + selectedItem.getName() + " for $" + selectedItem.getPrice());
                    break;

                case 5:
                    // Edit balance
                    while (true) {
                        System.out.println("\nYour current balance is: $" + currentUser.getBalance());
                        System.out.println("1. Add to balance");
                        System.out.println("2. Remove from balance");
                        System.out.println("3. Go back");
                        System.out.print("Enter your choice: ");
                        int balanceChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        if (balanceChoice == 1) {
                            System.out.print("Enter amount to add: ");
                            double amountToAdd = scanner.nextDouble();
                            scanner.nextLine(); // Consume newline
                            userManager.updateUserBalance(currentUser.getUsername(), currentUser.getBalance() + amountToAdd);
                            System.out.println("Balance updated successfully!");
                        } else if (balanceChoice == 2) {
                            System.out.print("Enter amount to remove: ");
                            double amountToRemove = scanner.nextDouble();
                            scanner.nextLine(); // Consume newline
                            if (currentUser.getBalance() >= amountToRemove) {
                                userManager.updateUserBalance(currentUser.getUsername(), currentUser.getBalance() - amountToRemove);
                                System.out.println("Balance updated successfully!");
                            } else {
                                System.out.println("Insufficient balance.");
                            }
                        } else if (balanceChoice == 3) {
                            break;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                    }
                    break;

                case 6:
                    // Message a seller
                    System.out.print("Enter the seller's username: ");
                    String sellerUsername = scanner.nextLine();
                    System.out.print("Enter your message: ");
                    String messageContent = scanner.nextLine();

                    User sellerUser = userManager.getUser(sellerUsername);
                    if (sellerUser != null) {
                        Message message = new Message(currentUser.getUsername(), sellerUsername, messageContent);
                        messageManager.addMessage(message);
                        System.out.println("Message sent to " + sellerUsername + "!");
                    } else {
                        System.out.println("Seller not found.");
                    }
                    break;

                case 7:
                    // View messages
                    ArrayList<Message> messages = messageManager.getMessagesForUser(currentUser.getUsername());
                    if (messages.isEmpty()) {
                        System.out.println("No messages.");
                    } else {
                        System.out.println("Your messages:");
                        for (Message message : messages) {
                            System.out.println("From: " + message.getSender() + " - " + message.getContent());
                        }
                    }
                    break;

                case 8:
                    // Log out
                    System.out.println("Logging out. Goodbye, " + currentUser.getUsername() + "!");
                    currentUser = null;
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}