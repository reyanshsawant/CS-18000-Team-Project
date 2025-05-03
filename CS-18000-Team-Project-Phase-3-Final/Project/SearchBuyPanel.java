import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Font;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.BoxLayout;
import javax.swing.Box;

/**
 * Search/Buy Panel (Updated for Categories and Seller Search)
 *
 * Panel within the Marketplace Dashboard for searching items (by keyword or category)
 * and sellers, and initiating purchases.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 28, 2025
 */
public class SearchBuyPanel extends JPanel {

    private MarketPlaceGUI guiManager;
    
    // Item Search Components
    private JTextField itemSearchField;
    private JComboBox<String> categoryComboBox;
    private JButton itemSearchButton;
    private JTable itemResultsTable;
    private DefaultTableModel itemTableModel;
    private JButton buyButton;
    private JLabel itemStatusLabel;

    // Seller Search Components
    private JTextField sellerSearchField;
    private JButton sellerSearchButton;
    private JTable sellerResultsTable;
    private DefaultTableModel sellerTableModel;
    private JLabel sellerStatusLabel; // Label for seller search feedback

    // Define categories (include "All")
    private final String[] CATEGORIES = {"All Categories", "Electronics", "Books", "Clothing", "Home Goods", "Toys", "Sports", "Other"};

