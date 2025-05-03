import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/**
 * UserManager.java
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Shankh Gupta
 * @version April 6th, 2025
 */
public class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private String usersFile = "users.txt";

    public UserManager(String usersFile) {
        this.usersFile = usersFile;
        loadUsers();
    }

    // Add a user to the users list
    public synchronized void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    // Delete a user by username
    public synchronized void deleteUser(String username) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.remove(i);
                break;
            }
        }
        saveUsers();
    }

    // Get a user by username
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Update a user's balance
    public synchronized void updateUserBalance(String username, double newBalance) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.updateBalance(newBalance - user.getBalance()); // Adjust balance
                saveUsers();
                return;
            }
        }
    }

    // Search for users by username substring (case-insensitive)
    public synchronized ArrayList<User> searchUsers(String query) {
        ArrayList<User> matchedUsers = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return matchedUsers; 
        }
        String lowerCaseQuery = query.toLowerCase().trim();
        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(lowerCaseQuery)) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }

    // Load users from the users.txt file
    private void loadUsers() {
        users.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                User user = null;
                if (parts.length == 5) { // Format: username,password,balance,totalRatingSum,numberOfRatings
                    String username = parts[0];
                    String password = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    int totalRatingSum = Integer.parseInt(parts[3]);
                    int numberOfRatings = Integer.parseInt(parts[4]);
                    user = new User(username, password, balance);
                    user.setTotalRatingSum(totalRatingSum);
                    user.setNumberOfRatings(numberOfRatings);
                } else if (parts.length == 3) { // Old Format: username, password, balance
                    String username = parts[0];
                    String password = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    user = new User(username, password, balance);
                }

                if (user != null) {
                    users.add(user);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found. Starting with an empty user list.");
        } catch (IOException | NumberFormatException e) { 
            System.out.println("Error reading or parsing users file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Save users to the users.txt file
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
            for (User user : users) {
                // Format: username,password,balance,totalRatingSum,numberOfRatings
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getBalance() +
                             "," + user.getTotalRatingSum() + "," + user.getNumberOfRatings());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to users file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}