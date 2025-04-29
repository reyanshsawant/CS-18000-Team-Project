import java.awt.BorderLayout;
import java.awt.Component; // Import Component
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Marketplace Dashboard Frame
 *
 * The main window displayed after successful user login, providing access
 * to all marketplace functions via tabs.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public class MarketplaceDashboard extends JFrame {

    private MarketPlaceGUI guiManager;
    private String username;

    private JTabbedPane tabbedPane;
    private JButton logoutButton;

    // Panels for each tab
    private SearchBuyPanel searchBuyPanel;
    private ListItemPanel listItemPanel;
    private ManageListingsPanel manageListingsPanel;
    private MessagingPanel messagingPanel;
    private AccountPanel accountPanel;

    public MarketplaceDashboard(MarketPlaceGUI guiManager, String username) {
        this.guiManager = guiManager;
        this.username = username;

        setTitle("Marketplace Dashboard - Welcome " + username);
        setSize(800, 600); // A reasonable default size
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle close via listener
        setLocationRelativeTo(null); // Center on screen

        // Add a window listener to handle the close button (should probably log out)
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Ask guiManager to handle logout/exit
                guiManager.requestLogout();
            }
        });

        // Top Panel for Welcome message and Buttons ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("  Welcome, " + username + "!", JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButton = new JButton("Logout");
        buttonPanel.add(logoutButton);

        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding

        // Tabbed Pane for Core Functions ---
        tabbedPane = new JTabbedPane();

        // Initialize panels (use specific panels where available)
        searchBuyPanel = new SearchBuyPanel(guiManager); // Instantiate the actual SearchBuyPanel
        listItemPanel = new ListItemPanel(guiManager); // Instantiate the actual ListItemPanel
        manageListingsPanel = new ManageListingsPanel(guiManager); // Instantiate the actual ManageListingsPanel
        messagingPanel = new MessagingPanel(guiManager); // Instantiate the actual MessagingPanel
        accountPanel = new AccountPanel(guiManager); // Pass guiManager here

        // Add tabs
        tabbedPane.addTab("Search/Buy", searchBuyPanel); // Add the SearchBuyPanel instance
        tabbedPane.addTab("List Item", listItemPanel); // Add the ListItemPanel instance
        tabbedPane.addTab("My Listings", manageListingsPanel); // Add the ManageListingsPanel instance
        tabbedPane.addTab("Messages", messagingPanel); // Add the MessagingPanel instance
        tabbedPane.addTab("Account", accountPanel); // Add the AccountPanel instance

        // Add listener to refresh data when a tab is selected
        tabbedPane.addChangeListener(e -> {
             Component selectedComponent = tabbedPane.getSelectedComponent();
             if (selectedComponent == messagingPanel) {
                 guiManager.requestViewMessages(); // Request messages when tab becomes visible
             } else if (selectedComponent == accountPanel) {
                 guiManager.requestCheckBalance(); // Request balance when Account tab becomes visible
             } else if (selectedComponent == manageListingsPanel) {
                 guiManager.requestMyListings();
             }
        });

        // Main Layout ---
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Action Listeners ---
        logoutButton.addActionListener(e -> guiManager.requestLogout());
    }

    // Helper method remains for other placeholders
    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);
        return panel;
    }

    // Method for MarketPlaceGUI to call to update the balance display
    public void updateBalanceDisplay(String balanceText) {
        if (accountPanel != null) {
            accountPanel.setBalance(balanceText);
        }
    }

    // Method for MarketPlaceGUI to call after item is listed successfully
    public void clearListItemFields() {
        if (listItemPanel != null) {
            listItemPanel.clearFields();
        }
    }

    // Method for MarketPlaceGUI to call to update the search results table
    public void updateSearchResults(ArrayList<String[]> searchData) {
        if (searchBuyPanel != null) {
            searchBuyPanel.updateSearchResults(searchData);
        }
    }

    // Method for MarketPlaceGUI to call to update the user's listings table
    public void updateMyListings(ArrayList<String[]> listingsData) {
        if (manageListingsPanel != null) {
            manageListingsPanel.updateListingsTable(listingsData);
        }
    }

    // Method for MarketPlaceGUI to call to display received messages
    public void displayMessages(List<String> messages) {
        if (messagingPanel != null) {
            messagingPanel.displayMessages(messages);
        }
    }

    // Method for MarketPlaceGUI to call after sending a message
    public void clearMessageSendFields() {
        if (messagingPanel != null) {
            messagingPanel.clearSendFields();
        }
    }

    // TODO: Add methods to update the content of other specific panels later
    // e.g., public void displayMessages(ArrayList<Message> messages)

} 