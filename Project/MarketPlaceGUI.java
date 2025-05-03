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
public class MarketPlaceGUI implements GuiCallback {

    private MarketPlaceClient client;
    private LoginFrame loginFrame;
    private RegistrationFrame registrationFrame;
    private MarketplaceDashboard dashboardFrame;

    private final BlockingQueue<String> userInputQueue = new LinkedBlockingQueue<>();
    private String currentPrompt = "";
    private String loggedInUsername = null;
    private String sellerToRate = null; // Store seller name between prompts

    // State flags for expected results
    private boolean expectingSearchResults = false; // Items by keyword/category
    private boolean expectingMyListings = false; 
    private boolean expectingMessages = false; 
    private boolean expectingSellerSearchResults = false;
    private boolean expectingSoldItems = false;
    private boolean expectingNormalDisconnect = false; 

    // Temporary storage for accumulating results
    private ArrayList<String[]> currentSearchResults = new ArrayList<>();
    private ArrayList<String[]> currentMyListings = new ArrayList<>();
    private List<String> currentMessages = new ArrayList<>();
    private ArrayList<String[]> currentSellerSearchResults = new ArrayList<>();
    private ArrayList<String[]> currentSoldItems = new ArrayList<>();

    // Patterns for parsing server output
    // Example: "ItemName - $Price - Category: CategoryName"
    private static final Pattern MY_LISTING_PATTERN = Pattern.compile(
            "^(.*) - Price: \\$(.*) - Category: (.*)$"); // Corrected double escape \\$
    // Example: "From: SenderName - Message Content"
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^From: (.*) - (.*)$");
    // Example: "Username: SellerName - Rating: X.X/5 stars (Y ratings)" or "Username: SellerName - Rating: No ratings yet"
    private static final Pattern SELLER_SEARCH_RESULT_PATTERN = Pattern.compile(
            "^Username: (.*) - Rating: (.*)$");

    private static final String END_SEARCH_RESULTS = "--- Items in Category:";
    private static final String END_MY_LISTINGS = "--- Your Sold Items";
    private static final String END_MESSAGES = "--- End Messages";
    private static final String END_SELLER_SEARCH = "------------------------";
    private static final String END_SOLD_ITEMS = "-----------------------";

    private static final String FINAL_MENU_PROMPT = "Enter option:";
    private static final String NO_ITEMS_FOUND_MSG = "No items found";
    private static final String NO_SELLERS_FOUND_MSG = "No sellers found";
    private static final String NO_SOLD_ITEMS_MSG = "You have not sold any items yet.";
    private static final String NO_MESSAGES_MSG = "No messages.";

    public MarketPlaceGUI() {
        loginFrame = new LoginFrame(this);
        registrationFrame = new RegistrationFrame(this);
    }

