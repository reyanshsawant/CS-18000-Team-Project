import java.io.IOException;

public interface User {
    String getUsername();
    boolean authenticate(String password);
    double getBalance();
    void addFunds(double amount);
    boolean makePurchase(double amount);
    void displayUserInfo();

    // File-based authentication (login or create user)
    static User loginOrCreate(String username, String password) throws IOException {
        return MarketplaceUser.loginOrCreateUser(username, password);
    }

    // Delete user account
    static boolean deleteAccount(String username, String password) throws IOException {
        return MarketplaceUser.deleteUser(username, password);
    }
}
