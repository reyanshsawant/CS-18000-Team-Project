import java.util.List;

public interface MessageManagerInterface {
    void sendMessage(Message message);
    List<Message> getMessagesForUser(String username);
    List<Message> getMessagesForItem(int itemID);
}
