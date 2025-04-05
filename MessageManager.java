import java.util.ArrayList;
import java.util.List;

public class MessageManager implements MessageManagerInterface {
    private List<Message> messages;
    
    public MessageManager() {
        messages = new ArrayList<>();
    }
    
    @Override
    public void sendMessage(Message message) {
        messages.add(message);
        System.out.println("Message sent: " + message);
    }
    
    @Override
    public List<Message> getMessagesForUser(String username) {
        List<Message> userMessages = new ArrayList<>();
        for (Message msg : messages) {
            if (msg.getReceiverUsername().equals(username) || msg.getSenderUsername().equals(username)) {
                userMessages.add(msg);
            }
        }
        return userMessages;
    }
    
    @Override
    public List<Message> getMessagesForItem(int itemID) {
        List<Message> itemMessages = new ArrayList<>();
        for (Message msg : messages) {
            if (msg.getItemID() == itemID) {
                itemMessages.add(msg);
            }
        }
        return itemMessages;
    }
}
