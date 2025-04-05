import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements MessageInterface {
    private String senderUsername;
    private String receiverUsername;
    private int itemID;
    private String content;
    private String timestamp;
    
    public Message(String senderUsername, String receiverUsername, int itemID, String content) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.itemID = itemID;
        this.content = content;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    
    @Override
    public String getSenderUsername() {
        return senderUsername;
    }
    
    @Override
    public String getReceiverUsername() {
        return receiverUsername;
    }
    
    @Override
    public int getItemID() {
        return itemID;
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public String getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "[" + timestamp + "] " + senderUsername + " -> " + receiverUsername 
               + " (Item ID: " + itemID + "): " + content;
    }
}
