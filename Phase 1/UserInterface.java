/**
 * UserInterface.java
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Shankh Gupta
 * @version April 6th, 2025
 */
public interface UserInterface {
    void createAccount(String username, String password);
    boolean login(String username, String password);
    void deleteAccount(String username);
    double getBalance();
    void updateBalance(double amount);
}