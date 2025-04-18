/**
 * Message
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 6th, 2025
 */
public class Message {
    private String sender;
    private String receiver;
    private String content;
    private long timestamp;

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;

    }
    public void setContent(String content) {
        this.content = content;
    }

}