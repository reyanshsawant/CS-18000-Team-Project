import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * List Item Panel
 *
 * Panel within the Marketplace Dashboard for users to list new items for sale.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public class ListItemPanel extends JPanel {

    private MarketPlaceGUI guiManager;

    private JTextField nameField;
    private JTextArea descriptionArea; // Use JTextArea for potentially longer descriptions
    private JTextField priceField;
    private JTextField categoryField;
    private JButton listButton;

    public ListItemPanel(MarketPlaceGUI guiManager) {
        this.guiManager = guiManager;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Item Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(30);
        add(nameField, gbc);

        // Row 1: Description 
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align label top-left with text area
        add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Allow description area to expand vertically
        gbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
        descriptionArea = new JTextArea(5, 30); // Rows, Columns
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        add(descriptionScrollPane, gbc);

        // Row 2: Price 
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0; // Reset vertical weight
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Price ($):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        priceField = new JTextField(10);
        add(priceField, gbc);

         // Row 3: Category 
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        categoryField = new JTextField(20);
        add(categoryField, gbc);

        // Row 4: List Button 
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span columns
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        listButton = new JButton("List This Item");
        add(listButton, gbc);

        // Action Listener for the button
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String description = descriptionArea.getText();
                String priceStr = priceField.getText();
                String category = categoryField.getText();

                // Basic Validation
                if (name.trim().isEmpty() || description.trim().isEmpty() || priceStr.trim().isEmpty() || category.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ListItemPanel.this,
                            "All fields must be filled.", "Listing Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double price = Double.parseDouble(priceStr);
                    if (price < 0) {
                        JOptionPane.showMessageDialog(ListItemPanel.this,
                                "Price cannot be negative.", "Listing Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Call the GUI manager to handle the listing request
                    guiManager.requestListItem(name, description, priceStr, category);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ListItemPanel.this,
                            "Invalid price format. Please enter a number.", "Listing Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

     // Method to clear fields after successful listing
    public void clearFields() {
        nameField.setText("");
        descriptionArea.setText("");
        priceField.setText("");
        categoryField.setText("");
    }
} 