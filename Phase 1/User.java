/**
 * User.java
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Shankh Gupta
 * @version April 6th, 2025
 */
public class User implements UserInterface {
    private String userName;
    private String userPassword;
    private double balance;

    // Constructor with balance parameter
    public User(String username, String password, double balance) {
        this.userName = username;
        this.userPassword = password;
        this.balance = balance;
    }

    @Override
    public void createAccount(String username, String password) {
        this.userName = username;
        this.userPassword = password;
    }

    @Override
    public boolean login(String username, String password) {
        return this.userName.equals(username) && this.userPassword.equals(password);
    }

    @Override
    public void deleteAccount(String username) {
        this.userName = username;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void updateBalance(double amount) {
        this.balance += amount;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return userPassword;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public void setPassword(String password) {
        this.userPassword = password;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

}