import java.io.*;
import java.util.Scanner;

public class MarketplaceUser implements User {
    private String username;
    private String password;
    private double balance;
    private static final String FILE_NAME = "users.txt" ; // User data file

    private MarketplaceUser(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean authenticate(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void addFunds(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Added $" + amount + " to your balance.");
            updateUserData();
        } else {
            System.out.println("Invalid amount.");
        }
    }

    @Override
    public boolean makePurchase(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            System.out.println("Purchase successful! Deducted $" + amount);
            updateUserData();
            return true;
        } else {
            System.out.println("Insufficient funds.");
            return false;
        }
    }

    @Override
    public void displayUserInfo() {
        System.out.println("User: " + username);
        System.out.println("Balance: $" + balance);
    }

    public static User loginOrCreateUser(String username, String password) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) file.createNewFile();

        Scanner scanner = new Scanner(file);
        StringBuilder fileData = new StringBuilder();
        boolean userExists = false;
        double balance = 0.0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");

            if (parts[0].equals(username)) {
                userExists = true;
                if (parts[1].equals(password)) {
                    balance = Double.parseDouble(parts[2]);
                } else {
                    System.out.println("Incorrect password.");
                    return null;
                }
            }
            fileData.append(line).append("\n");
        }
        scanner.close();

        if (!userExists) {
            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.write(username + "," + password + ",0.0\n");
            writer.close();
            System.out.println("New account created!");
            balance = 0.0;
        }

        return new MarketplaceUser(username, password, balance);
    }

    public static boolean deleteUser(String username, String password) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No user database found.");
            return false;
        }

        Scanner scanner = new Scanner(file);
        StringBuilder fileData = new StringBuilder();
        boolean userDeleted = false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");

            if (parts[0].equals(username)) {
                if (parts[1].equals(password)) {
                    userDeleted = true;
                    continue;
                } else {
                    System.out.println("Incorrect password. Cannot delete account.");
                    return false;
                }
            }
            fileData.append(line).append("\n");
        }
        scanner.close();

        FileWriter writer = new FileWriter(FILE_NAME);
        writer.write(fileData.toString());
        writer.close();

        return userDeleted;
    }

    private void updateUserData() {
        try {
            File file = new File(FILE_NAME);
            Scanner scanner = new Scanner(file);
            StringBuilder fileData = new StringBuilder();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts[0].equals(username)) {
                    fileData.append(username).append(",").append(password).append(",").append(balance).append("\n");
                } else {
                    fileData.append(line).append("\n");
                }
            }
            scanner.close();

            FileWriter writer = new FileWriter(FILE_NAME);
            writer.write(fileData.toString());
            writer.close();

        } catch (IOException e) {
            System.out.println("Error updating user data.");
        }
    }
}
