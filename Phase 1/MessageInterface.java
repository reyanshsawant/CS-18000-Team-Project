/**
 * MessageInterface
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 6th, 2025
 */
public interface MessageInterface {
    void sendMessage(String sender, String receiver, String message);
    String[] getMessages(String username);
}