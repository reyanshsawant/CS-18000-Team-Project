import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = User.loginOrCreate(username, password);

            if (user != null) {
                System.out.println("Login successful!");
                user.displayUserInfo();

                while (true) {
                    System.out.println("\nChoose an option:");
                    System.out.println("1. Add Funds");
                    System.out.println("2. Make Purchase");
                    System.out.println("3. Delete Account");
                    System.out.println("4. Exit");
                    System.out.print("Enter choice: ");

                    int choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            System.out.print("Enter amount to add: ");
                            double addAmount = scanner.nextDouble();
                            user.addFunds(addAmount);
                            break;
                        case 2:
                            System.out.print("Enter purchase amount: ");
                            double purchaseAmount = scanner.nextDouble();
                            user.makePurchase(purchaseAmount);
                            break;
                        case 3:
                            System.out.print("Are you sure you want to delete your account? (yes/no): ");
                            scanner.nextLine(); // Consume newline
                            String confirm = scanner.nextLine();
                            if (confirm.equalsIgnoreCase("yes")) {
                                if (User.deleteAccount(username, password)) {
                                    System.out.println("Account deleted. Exiting...");
                                    return; // Exit program
                                }
                            }
                            break;
                        case 4:
                            System.out.println("Goodbye!");
                            return;
                        default:
                            System.out.println("Invalid option.");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error accessing user file.");
        }
    }
}
