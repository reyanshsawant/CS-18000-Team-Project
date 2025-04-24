/**
 * Server
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Neil Lapsia
 * @version April 19th, 2025
 */

public class ServerMain {
    public static void main(String[] args) {
        MarketPlaceServer server = new MarketPlaceServer(4242);
        new Thread(server).start();
    }
}