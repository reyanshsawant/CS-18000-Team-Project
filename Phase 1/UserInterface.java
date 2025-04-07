public interface UserInterface {
    void createAccount(String username, String password);
    boolean login(String username, String password);
    void deleteAccount(String username);
    double getBalance();
    void updateBalance(double amount);
}