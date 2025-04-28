import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Market Place Server
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Reyansh Sawant
 * @version April 17th, 2025
 */

public class MarketPlaceServer implements Runnable, ServerInterface {
    private final int port;
    private boolean running = false;
    private ServerSocket serverSocket;

    private final UserManager userManager;
    private final ItemManager itemManager;
    private final MessageManager messageManager;
    private final RatingManager ratingManager;
    private final SoldItemManager soldItemManager;

    public MarketPlaceServer(int port) {
        this.port = port;
        this.userManager = new UserManager("users.txt");
        this.itemManager = new ItemManager("items.txt");
        this.messageManager = new MessageManager("messages.txt");
        this.ratingManager = new RatingManager();
        this.soldItemManager = new SoldItemManager();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Marketplace server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Spawn new thread for each client
                ClientHandler handler = new ClientHandler(clientSocket, userManager, itemManager,
                        messageManager, ratingManager, soldItemManager);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    @Override
    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket.");
        }
    }
}
