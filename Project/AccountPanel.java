import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Account Panel
 *
 * Panel within the Marketplace Dashboard to display account information,
 * primarily the user's balance.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 28, 2025
 */
public class AccountPanel extends JPanel {

    private MarketPlaceGUI guiManager;
    private JLabel balanceLabel;
    private JLabel balanceValueLabel;
    private JButton checkBalanceButton;

    public AccountPanel(MarketPlaceGUI guiManager) {
        this.guiManager = guiManager;

        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Simple left-aligned flow layout
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        balanceLabel = new JLabel("Current Balance:");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Initial placeholder text - will be updated on demand or after purchase
        balanceValueLabel = new JLabel("Click button to check ->");
        balanceValueLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        checkBalanceButton = new JButton("Check Balance");

        add(balanceLabel);
        add(balanceValueLabel);
        add(checkBalanceButton);

        // Action Listener for the button
        checkBalanceButton.addActionListener(e -> {
            guiManager.requestCheckBalance();
        });
    }

    public void setBalance(String balanceText) {
        // Ensure update happens on the Event Dispatch Thread if called from another thread
        SwingUtilities.invokeLater(() -> {
            balanceValueLabel.setText(balanceText);
        });
    }
} 