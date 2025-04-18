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