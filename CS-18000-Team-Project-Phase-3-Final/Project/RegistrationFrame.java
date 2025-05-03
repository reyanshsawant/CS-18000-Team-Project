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
 * Registration Frame
 *
 * Provides the GUI for new user registration.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public class RegistrationFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField balanceField;
    private JButton registerButton;
    private JButton backButton;
    private MarketPlaceGUI guiManager;

    public RegistrationFrame(MarketPlaceGUI guiManager) {
        this.guiManager = guiManager;

        setTitle("Create Marketplace Account");
        setSize(400, 250); // Slightly larger to accommodate balance field
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose this frame, don't exit app
        setLocationRelativeTo(null); // Center on screen
        // Prevent closing LoginFrame when this opens by setting default close operation
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                guiManager.showLogin(); // Show login frame when this one is closed
            }
        });


        // Components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        balanceField = new JTextField(20);
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        // Layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.anchor = GridBagConstraints.EAST;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Choose Username:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Choose Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        // Initial Balance
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Initial Balance ($):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(balanceField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel);

        // Action Listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String balanceStr = balanceField.getText();

                // Basic validation
                if (username.trim().isEmpty() || password.trim().isEmpty() || balanceStr.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(RegistrationFrame.this,
                            "All fields must be filled.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    double balance = Double.parseDouble(balanceStr);
                    if (balance < 0) {
                         JOptionPane.showMessageDialog(RegistrationFrame.this,
                            "Balance cannot be negative.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Pass registration attempt to the main GUI manager
                    guiManager.attemptRegistration(username, password, balanceStr);
                } catch (NumberFormatException ex) {
                     JOptionPane.showMessageDialog(RegistrationFrame.this,
                            "Invalid balance format. Please enter a number.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiManager.showLogin(); // Tell manager to switch back
            }
        });
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        balanceField.setText("");
    }
} 