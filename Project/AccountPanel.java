import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.BoxLayout;
import javax.swing.ListSelectionModel;
import java.awt.Color;

/**
 * Account Panel
 *
 * Panel within the Marketplace Dashboard to display account information,
 * balance, average seller rating, and previously sold items.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 28, 2025
 */
public class AccountPanel extends JPanel {

    private MarketPlaceGUI guiManager;
    
    private JLabel balanceValueLabel;
    private JButton checkBalanceButton;

    // Rating Components
    private JLabel averageRatingLabel; // To display own rating

    // Sold Items Components
    private JButton viewSoldItemsButton;
    private JTable soldItemsTable;
    private DefaultTableModel soldItemsTableModel;
    private JLabel soldItemStatusLabel;

    public AccountPanel(MarketPlaceGUI guiManager) {
        this.guiManager = guiManager;

        // Use BorderLayout for overall structure
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Info Panel (Balance and Rating)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); // Stack vertically
        infoPanel.setBorder(BorderFactory.createTitledBorder("Account Info"));

        // Balance Row
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balancePanel.add(new JLabel("Current Balance:"));
        balanceValueLabel = new JLabel("Click button ->");
        checkBalanceButton = new JButton("Refresh Balance");
        balancePanel.add(balanceValueLabel);
        balancePanel.add(checkBalanceButton);
        infoPanel.add(balancePanel);

        // Rating Row
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.add(new JLabel("Your Average Seller Rating:"));
        averageRatingLabel = new JLabel("N/A"); // Placeholder
        ratingPanel.add(averageRatingLabel);
        infoPanel.add(ratingPanel);
        
        add(infoPanel, BorderLayout.NORTH);

        // Center Sold Items Panel
        JPanel soldItemsContainer = new JPanel(new BorderLayout(5, 5));
        soldItemsContainer.setBorder(BorderFactory.createTitledBorder("Your Sold Items History"));
        
        // Sold Items Table
        String[] soldColumnNames = {"Item Name", "Sold Price", "Category"};
        soldItemsTableModel = new DefaultTableModel(soldColumnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        soldItemsTable = new JTable(soldItemsTableModel);
        soldItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane soldTableScrollPane = new JScrollPane(soldItemsTable);
        soldItemsContainer.add(soldTableScrollPane, BorderLayout.CENTER);

        // Status Label for Sold Items
        soldItemStatusLabel = new JLabel(" ");
        soldItemStatusLabel.setFont(soldItemStatusLabel.getFont().deriveFont(Font.ITALIC));
        soldItemStatusLabel.setForeground(Color.GRAY);
        soldItemsContainer.add(soldItemStatusLabel, BorderLayout.NORTH);

        // Sold Items Button
        viewSoldItemsButton = new JButton("Refresh Sold Items List");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center button
        buttonPanel.add(viewSoldItemsButton);
        soldItemsContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(soldItemsContainer, BorderLayout.CENTER);

        // Action Listeners
        checkBalanceButton.addActionListener(e -> guiManager.requestCheckBalance());
        viewSoldItemsButton.addActionListener(e -> {
             soldItemStatusLabel.setText("Fetching...");
             guiManager.requestViewSoldItems();
         });

    }

    public void setBalance(String balanceText) {
        SwingUtilities.invokeLater(() -> balanceValueLabel.setText(balanceText));
    }

    public void setAverageRating(String ratingText) {
         SwingUtilities.invokeLater(() -> averageRatingLabel.setText(ratingText));
    }

    public void updateSoldItemsView(ArrayList<String[]> soldItemsData) {
        SwingUtilities.invokeLater(() -> {
            soldItemsTableModel.setRowCount(0);
            if (soldItemsData != null && !soldItemsData.isEmpty()) {
                for (String[] rowData : soldItemsData) {
                     if (rowData.length == 3) {
                         soldItemsTableModel.addRow(rowData);
                     } else {
                         System.err.println("AccountPanel: Received sold item data with incorrect column count: " + rowData.length);
                     }
                }
                soldItemStatusLabel.setText(" ");
            } else {
                soldItemStatusLabel.setText("You have not sold any items yet.");
            }
            soldItemsTable.clearSelection();
        });
    }
} 