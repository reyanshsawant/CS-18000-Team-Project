/**
 * UserInterface.java
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Shankh Gupta
 * @version April 6th, 2025
 */
public interface UserInterface {
    String getUsername();
    String getPassword();
    double getBalance();
    int getTotalRatingSum();
    int getNumberOfRatings();
    double getAverageRating();

    void setUsername(String username);
    void setPassword(String password);
    void setBalance(double balance);
    void setTotalRatingSum(int sum);
    void setNumberOfRatings(int count);
    
    void createAccount(String username, String password);
    boolean login(String username, String password);
    void deleteAccount(String username);
    
    void updateBalance(double amount);
    void addRating(int rating);
}