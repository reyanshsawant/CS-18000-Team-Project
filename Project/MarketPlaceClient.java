import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Market Place Client
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Reyansh Sawant
 * @version April 14th, 2025
 */

public class MarketPlaceClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public MarketPlaceClient(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Unable to connect to server: " + e.getMessage());
        }
    }

    public void start() {
        if (socket == null || out == null || in == null) {
            System.out.println("Client not properly initialized.");
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                StringBuilder block = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    block.append(line).append("\n");
                    if (line.trim().endsWith(":") || line.toLowerCase().contains("goodbye!")) {
                        break;
                    }
                }

                // If server has closed the connection
                if (line == null) {
                    System.out.println("Server disconnected.");
                    break;
                }

                // Show server output
                System.out.print(block.toString());

                // Exit if the server said goodbye
                if (block.toString().toLowerCase().contains("goodbye!")) {
                    break;
                }

                // Get user input
                String userInput = scanner.nextLine();
                out.println(userInput);
                out.flush(); // ensure input is sent immediately
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        MarketPlaceClient client = new MarketPlaceClient("localhost", 15000);
        client.start();
    }
}
