public interface MessageInterface {
    String getSenderUsername();
    String getReceiverUsername();
    int getItemID();
    String getContent();
    String getTimestamp();
}