    public SearchBuyPanel(MarketPlaceGUI guiManager) {
        this.guiManager = guiManager;

        setLayout(new BorderLayout(10, 10)); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Search Controls Panel ---
        JPanel searchControlsPanel = new JPanel();
        searchControlsPanel.setLayout(new BoxLayout(searchControlsPanel, BoxLayout.Y_AXIS)); // Stack vertically

        // Item Search Row
        JPanel itemSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemSearchField = new JTextField(20);
        categoryComboBox = new JComboBox<>(CATEGORIES);
        itemSearchButton = new JButton("Search Items");
        itemSearchPanel.add(new JLabel("Keyword:"));
        itemSearchPanel.add(itemSearchField);
        itemSearchPanel.add(new JLabel("Category:"));
        itemSearchPanel.add(categoryComboBox);
        itemSearchPanel.add(itemSearchButton);
        searchControlsPanel.add(itemSearchPanel);

        // Seller Search Row
        JPanel sellerSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sellerSearchField = new JTextField(20);
        sellerSearchButton = new JButton("Search Sellers");
        sellerSearchPanel.add(new JLabel("Seller Username:"));
        sellerSearchPanel.add(sellerSearchField);
        sellerSearchPanel.add(sellerSearchButton);
        searchControlsPanel.add(sellerSearchPanel);

        // --- Center Results Area (Split Pane) ---
        // Item Results Panel (Table + Status Label)
        JPanel itemResultsPanel = new JPanel(new BorderLayout());
        itemResultsPanel.setBorder(BorderFactory.createTitledBorder("Item Search Results"));
        String[] itemColumnNames = {"Item Name", "Price", "Seller", "Rating", "Category"}; 
        itemTableModel = new DefaultTableModel(itemColumnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        itemResultsTable = new JTable(itemTableModel);
        itemResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemResultsTable.getTableHeader().setReorderingAllowed(false);
        itemResultsTable.getSelectionModel().addListSelectionListener(e -> {
            buyButton.setEnabled(itemResultsTable.getSelectedRow() != -1);
        });
        JScrollPane itemTableScrollPane = new JScrollPane(itemResultsTable);
        itemStatusLabel = new JLabel(" ");
        itemStatusLabel.setFont(itemStatusLabel.getFont().deriveFont(Font.ITALIC));
        itemStatusLabel.setForeground(Color.GRAY);
        itemResultsPanel.add(itemTableScrollPane, BorderLayout.CENTER);
        itemResultsPanel.add(itemStatusLabel, BorderLayout.SOUTH);
        
        // Seller Results Panel (Table + Status Label)
        JPanel sellerResultsPanel = new JPanel(new BorderLayout());
        sellerResultsPanel.setBorder(BorderFactory.createTitledBorder("Seller Search Results"));
        String[] sellerColumnNames = {"Seller Username", "Rating"}; 
        sellerTableModel = new DefaultTableModel(sellerColumnNames, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        sellerResultsTable = new JTable(sellerTableModel);
        sellerResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sellerResultsTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane sellerTableScrollPane = new JScrollPane(sellerResultsTable);
        sellerStatusLabel = new JLabel(" ");
        sellerStatusLabel.setFont(sellerStatusLabel.getFont().deriveFont(Font.ITALIC));
        sellerStatusLabel.setForeground(Color.GRAY);
        sellerResultsPanel.add(sellerTableScrollPane, BorderLayout.CENTER);
        sellerResultsPanel.add(sellerStatusLabel, BorderLayout.SOUTH);
        
        // Split Pane to hold both results panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                                            itemResultsPanel, sellerResultsPanel);
        splitPane.setResizeWeight(0.6); 

        // --- Bottom Buy Button Panel ---
        JPanel buyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buyButton = new JButton("Buy Selected Item");
        buyButton.setEnabled(false); 
        buyPanel.add(buyButton);

        // --- Add Components to Main Panel ---
        add(searchControlsPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buyPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        itemSearchButton.addActionListener(e -> {
            itemStatusLabel.setText(" "); 
            sellerStatusLabel.setText(" ");
            
            String keyword = itemSearchField.getText().trim();
            String category = (String) categoryComboBox.getSelectedItem();
            if (category == null) return; 

            if ("All Categories".equals(category)) {
                guiManager.requestSearchItems(keyword); 
            } else {
                if (!keyword.isEmpty()) {
                     JOptionPane.showMessageDialog(this, 
                        "Searching by selected category ('" + category + "'). Keyword ignored.", 
                        "Search Info", JOptionPane.INFORMATION_MESSAGE);
                }
                guiManager.requestSearchByCategory(category);
            }
        });

        sellerSearchButton.addActionListener(e -> {
            itemStatusLabel.setText(" "); 
            sellerStatusLabel.setText("Searching...");
            
            String query = sellerSearchField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(SearchBuyPanel.this, 
                    "Please enter a seller username (or part of it) to search.", 
                    "Seller Search Error", JOptionPane.WARNING_MESSAGE);
                sellerStatusLabel.setText(" ");
                return;
            }
            guiManager.requestSearchSeller(query);
        });

        buyButton.addActionListener(e -> {
            int selectedRow = itemResultsTable.getSelectedRow();
            if (selectedRow != -1) {
                String itemName = (String) itemTableModel.getValueAt(selectedRow, 0);
                 int confirmation = JOptionPane.showConfirmDialog(SearchBuyPanel.this,
                    "Are you sure you want to buy '" + itemName + "'?",
                    "Confirm Purchase", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    guiManager.requestBuyItem(itemName);
                }
            } else {
                 buyButton.setEnabled(false);
            }
        });
    }

    // Update Item Search Results Table
    public void updateSearchResults(ArrayList<String[]> searchData) {
        SwingUtilities.invokeLater(() -> {
            itemTableModel.setRowCount(0);
            if (searchData != null && !searchData.isEmpty()) {
                for (String[] rowData : searchData) {
                    if (rowData.length == 5) { 
                        itemTableModel.addRow(rowData);
                    } else {
                         System.err.println("SearchBuyPanel: Received item data with incorrect column count: " + rowData.length);
                    }
                }
                itemStatusLabel.setText(" ");
            } else {
                itemStatusLabel.setText("No items found matching your criteria.");
            }
            itemResultsTable.clearSelection();
            buyButton.setEnabled(false);
        });
    }

    // Update Seller Search Results Table
    public void updateSellerSearchResults(ArrayList<String[]> sellerData) {
        SwingUtilities.invokeLater(() -> {
            sellerTableModel.setRowCount(0);
            if (sellerData != null && !sellerData.isEmpty()) {
                for (String[] rowData : sellerData) {
                    if (rowData.length == 2) {
                        sellerTableModel.addRow(rowData);
                    } else {
                        System.err.println("SearchBuyPanel: Received seller data with incorrect column count: " + rowData.length);
                    }
                }
                 sellerStatusLabel.setText(" ");
            } else {
                 sellerStatusLabel.setText("No sellers found matching your query.");
            }
            sellerResultsTable.clearSelection();
        });
    }

    // Method to clear the search results table
    public void clearSearchResultsTable() {
        SwingUtilities.invokeLater(() -> {
            itemTableModel.setRowCount(0);
            itemStatusLabel.setText(" "); // Clear status too
            itemResultsTable.clearSelection();
            buyButton.setEnabled(false);
        });
    }
} 