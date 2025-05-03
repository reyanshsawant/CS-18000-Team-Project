import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Manage Listings Panel
 *
 * Panel within the Marketplace Dashboard for viewing and deleting the user's own listings.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public class ManageListingsPanel extends JPanel {

    private MarketPlaceGUI guiManager;
    private JTable listingsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton deleteButton;

    public ManageListingsPanel(MarketPlaceGUI guiManager) {
        this.guiManager = guiManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel for Refresh Button 
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButton = new JButton("Refresh My Listings");
        topPanel.add(refreshButton);

        // Listings Table (Center) 
        String[] columnNames = {"Item Name", "Price", "Category"}; // Adjust columns as needed
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        listingsTable = new JTable(tableModel);
        listingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listingsTable.getTableHeader().setReorderingAllowed(false);

        // Listener to enable delete button
        listingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(listingsTable.getSelectedRow() != -1);
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(listingsTable);

        // Bottom Panel for Delete Button 
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteButton = new JButton("Delete Selected Listing");
        deleteButton.setEnabled(false); // Initially disabled
        deleteButton.setForeground(Color.RED);
        bottomPanel.add(deleteButton);

        // Add Components 
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners 
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiManager.requestMyListings(); // Ask manager to fetch listings
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = listingsTable.getSelectedRow();
                if (selectedRow != -1) {
                    String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                    int confirmation = JOptionPane.showConfirmDialog(ManageListingsPanel.this,
                            "Are you sure you want to delete your listing for '" + itemName + "'?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (confirmation == JOptionPane.YES_OPTION) {
                        guiManager.requestDeleteListing(itemName); // Ask manager to delete
                    }
                } else {
                     deleteButton.setEnabled(false);
                }
            }
        });

    }

    public void updateListingsTable(ArrayList<String[]> listingsData) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // Clear previous data
            if (listingsData != null) {
                for (String[] rowData : listingsData) {
                    tableModel.addRow(rowData);
                }
            }
            listingsTable.clearSelection();
            deleteButton.setEnabled(false);
        });
    }
} 