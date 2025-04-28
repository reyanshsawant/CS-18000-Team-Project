/**
 * Client interface
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Shankh Gupta
 * @version April 17th, 2025
 */
public interface ClientInterface {
    void sendCommand(String command);
    String receiveResponse() throws java.io.IOException;
}
