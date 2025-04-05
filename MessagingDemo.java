import java.util.List;
import java.util.Scanner;

public class MessagingDemo {
    public static void main(String[] args) {
        MessageManager messageManager = new MessageManager();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter your username (Buyer):");
        String sender = scanner.nextLine();
        
        System.out.println("Enter the seller's username:");
        String receiver = scanner.nextLine();
        
        System.out.println("Enter the item ID you're interested in:");
        int itemID = scanner.nextInt();
        scanner.nextLine(); 
        
        System.out.println("Enter your message:");
        String content = scanner.nextLine();
        
        Message msg = new Message(sender, receiver, itemID, content);
        messageManager.sendMessage(msg);
        
        System.out.println("\n" + receiver + ", do you want to reply? (yes/no)");
        String replyChoice = scanner.nextLine();
        if (replyChoice.equalsIgnoreCase("yes")) {
            System.out.println("Enter your reply message:");
            String replyContent = scanner.nextLine();
            Message replyMsg = new Message(receiver, sender, itemID, replyContent);
            messageManager.sendMessage(replyMsg);
        }
        
        System.out.println("\nMessages for Item ID " + itemID + ":");
        List<Message> itemMessages = messageManager.getMessagesForItem(itemID);
        for (Message m : itemMessages) {
            System.out.println(m);
        }
    }
}
