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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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

        tabbedPane = new JTabbedPane();

        searchBuyPanel = new SearchBuyPanel(guiManager);
        listItemPanel = new ListItemPanel(guiManager);
        manageListingsPanel = new ManageListingsPanel(guiManager);
        messagingPanel = new MessagingPanel(guiManager);
        accountPanel = new AccountPanel(guiManager);

        // Add tabs
        tabbedPane.addTab("Search/Buy", searchBuyPanel);
        tabbedPane.addTab("List Item", listItemPanel);
        tabbedPane.addTab("My Listings", manageListingsPanel);
        tabbedPane.addTab("Messages", messagingPanel);
        tabbedPane.addTab("Account", accountPanel);

        // Add listener to refresh data when a tab is selected
        tabbedPane.addChangeListener(e -> {
             Component selectedComponent = tabbedPane.getSelectedComponent();
             if (selectedComponent == messagingPanel) {
                 guiManager.requestViewMessages(); // Request messages when tab becomes visible
             } else if (selectedComponent == accountPanel) {
                 // Refresh Account Panel data
                 guiManager.requestCheckBalance();
                 guiManager.requestViewSoldItems();
                 if (username != null) {
                     guiManager.requestViewSellerRating(username); 
                 }
             } else if (selectedComponent == manageListingsPanel) {
                 guiManager.requestMyListings();
             } else if (selectedComponent == searchBuyPanel) {
                // Do nothing for now
             } else if (selectedComponent == listItemPanel) {
                // Do nothing for now
             }
        });

        // Main Layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Action Listeners
        logoutButton.addActionListener(e -> guiManager.requestLogout());
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);
        return panel;
    }

    public void updateBalanceDisplay(String balanceText) {
        if (accountPanel != null) {
            accountPanel.setBalance(balanceText);
        }
    }

    public void clearListItemFields() {
        if (listItemPanel != null) {
            listItemPanel.clearFields();
        }
    }

    public void updateSearchResults(ArrayList<String[]> searchData) {
        if (searchBuyPanel != null) {
            searchBuyPanel.updateSearchResults(searchData);
        }
    }

    public void updateMyListings(ArrayList<String[]> listingsData) {
        if (manageListingsPanel != null) {
            manageListingsPanel.updateListingsTable(listingsData);
        }
    }

    public void displayMessages(List<String> messages) {
        if (messagingPanel != null) {
            messagingPanel.displayMessages(messages);
        }
    }

    public void clearMessageSendFields() {
        if (messagingPanel != null) {
            messagingPanel.clearSendFields();
        }
    }


    public void updateSellerSearchResults(ArrayList<String[]> sellerData) {
        // Assuming seller search display is part of SearchBuyPanel for now
        if (searchBuyPanel != null) {
            searchBuyPanel.updateSellerSearchResults(sellerData);
        } else {
            System.err.println("Dashboard: SearchBuyPanel is null, cannot update seller results.");
        }
    }

    public void updateSoldItemsView(ArrayList<String[]> soldItemsData) {
        // Assuming sold items display is part of AccountPanel
        if (accountPanel != null) {
            accountPanel.updateSoldItemsView(soldItemsData);
        } else {
            System.err.println("Dashboard: AccountPanel is null, cannot update sold items.");
        }
    }

    public void promptForRating(String sellerName) {
        SwingUtilities.invokeLater(() -> {
            // Simple integer input for rating
             String[] options = {"1", "2", "3", "4", "5"};
            String ratingStr = (String) JOptionPane.showInputDialog(
                    this, // Parent component
                    "Rate the seller '" + sellerName + "' (1-5 stars):", // Message
                    "Rate Seller", // Title
                    JOptionPane.QUESTION_MESSAGE,
                    null, // Icon
                    options, // Selection values
                    options[4]); // Default selection (5 stars)

            if (ratingStr != null && !ratingStr.isEmpty()) {
                try {
                    int rating = Integer.parseInt(ratingStr);
                    if (rating >= 1 && rating <= 5) {
                        // Send the rating back to the server via guiManager
                        guiManager.requestRateSeller(sellerName, rating);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid rating selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                     JOptionPane.showMessageDialog(this, "Invalid rating format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // User cancelled or closed the dialog
                System.out.println("User chose not to rate seller: " + sellerName);
            }
        });
    }

    public void updateAverageRatingDisplay(String ratingText) {
        if (accountPanel != null) {
            accountPanel.setAverageRating(ratingText);
        } else {
            System.err.println("Dashboard: AccountPanel is null, cannot update average rating.");
        }
    }

    public void clearSearchResults() {
        if (searchBuyPanel != null) {
            searchBuyPanel.clearSearchResultsTable();
        }
    }

} 