    public void startClientOrReconnect() {
        // Create a NEW client instance each time we connect/reconnect
        client = new MarketPlaceClient("localhost", 15000); 
        
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
    public void displayServerMessage(String line) {
            String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) return; // Ignore empty lines

        System.out.println("GUI_PROCESS_LINE: Processing: [" + trimmedLine + "]");
        currentPrompt = trimmedLine;
        boolean lineHandled = false; // Track if the line was handled (as data or finalizer)

        // Step 1: Check if this line is DATA we are expecting 
            if (expectingSearchResults) {
            System.out.println("DEBUG_PARSE: Checking line for item search: [" + trimmedLine + "]");
            try {
                // Attempt string parsing 
                if (trimmedLine.contains(" - $") && trimmedLine.contains(" - Seller: ") && trimmedLine.contains(" (")) {
                    String[] parts = trimmedLine.split(" - ", 4); 
                    if (parts.length >= 3) {
                        String sellerInfo = parts[2];
                        int sellerPrefixEnd = sellerInfo.indexOf(":");
                        int parenStart = sellerInfo.lastIndexOf('(');
                        int parenEnd = sellerInfo.lastIndexOf(')');
                        if (sellerPrefixEnd != -1 && parenStart > sellerPrefixEnd && parenEnd > parenStart) {
                            // Looks like a valid item line, parse it
                            String name = parts[0].trim();
                            String priceStr = parts[1].startsWith("$") ? parts[1].substring(1).trim() : parts[1].trim();
                            String seller = sellerInfo.substring(sellerPrefixEnd + 1, parenStart).trim();
                            String rating = sellerInfo.substring(parenStart + 1, parenEnd).trim();
                            String category = "N/A";
                            if (parts.length == 4 && parts[3].startsWith("Category: ")) {
                                category = parts[3].substring("Category: ".length()).trim();
                            }
                            System.out.println("DEBUG_PARSE: Parsed Item OK. Adding to current list.");
                            currentSearchResults.add(new String[]{name, priceStr, seller, rating, category});
                            lineHandled = true; // Mark as handled
                        }
                    }
                }
                if (!lineHandled) {
                     System.out.println("DEBUG_PARSE: Line did not parse as item result.");
                }
            } catch (Exception e) {
                 System.err.println("ERROR_PARSE: Exception parsing item line: [" + trimmedLine + "] - " + e.getMessage());
            }
        } else if (expectingSellerSearchResults) {
            System.out.println("DEBUG_REGEX: Checking line for seller search: [" + trimmedLine + "]");
            Matcher matcher = SELLER_SEARCH_RESULT_PATTERN.matcher(trimmedLine);
            if (matcher.matches()) {
                System.out.println("DEBUG_REGEX: MATCHED seller! Adding to current list.");
                currentSellerSearchResults.add(new String[]{matcher.group(1).trim(), matcher.group(2).trim()});
                lineHandled = true;
            } else {
                 System.out.println("DEBUG_REGEX: Line did NOT match seller search pattern.");
            }
        } else if (expectingSoldItems || expectingMyListings) {
            System.out.println("DEBUG_REGEX: Checking line for sold/my listing: [" + trimmedLine + "]");
                 Matcher matcher = MY_LISTING_PATTERN.matcher(trimmedLine);
                 if (matcher.matches()) {
                  System.out.println("DEBUG_REGEX: MATCHED sold/my listing! Adding to current list.");
                  if (expectingSoldItems) currentSoldItems.add(new String[]{matcher.group(1).trim(), matcher.group(2).trim(), matcher.group(3).trim()});
                  if (expectingMyListings) currentMyListings.add(new String[]{matcher.group(1).trim(), matcher.group(2).trim(), matcher.group(3).trim()});
                  lineHandled = true;
             } else {
                  System.out.println("DEBUG_REGEX: Line did NOT match sold/my listing pattern.");
                 }
            } else if (expectingMessages) {
             System.out.println("DEBUG_REGEX: Checking line for message: [" + trimmedLine + "]");
                 Matcher matcher = MESSAGE_PATTERN.matcher(trimmedLine);
                 if (matcher.matches()) {
                  System.out.println("DEBUG_REGEX: MATCHED message! Adding to current list.");
                  currentMessages.add(trimmedLine);
                  lineHandled = true;
             } else {
                 System.out.println("DEBUG_REGEX: Line did NOT match message pattern.");
             }
        }

        // Step 2: Check if this line is a FINALIZER for the current expectation 
        boolean isFinalPrompt = trimmedLine.equals(FINAL_MENU_PROMPT);
        boolean isNoItems = trimmedLine.startsWith(NO_ITEMS_FOUND_MSG);
        boolean isNoSellers = trimmedLine.startsWith(NO_SELLERS_FOUND_MSG);
        boolean isNoSold = trimmedLine.startsWith(NO_SOLD_ITEMS_MSG);
        boolean isNoMsgs = trimmedLine.startsWith(NO_MESSAGES_MSG);
        // Add other specific end markers if needed

        if (expectingSearchResults && (isFinalPrompt || isNoItems)) {
            System.out.println("DEBUG_FINALIZE: Item Search finalized by line: [" + trimmedLine + "]");
            finalizeSearchResults(new ArrayList<>(currentSearchResults), isNoItems);
            resetStateAfterProcessing(true, false, false, false, false);
            lineHandled = true;
        } else if (expectingSellerSearchResults && (isFinalPrompt || isNoSellers)) {
            System.out.println("DEBUG_FINALIZE: Seller Search finalized by line: [" + trimmedLine + "]");
            finalizeSellerSearch(new ArrayList<>(currentSellerSearchResults), isNoSellers);
            resetStateAfterProcessing(false, false, false, true, false);
            lineHandled = true;
        } else if (expectingSoldItems && (isFinalPrompt || isNoSold)) {
             System.out.println("DEBUG_FINALIZE: Sold Items finalized by line: [" + trimmedLine + "]");
            finalizeSoldItemsView(new ArrayList<>(currentSoldItems), isNoSold);
            resetStateAfterProcessing(false, false, false, false, true);
            lineHandled = true;
        } else if (expectingMyListings && isFinalPrompt) { // Assuming only prompt finalizes this
             System.out.println("DEBUG_FINALIZE: My Listings finalized by line: [" + trimmedLine + "]");
            finalizeMyListings(new ArrayList<>(currentMyListings), currentMyListings.isEmpty());
            resetStateAfterProcessing(false, true, false, false, false);
            lineHandled = true;
        } else if (expectingMessages && (isFinalPrompt || isNoMsgs)) {
             System.out.println("DEBUG_FINALIZE: Messages finalized by line: [" + trimmedLine + "]");
            finalizeMessageView(new ArrayList<>(currentMessages), isNoMsgs);
            resetStateAfterProcessing(false, false, true, false, false);
            lineHandled = true;
        }

        // Step 3: If not handled as data or finalizer, treat as specific message 
        if (!lineHandled) {
             System.out.println("DEBUG_MSG: Handling line as specific message: [" + trimmedLine + "]");
            handleSpecificMessages(trimmedLine);
        }
    }
    
