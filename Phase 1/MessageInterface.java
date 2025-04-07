public interface MessageInterface {
    void sendMessage(String sender, String receiver, String message);
    String[] getMessages(String username);
}