import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Login Frame
 *
 * Provides the GUI for user login, registration navigation, and account deletion.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton deleteButton; // Added delete button
    private MarketplaceGUI guiManager; // Reference to the main GUI controller

    public LoginFrame(MarketplaceGUI guiManager) {
        this.guiManager = guiManager;

        setTitle("Marketplace Login / Register");
        // Increased height slightly for the third button
        setSize(400, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application if this frame closes
        setLocationRelativeTo(null); // Center on screen

        // Components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Create Account");
        deleteButton = new JButton("Delete Account"); // Initialize delete button
        deleteButton.setForeground(java.awt.Color.RED); // Make delete button red

        // Layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Row 0: Username 
        // Username Label (Column 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;  // Explicitly 1
        gbc.weightx = 0.0; // Don't expand label horizontally
        gbc.fill = GridBagConstraints.NONE; // Don't fill space
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Username:"), gbc);

        // Username Field (Column 1 & 2)
        gbc.gridx = 1;
        // gbc.gridy = 0; // Stays 0
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.weightx = 1.0; // Allow field to expand horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill available horizontal space
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);

        // Row 1: Password 
        // Password Label (Column 0)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.weightx = 0.0; // Reset weightx
        gbc.fill = GridBagConstraints.NONE; // Reset fill
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Password:"), gbc);

        // Password Field (Column 1 & 2)
        gbc.gridx = 1;
        // gbc.gridy = 1; // Stays 1
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.weightx = 1.0; // Allow field to expand horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill available horizontal space
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        // Row 2: Buttons 
        // Buttons Panel (FlowLayout)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Added hgap
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(deleteButton); // Add delete button to panel

        // Add button panel to the main panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3; // Span all columns
        gbc.weightx = 0.0; // Buttons panel shouldn't expand horizontally on its own
        gbc.fill = GridBagConstraints.NONE; // Don't resize button panel
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel);

        // Action Listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                guiManager.attemptLogin(username, password);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiManager.showRegistration();
            }
        });

        // Action listener for the Delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.trim().isEmpty() || password.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Please enter Username and Password to delete the account.",
                        "Deletion Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Confirmation dialog
                int confirmation = JOptionPane.showConfirmDialog(LoginFrame.this,
                    "Are you sure you want to permanently delete the account '" + username + "'?\nThis action cannot be undone.",
                    "Confirm Account Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (confirmation == JOptionPane.YES_OPTION) {
                    // Pass deletion attempt to the main GUI manager
                    guiManager.attemptDeleteAccount(username, password);
                }
            }
        });
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}