    // Helper to reset state flags and clear temporary lists after processing results
    private void resetStateAfterProcessing(boolean search, boolean listings, boolean msgs, boolean sellerSearch, boolean soldItems) {
        expectingSearchResults = !search && expectingSearchResults;
        expectingMyListings = !listings && expectingMyListings;
        expectingMessages = !msgs && expectingMessages;
        expectingSellerSearchResults = !sellerSearch && expectingSellerSearchResults;
        expectingSoldItems = !soldItems && expectingSoldItems;

        if (search) currentSearchResults.clear();
        if (listings) currentMyListings.clear();
        if (msgs) currentMessages.clear();
        if (sellerSearch) currentSellerSearchResults.clear();
        if (soldItems) currentSoldItems.clear();
    }

    // Finalize Methods (Call Dashboard Updates)
    private void finalizeSearchResults(ArrayList<String[]> results, boolean noResultsMsgReceived) {
        System.out.println("DEBUG: Finalizing item search results. Count: " + results.size() + ", NoResults: " + noResultsMsgReceived);
        System.out.println("DEBUG_FINALIZE: finalizeSearchResults called with list size: " + (results != null ? results.size() : "null"));
        SwingUtilities.invokeLater(() -> {
            if (dashboardFrame != null) {
                    dashboardFrame.updateSearchResults(results);
            }
        });
    }

    private void finalizeMyListings(ArrayList<String[]> results, boolean noResultsMsgReceived) {
         System.out.println("DEBUG: Finalizing my listings. Count: " + results.size() + ", NoResults: " + noResultsMsgReceived);
         SwingUtilities.invokeLater(() -> {
             if (dashboardFrame != null) {
                 dashboardFrame.updateMyListings(results);
             }
         });
    }

    private void finalizeMessageView(List<String> messages, boolean noMessagesReceived) {
         System.out.println("DEBUG: Finalizing message view. Count: " + messages.size() + ", NoResults: " + noMessagesReceived);
         SwingUtilities.invokeLater(() -> {
             if (dashboardFrame != null) {
                 dashboardFrame.displayMessages(messages); // Assumes dashboard has this method
             }
         });
    }
    
    // Finalize Seller Search Results
    private void finalizeSellerSearch(ArrayList<String[]> results, boolean noResultsMsgReceived) {
         System.out.println("DEBUG: Finalizing seller search results. Count: " + results.size() + ", NoResults: " + noResultsMsgReceived);
         System.out.println("DEBUG_FINALIZE: finalizeSellerSearch called with list size: " + (results != null ? results.size() : "null"));
         SwingUtilities.invokeLater(() -> {
            if (dashboardFrame != null) {
                dashboardFrame.updateSellerSearchResults(results); 
            }
        });
    }

