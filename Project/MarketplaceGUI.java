import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Marketplace GUI Manager
 *
 * Main class for the Swing GUI. Manages different frames and interacts
 * with the MarketPlaceClient via the GuiCallback interface.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public class MarketplaceGUI implements GuiCallback {

    private MarketPlaceClient client;
    private LoginFrame loginFrame;
    private RegistrationFrame registrationFrame;
    private MarketplaceDashboard dashboardFrame;

    private final BlockingQueue<String> userInputQueue = new LinkedBlockingQueue<>();
    private String currentPrompt = "";
    private String loggedInUsername = null;

    // State variable to track if we are expecting search results
    private boolean expectingSearchResults = false;
    private ArrayList<String[]> currentSearchResults = new ArrayList<>();
    private boolean expectingMyListings = false; // New flag for managing listings
    private ArrayList<String[]> currentMyListings = new ArrayList<>(); // New list for own listings
    private boolean expectingMessages = false; // New flag for viewing messages
    private List<String> currentMessages = new ArrayList<>(); // New list for messages
    private boolean expectingNormalDisconnect = false; // Flag to suppress disconnect message on logout

    // Pattern to identify search result lines from the server
    // Example: "ItemName - $Price - Seller: SellerName"
    private static final Pattern SEARCH_RESULT_PATTERN = Pattern.compile("^(.*) - \\$(.*) - Seller: (.*)$");
    // Pattern for own listings (Search by Seller results)
    // Example: "ItemName - $Price - Category: CategoryName"
    private static final Pattern MY_LISTING_PATTERN = Pattern.compile("^(.*) - \\$(.*) - Category: (.*)$");
    // Pattern for incoming messages
    // Example: "From: SenderName - Message Content"
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^From: (.*) - (.*)$");

    public MarketplaceGUI() {
        loginFrame = new LoginFrame(this);
        registrationFrame = new RegistrationFrame(this);
        // Client is created on demand in startClientOrReconnect
    }

    // Renamed startClient to handle initial start and reconnects
    public void startClientOrReconnect() {
        // Create a NEW client instance each time we connect/reconnect
        client = new MarketPlaceClient("localhost", 15000); // TODO: Use config for host/port
        
        new Thread(() -> {
            // Check if connection was successful before starting loop
            if (client.isConnected()) { 
                client.start(this);
            } else {
                // Handle initial connection failure
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Failed to connect to the server. Please check if it's running.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1); // Exit if initial connection fails
                });
            }
        }).start();
        
        // Show the login frame (or appropriate initial frame)
        SwingUtilities.invokeLater(() -> {
            if (registrationFrame != null) registrationFrame.setVisible(false);
            if (dashboardFrame != null) dashboardFrame.setVisible(false);
             if (loginFrame == null) { // If first time or after disposeAllFrames
                  loginFrame = new LoginFrame(this);
             }
            loginFrame.setVisible(true);
        });
    }

    // GuiCallback Methods 

    @Override
    public void displayServerMessage(String message) {
        String[] lines = message.split("\n");
        String lastNonEmptyLine = "";
        boolean searchEndedInThisBlock = false; 
        boolean myListingEndedInThisBlock = false; 
        boolean messageViewEndedInThisBlock = false; // Flag for message view end
        boolean noResultsFound = false; // Can apply to search, listings, or messages ("No messages.")
        
        // Temp lists for results found in this specific message block
        ArrayList<String[]> blockSearchResults = new ArrayList<>(); 
        ArrayList<String[]> blockMyListings = new ArrayList<>();
        List<String> blockMessages = new ArrayList<>();

        // Pass 1: Process lines, accumulate results, check for end 
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) continue;

            System.out.println("SERVER_LINE: " + trimmedLine);
            lastNonEmptyLine = trimmedLine;

            // Define end indicators
            boolean isMenuStart = trimmedLine.matches("^\\d+\\.\\s+.*");
            boolean isNoItemsMsg = trimmedLine.startsWith("No items found."); // For search/listings
            boolean isNoMessagesMsg = trimmedLine.startsWith("No messages."); // For messages
            boolean isGenericPrompt = trimmedLine.endsWith(":"); 

            // Determine if this line signals the end of the specific data we are waiting for
            boolean isEndOfSpecificResults = isMenuStart || isNoItemsMsg || isNoMessagesMsg;

            // Parsing Logic 
            if (expectingSearchResults) {
                Matcher matcher = SEARCH_RESULT_PATTERN.matcher(trimmedLine);
                if (matcher.matches()) {
                    System.out.println("DEBUG: Matched search result: " + trimmedLine);
                    blockSearchResults.add(new String[]{matcher.group(1).trim(), matcher.group(2).trim(), matcher.group(3).trim()});
                    continue; // Handled as search result
                } else if (isEndOfSpecificResults) {
                    System.out.println("DEBUG: End of search results detected: " + trimmedLine);
                    noResultsFound = isNoItemsMsg;
                    searchEndedInThisBlock = true; // Mark search as ended
                    // Fall through to handleSpecificMessages for the prompt/menu line itself
                } else if (!isGenericPrompt) {
                    // Ignore unexpected lines during search unless it's the final prompt
                     System.out.println("DEBUG: Ignored line during search: " + trimmedLine);
                     continue; // Consume ignored line
                }
            } else if (expectingMyListings) {
                 Matcher matcher = MY_LISTING_PATTERN.matcher(trimmedLine);
                 if (matcher.matches()) {
                     System.out.println("DEBUG: Matched my listing: " + trimmedLine);
                     blockMyListings.add(new String[]{matcher.group(1).trim(), matcher.group(2).trim(), matcher.group(3).trim()});
                     continue; // Handled as listing result
                 } else if (isEndOfSpecificResults) {
                     System.out.println("DEBUG: End of my listings detected: " + trimmedLine);
                     noResultsFound = isNoItemsMsg;
                     myListingEndedInThisBlock = true; // Mark listing search as ended
                      // Fall through to handleSpecificMessages for the prompt/menu line itself
                 } else if (!isGenericPrompt) {
                     // Ignore unexpected lines during listing unless it's the final prompt
                      System.out.println("DEBUG: Ignored line during my listings: " + trimmedLine);
                       continue; // Consume ignored line
                 }
            } else if (expectingMessages) {
                 // Message Parsing 
                 Matcher matcher = MESSAGE_PATTERN.matcher(trimmedLine);
                 if (matcher.matches()) {
                     System.out.println("DEBUG: Matched message: " + trimmedLine);
                     // Store the full matched line for display
                     blockMessages.add(trimmedLine);
                     continue; // Consume as message
                 } else if (isEndOfSpecificResults) { // Menu start or "No messages." ends the view
                     System.out.println("DEBUG: End of messages detected: " + trimmedLine);
                     noResultsFound = isNoMessagesMsg; // Check if it was the specific no messages line
                     messageViewEndedInThisBlock = true;
                      // Fall through to handleSpecificMessages for the prompt/menu line itself
                 } else if (!isGenericPrompt) {
                     // Ignore other lines while expecting messages
                     System.out.println("DEBUG: Ignored line during messages: " + trimmedLine);
                      continue;
                 }
            }

            // Handle Specific Single Messages (or prompts/menu lines that ended a results block) 
             handleSpecificMessages(trimmedLine);
        }

        // Update overall prompt state
        currentPrompt = lastNonEmptyLine;

        // Pass 2: Finalize results AFTER processing all lines 
        boolean stateReset = false;
        if (searchEndedInThisBlock) {
            System.out.println("DEBUG: Finalizing search results post-block.");
            currentSearchResults.addAll(blockSearchResults);
            finalizeResults(new ArrayList<>(currentSearchResults), noResultsFound, true); // isSearch = true
            expectingSearchResults = false;
            currentSearchResults.clear();
            stateReset = true;
        } else if (myListingEndedInThisBlock) { // Use the specific flag for listings
             System.out.println("DEBUG: Finalizing my listings post-block.");
             currentMyListings.addAll(blockMyListings);
             finalizeResults(new ArrayList<>(currentMyListings), noResultsFound, false); // isSearch = false
             expectingMyListings = false;
             currentMyListings.clear();
             stateReset = true;
        } else if (messageViewEndedInThisBlock) { // Finalize message view
             System.out.println("DEBUG: Finalizing message view post-block.");
             currentMessages.addAll(blockMessages);
             finalizeMessageView(new ArrayList<>(currentMessages), noResultsFound);
             expectingMessages = false;
             currentMessages.clear();
             stateReset = true;
        }
        
        if (!stateReset) { // Block didn't contain the end indicator for the expected results
             if (expectingSearchResults) {
                 currentSearchResults.addAll(blockSearchResults);
                 System.out.println("DEBUG: Search results block ended without end indicator. Cumulative: " + currentSearchResults.size());
             } else if (expectingMyListings) {
                  currentMyListings.addAll(blockMyListings);
                 System.out.println("DEBUG: My listings block ended without end indicator. Cumulative: " + currentMyListings.size());
             } else if (expectingMessages) {
                 currentMessages.addAll(blockMessages);
                 System.out.println("DEBUG: Messages block ended without end indicator. Cumulative: " + currentMessages.size());
             }
        }
    }
    
    // Helper to finalize results processing and update GUI
    // Changed signature to accept boolean isSearchOrMyListing to determine type
    private void finalizeResults(ArrayList<String[]> results, boolean noResultsMsgReceived, boolean isSearchRequest) {
        SwingUtilities.invokeLater(() -> {
            if (dashboardFrame != null) {
                if (isSearchRequest) {
                    System.out.println("DEBUG: Updating search table. Count: " + results.size());
                    dashboardFrame.updateSearchResults(results);
                } else { // Must be MyListings request
                    System.out.println("DEBUG: Updating my listings table. Count: " + results.size());
                    dashboardFrame.updateMyListings(results);
                }
                
                if (results.isEmpty()) {
                    // Use the specific flag passed to the method to determine context
                    String message = noResultsMsgReceived ? "No items found." : (isSearchRequest ? "Search complete. No items found." : "You have no active listings.");
                    String title = isSearchRequest ? "Search Results" : "My Listings";
                    JOptionPane.showMessageDialog(dashboardFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    // Helper to finalize message view update
    private void finalizeMessageView(List<String> messages, boolean noMessagesReceived) {
         SwingUtilities.invokeLater(() -> {
             if (dashboardFrame != null) {
                 System.out.println("DEBUG: Updating message view. Count: " + messages.size());
                 dashboardFrame.displayMessages(messages); // Call the new dashboard method
                 if (messages.isEmpty() && noMessagesReceived) {
                     // JOptionPane.showMessageDialog(dashboardFrame, "You have no messages.", "Messages", JOptionPane.INFORMATION_MESSAGE);
                 }
             }
         });
    }

    // Helper method to handle specific single-line messages
    private void handleSpecificMessages(String trimmedMsg) {
        SwingUtilities.invokeLater(() -> {
             if (trimmedMsg.startsWith("Account created successfully.")) {
                 JOptionPane.showMessageDialog(registrationFrame, "Account created successfully! Please log in.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                 registrationFrame.clearFields();
                 showLogin();
             } else if (trimmedMsg.startsWith("Username already exists.")) {
                 JOptionPane.showMessageDialog(registrationFrame, "Username already exists. Please choose another.", "Registration Error", JOptionPane.WARNING_MESSAGE);
             } else if (trimmedMsg.startsWith("Account deleted successfully.")) {
                 JOptionPane.showMessageDialog(null, "Account deleted successfully.", "Account Deleted", JOptionPane.INFORMATION_MESSAGE);
             } else if (trimmedMsg.contains("Item listed.")) {
                 if (dashboardFrame != null && dashboardFrame.isVisible()) {
                     JOptionPane.showMessageDialog(dashboardFrame, "Item listed successfully!", "Listing Success", JOptionPane.INFORMATION_MESSAGE);
                     dashboardFrame.clearListItemFields();
                 }
             } else if (trimmedMsg.startsWith("Item purchased. Your new balance: ")) {
                  if (dashboardFrame != null && dashboardFrame.isVisible()) {
                     try {
                         String balanceStr = trimmedMsg.substring(trimmedMsg.indexOf('$'));
                         dashboardFrame.updateBalanceDisplay(balanceStr);
                         JOptionPane.showMessageDialog(dashboardFrame, "Item purchased successfully!", "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);
                     } catch (Exception e) {
                         System.err.println("Error parsing balance from message: " + trimmedMsg);
                         dashboardFrame.updateBalanceDisplay("Error");
                     }
                 }
             } else if (trimmedMsg.startsWith("Item not found.")) {
                  JOptionPane.showMessageDialog(dashboardFrame, "The selected item was not found (perhaps sold already?).", "Purchase Error", JOptionPane.WARNING_MESSAGE);
             } else if (trimmedMsg.startsWith("Insufficient balance.")) {
                  JOptionPane.showMessageDialog(dashboardFrame, "Insufficient balance to purchase this item.", "Purchase Error", JOptionPane.ERROR_MESSAGE);
             } else if (trimmedMsg.startsWith("You cannot buy your own item.")) {
                  JOptionPane.showMessageDialog(dashboardFrame, "You cannot purchase an item you listed yourself.", "Purchase Error", JOptionPane.WARNING_MESSAGE);
             } 
             // Add handlers for Listing Deletion 
             else if (trimmedMsg.contains("Item deleted successfully!")) { // Server uses this exact phrase
                 if (dashboardFrame != null && dashboardFrame.isVisible()) {
                     JOptionPane.showMessageDialog(dashboardFrame, "Listing deleted successfully.", "Deletion Success", JOptionPane.INFORMATION_MESSAGE);
                     // Refresh the listings view automatically after deletion
                     requestMyListings(); 
                 }
             } else if (trimmedMsg.contains("Item not found or you do not own this item.")) { // Server uses this phrase
                 if (dashboardFrame != null && dashboardFrame.isVisible()) {
                      JOptionPane.showMessageDialog(dashboardFrame, "Could not delete listing. Item not found or not owned by you.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                 }
             }
             // Add handlers for Messaging 
             else if (trimmedMsg.startsWith("Message sent.")) {
                  if (dashboardFrame != null && dashboardFrame.isVisible()) {
                     JOptionPane.showMessageDialog(dashboardFrame, "Message sent successfully!", "Message Sent", JOptionPane.INFORMATION_MESSAGE);
                     dashboardFrame.clearMessageSendFields(); // Clear input fields
                  }
             }
             // Add handler for Balance Check 
              else if (trimmedMsg.startsWith("Your current balance is: $")) {
                   if (dashboardFrame != null && dashboardFrame.isVisible()) {
                        try {
                            String balanceStr = trimmedMsg.substring(trimmedMsg.indexOf('$'));
                            dashboardFrame.updateBalanceDisplay(balanceStr); 
                        } catch (Exception e) {
                             System.err.println("Error parsing balance from message: " + trimmedMsg);
                             dashboardFrame.updateBalanceDisplay("Error");
                        }
                   }
             }
         });
    }

    @Override
    public String getUserInput() {
        System.out.println("CLIENT: Waiting for user input for prompt: [" + currentPrompt + "]");
        try {
            String input = userInputQueue.take();
            System.out.println("CLIENT: Sending user input: [" + input + "]");
            return input;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("GUI input queue interrupted. Aborting interaction.");
            connectionLost();
            return null;
        }
    }

    @Override
    public void loginSuccess(String username) {
        this.loggedInUsername = username;
        System.out.println("GUI: Login success for " + username);
        expectingNormalDisconnect = false; // Reset flag on successful login
        SwingUtilities.invokeLater(() -> {
            if (loginFrame != null) loginFrame.setVisible(false);
            if (registrationFrame != null) registrationFrame.setVisible(false);
            dashboardFrame = new MarketplaceDashboard(this, username);
            dashboardFrame.setVisible(true);
        });
    }

    @Override
    public void loginFailure() {
        this.loggedInUsername = null;
        System.out.println("GUI: Login failure");
        expectingNormalDisconnect = false; // Reset flag on failure too
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(loginFrame, "Login failed. Please check username/password.", "Login Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void connectionLost() {
         this.loggedInUsername = null;
         expectingNormalDisconnect = false; // Connection loss is not normal
         System.out.println("GUI: Connection lost");
         SwingUtilities.invokeLater(() -> {
            disposeAllFrames();
            JOptionPane.showMessageDialog(null, "Connection to server lost.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
         });
    }

     @Override
    public void clientDisconnected() {
         this.loggedInUsername = null;
         System.out.println("GUI: Client disconnected callback. Normal logout expected: " + expectingNormalDisconnect);
         
         final boolean normalLogout = expectingNormalDisconnect; // Capture flag for use in invokeLater
         expectingNormalDisconnect = false; // Reset flag immediately

         SwingUtilities.invokeLater(() -> {
            disposeAllFrames();
            if (!normalLogout) { // Only show popup if it wasn't a normal logout
                 JOptionPane.showMessageDialog(null, "You have been disconnected.", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
            }
             // Re-initialize frames and prepare for reconnection
             loginFrame = new LoginFrame(this);
             registrationFrame = new RegistrationFrame(this); 
             // Attempt to reconnect by starting the client again
             startClientOrReconnect(); 
         });
    }

    // Methods called by GUI Frames 

    public void attemptLogin(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
             JOptionPane.showMessageDialog(loginFrame, "Username and password cannot be empty.", "Login Error", JOptionPane.WARNING_MESSAGE);
             return;
        }
        queueUserInput("1", username, password);
    }

    public void attemptRegistration(String username, String password, String balance) {
         queueUserInput("2", username, password, balance);
    }

     public void requestListItem(String name, String description, String price, String category) {
         System.out.println("GUI: Requesting to list item: " + name);
         queueUserInput("1", name, description, price, category);
     }

     public void requestSearchItems(String keyword) {
         System.out.println("GUI: Requesting search: " + keyword);
         clearPreviousRequestState(); // Clear other pending request states
         expectingSearchResults = true;
         currentSearchResults.clear(); 
         queueUserInput("2", keyword);
     }

     public void requestBuyItem(String itemName) {
         System.out.println("GUI: Requesting to buy item: " + itemName);
         clearPreviousRequestState(); 
         queueUserInput("3", itemName);
     }

     // Method called by ManageListingsPanel refresh button
     public void requestMyListings() {
         if (loggedInUsername == null) return; // Should not happen if dashboard is visible
         System.out.println("GUI: Requesting own listings for user: " + loggedInUsername);
         clearPreviousRequestState();
         expectingMyListings = true; // Set flag for my listings parsing
         currentMyListings.clear();
         // Server expects: 10 (Search by Seller) -> username
         queueUserInput("10", loggedInUsername);
     }

     // Method called by ManageListingsPanel delete button
     public void requestDeleteListing(String itemName) {
         System.out.println("GUI: Requesting to delete listing: " + itemName);
         clearPreviousRequestState();
         // Server expects: 11 (Delete Listing) -> itemName
         queueUserInput("11", itemName);
         // Response handled in handleSpecificMessages
     }

    public void requestLogout() {
         clearPreviousRequestState();
         expectingNormalDisconnect = true; // Set flag before sending command
        System.out.println("GUI: Requesting logout");
        queueUserInput("12");
    }

    public void requestAccountDeletion() {
         clearPreviousRequestState();
        System.out.println("GUI: Requesting account deletion");
         if (dashboardFrame != null && dashboardFrame.isVisible()) {
              SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(dashboardFrame, "Feature not available while logged in.\nPlease log out first, then use the 'Delete Account' option on the login screen.",
                     "Delete Account Error", JOptionPane.WARNING_MESSAGE);
              });
         }
    }

     public void attemptDeleteAccount(String username, String password) {
          clearPreviousRequestState();
         System.out.println("GUI: Attempting deletion for " + username + " from login screen");
         if (username.trim().isEmpty() || password.trim().isEmpty()) {
             JOptionPane.showMessageDialog(loginFrame, "Username and password required for deletion.", "Deletion Error", JOptionPane.WARNING_MESSAGE);
             return;
         }
         queueUserInput("4", username, password);
     }
     
    // Method called by MessagingPanel refresh button or tab selection
    public void requestViewMessages() {
        System.out.println("GUI: Requesting message view");
        clearPreviousRequestState();
        expectingMessages = true; // Set flag for message parsing
        currentMessages.clear();
        // Server expects: 4 (View Messages)
        queueUserInput("4");
        // Response handled in displayServerMessage
    }

    // Method called by MessagingPanel send button
    public void requestSendMessage(String recipient, String content) {
        System.out.println("GUI: Requesting to send message to: " + recipient);
        clearPreviousRequestState();
        // Server expects: 5 (Send Message) -> recipient -> content
        queueUserInput("5", recipient, content);
        // Response ("Message sent.") handled in handleSpecificMessages
    }

    // Method called by AccountPanel check balance button
    public void requestCheckBalance() {
         System.out.println("GUI: Requesting balance check");
         clearPreviousRequestState();
         // Server expects: 13 (View Balance)
         queueUserInput("13");
         // Response ("Your current balance is: $X.XX") handled in handleSpecificMessages
    }

    // Helper to prevent conflicting state flags
    private void clearPreviousRequestState() {
        expectingSearchResults = false;
        expectingMyListings = false;
        expectingMessages = false; // Clear message flag too
        currentSearchResults.clear();
        currentMyListings.clear();
        currentMessages.clear(); // Clear message list
    }

    public void showRegistration() {
        SwingUtilities.invokeLater(() -> {
            if (loginFrame != null) loginFrame.setVisible(false);
            if (dashboardFrame != null) dashboardFrame.setVisible(false);
            if (registrationFrame != null) {
                 registrationFrame.clearFields();
                 registrationFrame.setVisible(true);
            }
        });
    }

    public void showLogin() {
         SwingUtilities.invokeLater(() -> {
            if (registrationFrame != null) registrationFrame.setVisible(false);
            if (dashboardFrame != null) dashboardFrame.setVisible(false);
            if (loginFrame != null) {
                loginFrame.setVisible(true);
            }
         });
    }

    private void queueUserInput(String... inputs) {
        try {
            for (String input : inputs) {
                if (input == null) continue;
                userInputQueue.put(input);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to queue user input: " + String.join(",", inputs));
            connectionLost();
        }
    }

    private void disposeAllFrames() {
        if (loginFrame != null) loginFrame.dispose();
        if (registrationFrame != null) registrationFrame.dispose();
        if (dashboardFrame != null) dashboardFrame.dispose();
        loginFrame = null;
        registrationFrame = null;
        dashboardFrame = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MarketplaceGUI gui = new MarketplaceGUI();
            // Call the renamed method for initial start
            gui.startClientOrReconnect(); 
        });
    }
} 