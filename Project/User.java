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
    private int totalRatingSum;   // Added for seller ratings
    private int numberOfRatings; // Added for seller ratings

    // Constructor with balance parameter
    public User(String username, String password, double balance) {
        this.userName = username;
        this.userPassword = password;
        this.balance = balance;
        this.totalRatingSum = 0;  // Initialize ratings
        this.numberOfRatings = 0; // Initialize ratings
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

    // Setter for loading from file
    @Override
    public void setTotalRatingSum(int sum) {
        this.totalRatingSum = sum;
    }

    // Setter for loading from file
    @Override
    public void setNumberOfRatings(int count) {
        this.numberOfRatings = count;
    }

    // Seller rating methods
    @Override
    public void addRating(int rating) {
        if (rating >= 1 && rating <= 5) { // Basic validation
            this.totalRatingSum += rating;
            this.numberOfRatings++;
        }
    }

    // Seller rating methods
    @Override
    public int getTotalRatingSum() {
        return totalRatingSum;
    }

    // Seller rating methods
    @Override
    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    // Seller rating methods
    @Override
    public double getAverageRating() {
        if (numberOfRatings == 0) {
            return 0.0; // Avoid division by zero, return neutral rating
        }
        return (double) totalRatingSum / numberOfRatings;
    }
}