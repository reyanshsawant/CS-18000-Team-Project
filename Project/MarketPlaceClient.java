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
            while (true) {
                StringBuilder block = new StringBuilder();
                String line;
                String lastLine = "";

                while ((line = in.readLine()) != null) {
                    block.append(line).append("\n");
                    lastLine = line.trim();
                    if (lastLine.endsWith(":") || lastLine.toLowerCase().contains("goodbye!") || lastLine.toLowerCase().contains("logged out.")) {
                        break;
                    }
                    if (lastLine.toLowerCase().startsWith("login successful.") || lastLine.toLowerCase().startsWith("invalid credentials.")) {
                        break;
                    }
                    if (lastLine.toLowerCase().startsWith("account created successfully.") || lastLine.toLowerCase().startsWith("username already exists.")) {
                        break;
                    }
                    if (lastLine.toLowerCase().startsWith("account deleted successfully.")) {
                        break;
                    }
                }

                String serverMessage = block.toString();

                if (line == null) {
                    guiCallback.connectionLost();
                    break;
                }

                guiCallback.displayServerMessage(serverMessage);

                if (lastLine.toLowerCase().startsWith("login successful.")) {
                    try {
                        loggedInUser = lastLine.split("Welcome ")[1].trim();
                        guiCallback.loginSuccess(loggedInUser);
                    } catch (Exception e) {
                        System.err.println("Error parsing username from login success message: " + lastLine);
                        guiCallback.loginSuccess("UnknownUser");
                    }
                } else if (lastLine.toLowerCase().startsWith("invalid credentials.")) {
                    guiCallback.loginFailure();
                }

                if (lastLine.toLowerCase().contains("goodbye!") || lastLine.toLowerCase().contains("logged out.")) {
                    loggedInUser = null;
                    guiCallback.clientDisconnected();
                    break;
                }

                if (lastLine.endsWith(":")) {
                    String userInput = guiCallback.getUserInput();

                    if (userInput == null) {
                        System.err.println("GUI did not provide input. Closing connection.");
                        break;
                    }

                    out.println(userInput);
                    out.flush();
                }
            }
        } catch (IOException e) {
            guiCallback.connectionLost();
        } finally {
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
            MarketplaceGUI gui = new MarketplaceGUI();
            gui.startClientOrReconnect();
        });
    }
}
