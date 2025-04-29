import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

/**
 * Search/Buy Panel
 *
 * Panel within the Marketplace Dashboard for searching items and initiating purchases.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public class SearchBuyPanel extends JPanel {

    private MarketPlaceGUI guiManager;
    private JTextField searchField;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton buyButton;

    public SearchBuyPanel(MarketPlaceGUI guiManager) {
        this.guiManager = guiManager;

        setLayout(new BorderLayout(10, 10)); // Add gaps between components
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Input Panel (Top) ---
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(30);
        searchButton = new JButton("Search Items");

        searchInputPanel.add(new JLabel("Search Keyword:"));
        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);

        // Results Table (Center) ---
        String[] columnNames = {"Item Name", "Price", "Seller"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make table cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row selection
        resultsTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering

        // Add listener to enable/disable buy button based on selection
        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Prevent handling intermediate selection events
                 buyButton.setEnabled(resultsTable.getSelectedRow() != -1);
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(resultsTable);

        // Buy Button Panel (Bottom) ---
        JPanel buyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buyButton = new JButton("Buy Selected Item");
        buyButton.setEnabled(false); // Initially disabled
        buyPanel.add(buyButton);

        // Add Panels to Main Layout ---
        add(searchInputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buyPanel, BorderLayout.SOUTH);

        // Action Listeners ---
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                // Basic validation - allow empty search to show all?
                // if (keyword.trim().isEmpty()) {
                //     JOptionPane.showMessageDialog(SearchBuyPanel.this, "Please enter a search keyword.", "Search Error", JOptionPane.WARNING_MESSAGE);
                //     return;
                // }
                guiManager.requestSearchItems(keyword); // Ask manager to perform search
            }
        });

        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = resultsTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get item name from the first column of the selected row
                    String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                    // Confirmation dialog
                     int confirmation = JOptionPane.showConfirmDialog(SearchBuyPanel.this,
                        "Are you sure you want to buy '" + itemName + "'?",
                        "Confirm Purchase",
                        JOptionPane.YES_NO_OPTION);

                    if (confirmation == JOptionPane.YES_OPTION) {
                        guiManager.requestBuyItem(itemName);
                    }
                } else {
                     buyButton.setEnabled(false);
                }
            }
        });
    }

    public void updateSearchResults(ArrayList<String[]> searchData) {
         // Ensure update happens on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Clear previous results
            tableModel.setRowCount(0);
            // Add new rows
            if (searchData != null) {
                for (String[] rowData : searchData) {
                    tableModel.addRow(rowData);
                }
            }
            if (tableModel.getRowCount() == 0) { }
            // Clear selection and disable buy button after updating results
            resultsTable.clearSelection();
            buyButton.setEnabled(false);
        });
    }
} 