    // Finalize Sold Items View
    private void finalizeSoldItemsView(ArrayList<String[]> results, boolean noResultsMsgReceived) {
         System.out.println("DEBUG: Finalizing sold items view. Count: " + results.size() + ", NoResults: " + noResultsMsgReceived);
         SwingUtilities.invokeLater(() -> {
            if (dashboardFrame != null) {
                dashboardFrame.updateSoldItemsView(results); 
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
             } else if (trimmedMsg.startsWith("Invalid credentials.")) {
                 if (loginFrame != null && loginFrame.isVisible()) { 
                      JOptionPane.showMessageDialog(loginFrame, "Invalid credentials. Account deletion failed.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                 } 
             } else if (trimmedMsg.startsWith("Account deleted successfully.")) {
                 JOptionPane.showMessageDialog(null, "Account deleted successfully.", "Account Deleted", JOptionPane.INFORMATION_MESSAGE);
             } else if (trimmedMsg.contains("Item listed.")) {
                 if (dashboardFrame != null && dashboardFrame.isVisible()) {
                     JOptionPane.showMessageDialog(dashboardFrame, "Item listed successfully!", "Listing Success", JOptionPane.INFORMATION_MESSAGE);
                     dashboardFrame.clearListItemFields();
                 }
             } else if (trimmedMsg.startsWith("Item purchased successfully. Your new balance: ")) { // PURCHASE SUCCESS
                  if (dashboardFrame != null && dashboardFrame.isVisible()) {
                     try {
                         String balanceStr = trimmedMsg.substring(trimmedMsg.indexOf('$'));
                         dashboardFrame.updateBalanceDisplay(balanceStr);
                         dashboardFrame.clearSearchResults();
                         System.out.println("GUI_MSG: Purchase successful message processed (Dialog skipped).");
                         // Rating prompt comes next
                     } catch (Exception e) { /* ... */ }
                 }
             } else if (trimmedMsg.startsWith("Would you like to rate the seller")) { // RATING QUESTION
                 // Just parse and store the seller name for the next prompt
                  try {
                      Pattern sellerPattern = Pattern.compile(".*'(.*?)'.*");
                      Matcher matcher = sellerPattern.matcher(trimmedMsg);
                      if (matcher.find()) {
                          sellerToRate = matcher.group(1);
                          System.out.println("GUI_RATE: Stored seller to rate: " + sellerToRate);
                      } else {
                          sellerToRate = null; // Clear if parsing fails
                          System.err.println("Could not parse seller name from rating prompt: " + trimmedMsg);
                      }
                  } catch (Exception e) {
                      sellerToRate = null;
                      System.err.println("Error handling rating question: " + e.getMessage());
                  }
                 // DO NOT show dialog here, wait for "Enter choice:" prompt
             } else if (trimmedMsg.equalsIgnoreCase("Enter choice:")) { // RATING YES/NO PROMPT
                  if (dashboardFrame != null && dashboardFrame.isVisible() && sellerToRate != null) {
                       int choice = JOptionPane.showConfirmDialog(dashboardFrame, 
                                        "Rate the seller '" + sellerToRate + "'?",
                                        "Confirm Rating",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);
                       if (choice == JOptionPane.YES_OPTION) {
                           System.out.println("GUI_RATE: User chose YES to rate " + sellerToRate);
                           queueUserInput("yes");
                       } else {
                            System.out.println("GUI_RATE: User chose NO to rate " + sellerToRate);
                           queueUserInput("no");
                           sellerToRate = null; // Clear stored seller name if skipping
                       }
                  } else {
                      // Invalid state, shouldn't get here without seller name
                      System.err.println("ERROR: Received 'Enter choice:' prompt but no seller name stored or dashboard hidden.");
                      queueUserInput("no"); // Send 'no' to prevent server stall
                      sellerToRate = null;
                  }
             } else if (trimmedMsg.equalsIgnoreCase("Enter rating (1-5 stars):")) { // RATING 1-5 PROMPT
                  if (dashboardFrame != null && dashboardFrame.isVisible() && sellerToRate != null) {
                       // Use the rating dialog logic previously in MarketplaceDashboard
                       String[] options = {"1", "2", "3", "4", "5"};
                       String ratingStr = (String) JOptionPane.showInputDialog(
                               dashboardFrame, 
                               "Enter rating (1-5 stars) for '" + sellerToRate + "':", 
                               "Rate Seller", 
                               JOptionPane.QUESTION_MESSAGE,
                               null, options, options[4]);
                       if (ratingStr != null && !ratingStr.isEmpty()) {
                            try {
                                int rating = Integer.parseInt(ratingStr); // Already validated by options
                                System.out.println("GUI_RATE: Queuing rating " + rating + " for " + sellerToRate);
                                queueUserInput(String.valueOf(rating));
                            } catch (NumberFormatException ex) { /* Should not happen */ }
                       } else {
                           // User cancelled - send a default or invalid rating? Server loop handles invalid.
                           System.out.println("GUI_RATE: User cancelled rating input. Sending '0'.");
                           queueUserInput("0"); // Send invalid rating to break server loop cleanly
                       }
                  } else {
                      System.err.println("ERROR: Received 'Enter rating:' prompt but no seller stored or dashboard hidden.");
                      queueUserInput("0"); // Send invalid rating
                  }
                  sellerToRate = null; // Clear stored seller name after processing rating input

             } else if (trimmedMsg.startsWith("Thank you for your rating!") || trimmedMsg.startsWith("Rating submitted successfully")) { // RATING SUCCESS
                  if (dashboardFrame != null && dashboardFrame.isVisible()) {
                      JOptionPane.showMessageDialog(dashboardFrame, "Rating submitted successfully!", "Rating Submitted", JOptionPane.INFORMATION_MESSAGE);
                  }
                  sellerToRate = null; // Clear name on success too
             } else if (trimmedMsg.startsWith("Rating failed: You must purchase")) { // RATING FAILED (History)
                  if (dashboardFrame != null && dashboardFrame.isVisible()) {
                      JOptionPane.showMessageDialog(dashboardFrame, 
                          trimmedMsg, // Show server message directly 
                          "Rating Error", JOptionPane.WARNING_MESSAGE);
                  }
                  sellerToRate = null; // Clear name on failure
             } else if (trimmedMsg.startsWith("Skipping rating.")) { // RATING SKIPPED
                 System.out.println("GUI_RATE: Server confirmed rating skipped.");
                 sellerToRate = null; // Clear stored name
                 // No pop-up needed, just proceed
             } else if (trimmedMsg.startsWith("Seller not found.") && currentPrompt.toLowerCase().contains("rate")) { // RATING FAILED (Not found)
                  if (dashboardFrame != null && dashboardFrame.isVisible()) {
                      JOptionPane.showMessageDialog(dashboardFrame, "Seller not found. Could not submit rating.", "Rating Error", JOptionPane.ERROR_MESSAGE);
                  }
                  sellerToRate = null; // Clear name on failure
             } else if (trimmedMsg.startsWith("Average Rating: ")) { // Second line of command 7 output
                 if (dashboardFrame != null && dashboardFrame.isVisible() && loggedInUsername != null) {
                    
                      String ratingText = trimmedMsg.substring("Average Rating: ".length());
                      dashboardFrame.updateAverageRatingDisplay(ratingText); // Call a new method on Dashboard
                 } // Ignore if dashboard not visible or not logged in
             } else if (trimmedMsg.startsWith("Item not found.")) {
                  JOptionPane.showMessageDialog(dashboardFrame, "The selected item was not found (perhaps sold already?).", "Purchase Error", JOptionPane.WARNING_MESSAGE);
             } else if (trimmedMsg.startsWith("Insufficient balance.")) {
                  JOptionPane.showMessageDialog(dashboardFrame, "Insufficient balance to purchase this item.", "Purchase Error", JOptionPane.ERROR_MESSAGE);
             } else if (trimmedMsg.startsWith("You cannot buy your own item.")) {
                  JOptionPane.showMessageDialog(dashboardFrame, "You cannot purchase an item you listed yourself.", "Purchase Error", JOptionPane.WARNING_MESSAGE);
             } else if (trimmedMsg.contains("Item deleted successfully!")) { 
                 if (dashboardFrame != null && dashboardFrame.isVisible()) {
                     JOptionPane.showMessageDialog(dashboardFrame, "Listing deleted successfully.", "Deletion Success", JOptionPane.INFORMATION_MESSAGE);
                     requestMyListings(); 
                 }
             } else if (trimmedMsg.contains("Item not found or you do not own this item.")) { 
                 if (dashboardFrame != null && dashboardFrame.isVisible()) {
                      JOptionPane.showMessageDialog(dashboardFrame, "Could not delete listing. Item not found or not owned by you.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                 }
             } else if (trimmedMsg.startsWith("Message sent.")) {
                  if (dashboardFrame != null && dashboardFrame.isVisible()) {
                     JOptionPane.showMessageDialog(dashboardFrame, "Message sent successfully!", "Message Sent", JOptionPane.INFORMATION_MESSAGE);
                     dashboardFrame.clearMessageSendFields(); 
                  }
             } else if (trimmedMsg.startsWith("Your current balance is: $")) {
                   if (dashboardFrame != null && dashboardFrame.isVisible()) {
                        try {
                            String balanceStr = trimmedMsg.substring(trimmedMsg.indexOf('$'));
                            dashboardFrame.updateBalanceDisplay(balanceStr); 
                        } catch (Exception e) { dashboardFrame.updateBalanceDisplay("Error"); }
                   }
             }
         });
    }

    @Override
    public String getUserInput() {
        System.out.println("CLIENT: Waiting for user input for prompt: [" + currentPrompt + "]");
        try {
            System.out.println("GUI_INPUT: Attempting to take from queue...");
            String input = userInputQueue.take(); // Blocks here until input is queued
            System.out.println("GUI_INPUT: Successfully took input from queue: [" + input + "]");
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
        // No state to clear before login attempt
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
             JOptionPane.showMessageDialog(loginFrame, "Username and password cannot be empty.", "Login Error", JOptionPane.WARNING_MESSAGE);
             return;
        }
        queueUserInput("1", username, password); // Command 1 = Login
    }

    public void attemptRegistration(String username, String password, String balance) {
         // No state to clear before registration attempt
         queueUserInput("2", username, password, balance); // Command 2 = Create Account
    }

     public void requestListItem(String name, String description, String price, String category) {
         clearPreviousRequestState(); // Ensure state is clear
         System.out.println("GUI: Requesting to list item: " + name + " Category: " + category); 
         queueUserInput("1", name, description, price, category); // Command 1 = List Item
     }

     public void requestSearchItems(String keyword) {
         clearPreviousRequestState(); // Ensure state is clear
         System.out.println("GUI: Requesting item search (keyword): " + keyword);
         System.out.println("GUI_STATE: Before requestSearchItems - expectingSearchResults = " + expectingSearchResults);
         expectingSearchResults = true; // Set flag
         System.out.println("GUI_STATE: After requestSearchItems - expectingSearchResults = " + expectingSearchResults);
         // currentSearchResults.clear(); // Clearing is handled by clearPreviousRequestState
         queueUserInput("2", keyword); // Command 2 = Search Item (by keyword)
     }
     
    // Request Search by Category
    public void requestSearchByCategory(String category) {
         clearPreviousRequestState();
         System.out.println("GUI: Requesting item search (category): " + category);
         expectingSearchResults = true;
         queueUserInput("9", category); 
     }

     public void requestBuyItem(String itemName) {
         clearPreviousRequestState(); // Ensure state is clear
         System.out.println("GUI: Requesting to buy item: " + itemName);
         queueUserInput("3", itemName); // Command 3 = Buy Item
     }

    public void requestRateSeller(String sellerUsername, int rating) {
        clearPreviousRequestState(); // Ensure state is clear
        System.out.println("GUI: Requesting to rate seller: " + sellerUsername + " with " + rating + " stars");
        queueUserInput("6", sellerUsername, String.valueOf(rating)); // Command 6 = Rate Seller
    }

    // Request View Seller Rating 
    public void requestViewSellerRating(String sellerUsername) {
         clearPreviousRequestState(); // Ensure state is clear
         System.out.println("GUI: Requesting to view rating for seller: " + sellerUsername);
         queueUserInput("7", sellerUsername); // Command 7 = View Seller Rating
    }

     // Request My Listings 
     public void requestMyListings() {
         clearPreviousRequestState(); // Ensure state is clear
         if (loggedInUsername == null) return; 
         System.out.println("GUI: Requesting own listings for user: " + loggedInUsername);
         expectingMyListings = true; // Set flag
         queueUserInput("14"); // Command 14 = View My Listings
     }

    // Request Search Sellers (by query)
    public void requestSearchSeller(String query) {
        clearPreviousRequestState(); // Ensure state is clear
        System.out.println("GUI: Requesting seller search: " + query);
        expectingSellerSearchResults = true; // Set flag
        queueUserInput("10", query); // Command 10 = Search by Seller 
    }

    // Request View Sold Items (for account view)
    public void requestViewSoldItems() {
        clearPreviousRequestState(); // Ensure state is clear
        if (loggedInUsername == null) return; 
        System.out.println("GUI: Requesting sold items for user: " + loggedInUsername);
        expectingSoldItems = true; // Set flag
        queueUserInput("8", loggedInUsername); // Command 8 = View Sold Items 
    }

     public void requestDeleteListing(String itemName) {
         clearPreviousRequestState(); // Ensure state is clear
         System.out.println("GUI: Requesting to delete listing: " + itemName);
         queueUserInput("11", itemName); // Command 11 = Delete Listing
     }

    public void requestLogout() {
         clearPreviousRequestState(); // Ensure state is clear
         expectingNormalDisconnect = true; 
        System.out.println("GUI: Requesting logout");
        queueUserInput("12"); // Command 12 = Logout
    }

    public void requestAccountDeletion() {
         clearPreviousRequestState(); // Ensure state is clear
        System.out.println("GUI: Requesting account deletion");
         if (dashboardFrame != null && dashboardFrame.isVisible()) {
              SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(dashboardFrame, "Feature not available while logged in.\nPlease log out first, then use the 'Delete Account' option on the login screen.",
                     "Delete Account Error", JOptionPane.WARNING_MESSAGE);
              });
         }
    }

     public void attemptDeleteAccount(String username, String password) {
         clearPreviousRequestState(); // Ensure state is clear
         System.out.println("GUI: Attempting account deletion for: " + username);
         // Basic validation already done in LoginFrame, just send command
         queueUserInput("4", username, password); // Command 4 = Delete Account
     }
     
    public void requestViewMessages() {
        clearPreviousRequestState(); // Ensure state is clear
        System.out.println("GUI: Requesting message view");
        expectingMessages = true; // Set flag
        queueUserInput("4"); // Command 4 = View Messages
    }

    public void requestSendMessage(String recipient, String content) {
        clearPreviousRequestState(); // Ensure state is clear
        System.out.println("GUI: Requesting to send message to: " + recipient);
        queueUserInput("5", recipient, content); // Command 5 = Send Message
    }

    public void requestCheckBalance() {
         clearPreviousRequestState(); // Ensure state is clear
         System.out.println("GUI: Requesting balance check");
         queueUserInput("13"); // Command 13 = View Balance
    }

    // Helper to prevent conflicting state flags AND clear lists
    private void clearPreviousRequestState() {
        System.out.println("GUI_STATE: Clearing previous request state."); // Add log
        expectingSearchResults = false;
        expectingMyListings = false;
        expectingMessages = false; 
        expectingSellerSearchResults = false; 
        expectingSoldItems = false;         

        // Clear the main accumulating lists when starting a new request
        currentSearchResults.clear();
        currentMyListings.clear();
        currentMessages.clear(); 
        currentSellerSearchResults.clear(); 
        currentSoldItems.clear();         
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
        System.out.println("GUI_QUEUE: Attempting to queue: [" + String.join(",", inputs) + "]");
        try {
            for (String input : inputs) {
                if (input == null) continue;
                userInputQueue.put(input);
            }
            System.out.println("GUI_QUEUE: Successfully queued: [" + String.join(",", inputs) + "]");
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
            MarketPlaceGUI gui = new MarketPlaceGUI();
            // Call the renamed method for initial start
            gui.startClientOrReconnect(); 
        });
    }
} 