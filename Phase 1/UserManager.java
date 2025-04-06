import java.io.*;
import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private final String usersFile = "users.txt";

    public UserManager() {
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

    // Load users from the users.txt file
    private void loadUsers() {
        users.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) { // username, password, balance
                    String username = parts[0];
                    String password = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    users.add(new User(username, password, balance));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found. Starting with an empty user list.");
        } catch (IOException e) {
            System.out.println("Error reading users file.");
            e.printStackTrace();
        }
    }

    // Save users to the users.txt file
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
            for (User user : users) {
                writer.write(user.getUsername() + "," + user.getPassword() + "," + user.getBalance());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to users file.");
            e.printStackTrace();
        }
    }
}