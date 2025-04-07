/**
 * MainDatabase.java
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Arjun Anilkumar 
 * @version April 6th, 2025
 */
//main manages the item, messages, and user managers
public class MainDatabase {
    private UserManager userManager;
    private ItemManager itemManager;
    private MessageManager messageManager;

    public MainDatabase() {
        userManager = new UserManager();
        itemManager = new ItemManager();
        messageManager = new MessageManager();
    }

    //getters for managers
    public UserManager getUserManager() {
        return userManager;
    }
    public ItemManager getItemManager() {
        return itemManager;
    }
    public MessageManager getMessageManager() {
        return messageManager;
    }
}



# MainDatabase Class

## Overview
The `MainDatabase` class serves as the central hub for managing the `UserManager`, `ItemManager`, and `MessageManager` instances in the marketplace application. It provides access to these managers, enabling seamless interaction between different components of the system.

---

## Fields

### 1. `private UserManager userManager`
- **Description**: Manages all user-related operations, such as account creation, deletion, and balance updates.
- **Purpose**: Provides access to user management functionality.

### 2. `private ItemManager itemManager`
- **Description**: Manages all item-related operations, such as adding, deleting, and searching for items.
- **Purpose**: Provides access to item management functionality.

### 3. `private MessageManager messageManager`
- **Description**: Manages all message-related operations, such as sending and retrieving messages.
- **Purpose**: Provides access to message management functionality.

---

## Methods

### 1. `public MainDatabase()`
- **Description**: Constructor that initializes the `UserManager`, `ItemManager`, and `MessageManager` instances.
- **Purpose**: Ensures that all managers are ready for use when the `MainDatabase` is instantiated.

---

### 2. `public UserManager getUserManager()`
- **Description**: Retrieves the `UserManager` instance.
- **Purpose**: Provides access to user-related operations.

---

### 3. `public ItemManager getItemManager()`
- **Description**: Retrieves the `ItemManager` instance.
- **Purpose**: Provides access to item-related operations.

---

### 4. `public MessageManager getMessageManager()`
- **Description**: Retrieves the `MessageManager` instance.
- **Purpose**: Provides access to message-related operations.

---

## Usage Example

```java
public class Main {
    public static void main(String[] args) {
        // Initialize the main database
        MainDatabase database = new MainDatabase();

        // Access the UserManager
        UserManager userManager = database.getUserManager();
        userManager.addUser(new User("user1", "password1", 100.0));
        System.out.println("User added: " + userManager.getUser("user1").getUsername());

        // Access the ItemManager
        ItemManager itemManager = database.getItemManager();
        Item item = new Item("Laptop", "High-performance laptop", 999.99);
        item.setSellerName("user1");
        itemManager.addItem(item);
        System.out.println("Item added: " + itemManager.searchItemsByName("Laptop").get(0).getName());

        // Access the MessageManager
        MessageManager messageManager = database.getMessageManager();
        messageManager.sendMessage("user1", "user2", "Hello, is the item still available?");
        System.out.println("Message sent: " + messageManager.getMessages("user2")[0]);
    }
}