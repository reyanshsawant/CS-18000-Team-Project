import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * RunLocalTestCases
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Shankh Gupta, Arjun Anilkumar
 * @version April 17th, 2025
 */


@RunWith(Enclosed.class)
public class RunLocalTestCase {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class, ClientServerTest.class);
        if (result.wasSuccessful()) {
            System.out.println("cool - all tests passed");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    public static class TestCase {
        private static final String TEST_USERS_FILE = "test_users.txt";
        private static final String TEST_ITEMS_FILE = "test_items.txt";
        private static final String TEST_MESSAGES_FILE = "test_messages.txt";

        private UserManager userHandler;
        private ItemManager itemHandler;
        private MessageManager messageHandler;

        @Before
        public void setUp() throws IOException {
            new PrintWriter(TEST_USERS_FILE).close();
            new PrintWriter(TEST_ITEMS_FILE).close();
            new PrintWriter(TEST_MESSAGES_FILE).close();
            userHandler = new UserManager(TEST_USERS_FILE);
            itemHandler = new ItemManager(TEST_ITEMS_FILE);
            messageHandler = new MessageManager(TEST_MESSAGES_FILE);
        }

        @After
        public void tearDown() {
            new File(TEST_USERS_FILE).delete();
            new File(TEST_ITEMS_FILE).delete();
            new File(TEST_MESSAGES_FILE).delete();
        }

        @Test(timeout = 1000)
        public void userCreationTest() {
            userHandler.addUser(new User("testUser1", "pass123", 100.0));
            userHandler.addUser(new User("testUser2", "pass456", 200.0));
            assertNotNull(userHandler.getUser("testUser1"));
            assertNotNull(userHandler.getUser("testUser2"));
        }

        @Test(timeout = 1000)
        public void itemListingTest() {
            Item laptop = new Item("MacBook", "apple laptop", 1299.99);
            laptop.setSellerName("seller1");
            Item phone = new Item("iPhone", "latest model", 999.99);
            phone.setSellerName("seller2");
            itemHandler.addItem(laptop);
            itemHandler.addItem(phone);
            assertEquals(1, itemHandler.searchItemsByName("MacBook").size());
            assertEquals(1, itemHandler.searchItemsByName("iPhone").size());
        }

        @Test(timeout = 1000)
        public void testAddItems() {
            Item item = new Item("TestItem", "description", 50.0);
            item.setSellerName("testSeller");
            itemHandler.addItem(item);
            assertEquals(1, itemHandler.searchItemsByName("TestItem").size());
        }

        @Test(timeout = 1000)
        public void messageFunctionTest() {
            messageHandler.sendMessage("userA", "userB", "hey");
            messageHandler.sendMessage("userB", "userA", "what's up?");
            ArrayList<Message> userAMessages = messageHandler.getMessagesForUser("userA");
            assertEquals(1, userAMessages.size());
            assertEquals("what's up?", userAMessages.get(0).getContent());
        }

        @Test(timeout = 1000)
        public void accountDeletionTest() {
            userHandler.addUser(new User("tempUser", "tempPass", 0.0));
            userHandler.deleteUser("tempUser");
            assertNull(userHandler.getUser("tempUser"));
        }
    }

    public static class ClientServerTest {
        private static final int TEST_PORT = 23456;
        private Thread serverThread;

        @Before
        public void startServer() {
            serverThread = new Thread(() -> {
                MarketPlaceServer server = new MarketPlaceServer(TEST_PORT);
                server.run();
            });
            serverThread.start();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }

        @Test(timeout = 3000)
        public void testClientLoginPrompt() throws IOException {
            Socket socket = new Socket("localhost", TEST_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
                if (line.toLowerCase().contains("enter option")) break;
            }

            assertTrue(response.toString().contains("Login"));
            assertTrue(response.toString().contains("Create Account"));

            out.println("3");
            socket.close();
        }

        @Test(timeout = 3000)
        public void testClientCreatesAccount() throws IOException {
            Socket socket = new Socket("localhost", TEST_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Wait for login menu
            String line;
            while ((line = in.readLine()) != null && !line.toLowerCase().contains("enter option")) {
                System.out.println("SERVER >> " + line);
            }

            // Create Account
            out.println("2");
            in.readLine(); out.println("testUser123");  // Enter new username
            in.readLine(); out.println("pass123");      // Enter password
            in.readLine(); out.println("150.0");        // Enter balance
            String confirm = in.readLine();
            System.out.println("CREATE RESPONSE >> " + confirm);
            assertNotNull("Missing response after account creation", confirm);
            assertTrue(confirm.toLowerCase().contains("successfully"));

            // Wait for login menu again
            while ((line = in.readLine()) != null && !line.toLowerCase().contains("enter option")) {
                System.out.println("SERVER >> " + line);
            }

            // Delete account
            out.println("4");
            in.readLine(); out.println("testUser123");  // Enter username
            in.readLine(); out.println("pass123");      // Enter password
            String deletionConfirm = in.readLine();
            System.out.println("DELETE RESPONSE >> " + deletionConfirm);
            assertNotNull("Missing response after account deletion", deletionConfirm);
            assertTrue("Expected 'deleted' in response, got: " + deletionConfirm,
                    deletionConfirm.toLowerCase().contains("deleted"));

            out.println("3"); // Exit
            socket.close();
        }

        @Test(timeout = 3000)
        public void testClientInvalidLogin() throws IOException {
            Socket socket = new Socket("localhost", TEST_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (!in.readLine().toLowerCase().contains("enter option")) {}

            out.println("1");
            in.readLine(); out.println("wrongUser");
            in.readLine(); out.println("wrongPass");

            String result = in.readLine();
            assertTrue(result.toLowerCase().contains("invalid"));

            out.println("3");
            socket.close();
        }
    }
}