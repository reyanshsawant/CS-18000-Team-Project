import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

/**
 * Market Place Client
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Reyansh Sawant
 * @version April 21, 2025 (GUI Integration)
 */

public class MarketPlaceClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GuiCallback guiCallback;
    private String loggedInUser = null;

    public MarketPlaceClient(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            if (this.socket != null && this.socket.isConnected()) {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } else {
                this.socket = null;
            }
        } catch (IOException e) {
            this.socket = null;
            System.err.println("Unable to connect to server: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return this.socket != null && this.out != null && this.in != null && this.socket.isConnected();
    }

    public void start(GuiCallback callback) {
        this.guiCallback = callback;

        if (!isConnected() || guiCallback == null) {
            System.err.println("Client not properly initialized or GUI callback missing.");
            if (guiCallback != null) {
                guiCallback.connectionLost();
            }
            return;
        }

        try {
            String line;
            while ((line = in.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    System.out.println("CLIENT_LOOP: Received line: [" + trimmedLine + "]");
                    guiCallback.displayServerMessage(trimmedLine);
                }

                if (trimmedLine.toLowerCase().startsWith("login successful.")) {
                    try {
                        loggedInUser = trimmedLine.split("Welcome ")[1].trim();
                        System.out.println("CLIENT_LOOP: Login success detected.");
                        guiCallback.loginSuccess(loggedInUser);
                    } catch (Exception e) {
                        System.err.println("Error parsing username from login success message: " + trimmedLine);
                        guiCallback.loginSuccess("UnknownUser");
                    }
                } else if (trimmedLine.toLowerCase().startsWith("invalid credentials.")) {
                    System.out.println("CLIENT_LOOP: Login failure detected.");
                    guiCallback.loginFailure();
                }

                if (trimmedLine.toLowerCase().contains("goodbye!") || trimmedLine.toLowerCase().contains("logged out.")) {
                    System.out.println("CLIENT_LOOP: Logout/Goodbye detected.");
                    loggedInUser = null;
                    guiCallback.clientDisconnected();
                    break;
                }

                // If the line ends with a colon, assume it's a prompt requiring input
                // UNLESS it's a known intermediate message that doesn't actually need input yet.
                boolean isIgnoredInfoPrompt = trimmedLine.startsWith("No items listed by");
                
                if (trimmedLine.endsWith(":") && !isIgnoredInfoPrompt) {
                    System.out.println("CLIENT_LOOP: Prompt detected: [" + trimmedLine + "]. Requesting input from GUI.");
                    String userInput = guiCallback.getUserInput();

                    if (userInput == null) {
                        System.err.println("GUI did not provide input. Closing connection.");
                        break;
                    }
                    
                    System.out.println("CLIENT_LOOP: Sending input: [" + userInput + "]");
                    out.println(userInput);
                    out.flush();
                    
                    if (out.checkError()) {
                        System.err.println("CLIENT_LOOP: Error sending data to server.");
                        guiCallback.connectionLost();
                        break;
                    }
                    System.out.println("CLIENT_LOOP: Input sent successfully.");
                }
            }
            
            if (line == null) {
                System.out.println("CLIENT_LOOP: Server closed connection (readLine returned null).");
                guiCallback.connectionLost();
            }

        } catch (IOException e) {
            System.err.println("CLIENT_LOOP: IOException during read/write: " + e.getMessage());
            guiCallback.connectionLost();
        } finally {
            System.out.println("CLIENT_LOOP: Exiting start method, closing connection.");
            closeConnection();
        }
    }

    private void closeConnection() {
        loggedInUser = null;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing client resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MarketPlaceGUI gui = new MarketPlaceGUI();
            gui.startClientOrReconnect();
        });
    }
}
