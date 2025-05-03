/**
 * GUI Callback Interface
 *
 * Defines the methods the MarketPlaceClient will use to interact with the GUI.
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 21, 2025
 */
public interface GuiCallback {
    void displayServerMessage(String message);

    String getUserInput();

    void loginSuccess(String username);

    void loginFailure();

    void connectionLost();

    void clientDisconnected();
} 