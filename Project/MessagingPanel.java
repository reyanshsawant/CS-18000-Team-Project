import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Messaging Panel
 *
 * Panel within the Marketplace Dashboard for viewing received messages and sending new ones.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public class MessagingPanel extends JPanel {

    private MarketplaceGUI guiManager;

    private JTextArea receivedMessagesArea;
    private JTextField recipientField;
    private JTextField messageContentField;
    private JButton sendButton;
    private JButton refreshButton;

    public MessagingPanel(MarketplaceGUI guiManager) {
        this.guiManager = guiManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Received Messages Area (Top/Center) 
        JPanel receivedPanel = new JPanel(new BorderLayout(5, 5));
        receivedPanel.setBorder(BorderFactory.createTitledBorder("Received Messages"));

        receivedMessagesArea = new JTextArea(15, 50); // Rows, Columns
        receivedMessagesArea.setEditable(false);
        receivedMessagesArea.setLineWrap(true);
        receivedMessagesArea.setWrapStyleWord(true);
        JScrollPane receivedScrollPane = new JScrollPane(receivedMessagesArea);

        refreshButton = new JButton("Refresh Messages");
        JPanel refreshButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButtonPanel.add(refreshButton);

        receivedPanel.add(refreshButtonPanel, BorderLayout.NORTH);
        receivedPanel.add(receivedScrollPane, BorderLayout.CENTER);

        // Send Message Area (Bottom) 
        JPanel sendPanel = new JPanel(new GridBagLayout());
        sendPanel.setBorder(BorderFactory.createTitledBorder("Send New Message"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Recipient
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        sendPanel.add(new JLabel("To (Username):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        recipientField = new JTextField(25);
        sendPanel.add(recipientField, gbc);

        // Message Content
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        sendPanel.add(new JLabel("Message:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        messageContentField = new JTextField(40);
        sendPanel.add(messageContentField, gbc);

        // Send Button
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        sendButton = new JButton("Send Message");
        sendPanel.add(sendButton, gbc);

        // Add Panels to Main Layout 
        add(receivedPanel, BorderLayout.CENTER);
        add(sendPanel, BorderLayout.SOUTH);

        // Action Listeners 
        refreshButton.addActionListener(e -> guiManager.requestViewMessages());

        sendButton.addActionListener(e -> {
            String recipient = recipientField.getText();
            String content = messageContentField.getText();

            if (recipient.trim().isEmpty() || content.trim().isEmpty()) {
                 JOptionPane.showMessageDialog(MessagingPanel.this,
                        "Recipient and message content cannot be empty.",
                        "Send Error", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            guiManager.requestSendMessage(recipient, content);
        });
    }

    public void displayMessages(List<String> messages) {
        SwingUtilities.invokeLater(() -> {
            receivedMessagesArea.setText(""); // Clear previous messages
            if (messages == null || messages.isEmpty()) {
                receivedMessagesArea.setText("No messages.");
            } else {
                for (String msg : messages) {
                    receivedMessagesArea.append(msg + "\n");
                }
                 receivedMessagesArea.setCaretPosition(0); // Scroll to top
            }
        });
    }

    public void clearSendFields() {
         SwingUtilities.invokeLater(() -> {
            recipientField.setText("");
            messageContentField.setText("");
         });
    }
} 