import java.io.*;
import java.util.ArrayList;
/**
 * Message Interface
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 6th, 2025
 */
public class MessageManager implements MessageInterface {
    private ArrayList<Message> messages = new ArrayList<>();
    private final String messagesFile = "messages.txt";

    public MessageManager() {
        loadMessages();
    }

    //sends message (adds to messages.txt)
    @Override
    public void sendMessage(String sender, String receiver, String messageContent) {
        Message newMessage = new Message(sender, receiver, messageContent);
        messages.add(newMessage);
        saveMessages();
    }

    //reads messages from messages.txt
    @Override
    public String[] getMessages(String username) {
        ArrayList<String> userMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getReceiver().equals(username)) {
                userMessages.add("[" + message.getTimestamp() + "] " 
                    + message.getSender() + ": " + message.getContent());
            }
        }
        return userMessages.toArray(new String[0]); // Convert list to array
    }

    //gets mesages for specific receiver
    public ArrayList<Message> getMessagesForUser(String username) {
        ArrayList<Message> userMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getReceiver().equals(username)) {
                userMessages.add(message);
            }
        }
        return userMessages;
    }

    //add message to messages arraylist
    public synchronized void addMessage(Message message) {
        messages.add(message);
        saveMessages();
    }

    //deletes certain message
    public synchronized void deleteMessage(Message deletedMessage) {
        messages.remove(deletedMessage);
        saveMessages();
    }

    //loads messages from message.txt
    private void loadMessages() {
        messages.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(messagesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    messages.add(new Message(parts[0], parts[1], parts[2]));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Messages file not found. Starting with an empty message list.");
        } catch (IOException e) {
            System.out.println("Error reading messages file.");
            e.printStackTrace();
        }
    }

    //writes to messages.txt
    private void saveMessages() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(messagesFile))) {
            for (Message message : messages) {
                writer.write(message.getSender() + "," + message.getReceiver() + "," + message.getContent());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to messages file.");
            e.printStackTrace();
        }
    }
}