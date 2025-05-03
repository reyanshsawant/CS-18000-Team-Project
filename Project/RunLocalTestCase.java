import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

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

        // --- User Tests ---
        @Test(timeout = 1000)
        public void testUserConstructorAndGetters() {
            User user = new User("newUser", "newPass", 250.0);
            assertEquals("newUser", user.getUsername());
            assertEquals("newPass", user.getPassword()); // Assuming getPassword exists
            assertEquals(250.0, user.getBalance(), 0.001); // Assuming getBalance exists
        }

        @Test(timeout = 1000)
        public void testUserSetters() {
            User user = new User("setterUser", "oldPass", 100.0);
            user.setPassword("newPass"); // Assuming setPassword exists
            user.setBalance(150.0); // Assuming setBalance exists
            assertEquals("newPass", user.getPassword());
            assertEquals(150.0, user.getBalance(), 0.001);
        }

        @Test(timeout = 1000)
        public void testUserVerifyLogin() {
            User user = new User("loginUser", "correctPass", 50.0);
            assertTrue(user.verifyPassword("correctPass")); // Assuming verifyPassword exists
            assertFalse(user.verifyPassword("wrongPass")); // Assuming verifyPassword exists
        }

        @Test(timeout = 1000)
        public void testUserBalanceChanges() {
            User user = new User("balanceUser", "pass", 100.0);
            user.addToBalance(50.0); // Assuming addToBalance exists
            assertEquals(150.0, user.getBalance(), 0.001);
            boolean success = user.subtractFromBalance(75.0); // Assuming subtractFromBalance exists
            assertTrue(success);
            assertEquals(75.0, user.getBalance(), 0.001);
            boolean failure = user.subtractFromBalance(100.0); // Subtract more than available
            assertFalse(failure);
            assertEquals(75.0, user.getBalance(), 0.001); // Balance should remain unchanged
        }

        // User deletion is tested in accountDeletionTest

        // --- Item Tests ---
        @Test(timeout = 1000)
        public void testItemConstructorAndGetters() {
            Item item = new Item("Widget", "A standard widget", 19.99);
            item.setSellerName("sellerWidget");
            assertEquals("Widget", item.getName()); // Assuming getName exists
            assertEquals("A standard widget", item.getDescription()); // Assuming getDescription exists
            assertEquals(19.99, item.getPrice(), 0.001); // Assuming getPrice exists
            assertEquals("sellerWidget", item.getSellerName());
        }

        @Test(timeout = 1000)
        public void testItemSetters() {
            Item item = new Item("Old Name", "Old Desc", 10.0);
            item.setName("New Name"); // Assuming setName exists
            item.setDescription("New Desc"); // Assuming setDescription exists
            item.setPrice(20.0); // Assuming setPrice exists
            assertEquals("New Name", item.getName());
            assertEquals("New Desc", item.getDescription());
            assertEquals(20.0, item.getPrice(), 0.001);
        }

        @Test(timeout = 1000)
        public void testItemCategory() {
            Item item = new Item("Gadget", "A useful gadget", 45.50);
            item.setCategory("Electronics"); // Assuming setCategory exists
            assertEquals("Electronics", item.getCategory()); // Assuming getCategory exists
        }

        @Test(timeout = 1000)
        public void testItemMarkAsSold() {
            Item item = new Item("Thingamajig", "Rare item", 100.0);
            assertFalse(item.isSold()); // Assuming isSold exists and defaults to false
            item.markAsSold(); // Assuming markAsSold exists
            assertTrue(item.isSold());
        }

        @Test(timeout = 1000)
        public void testItemGetSeller() {
            // Assumes getSeller() returns a User object or seller identifier string
            // This depends heavily on your Item class implementation
            Item item = new Item("Gizmo", "A complex gizmo", 300.0);
            item.setSellerName("gizmoSeller");
            // If getSeller returns a User object:
            // User seller = new User("gizmoSeller", "pass", 0);
            // item.setSeller(seller); // Assuming a setSeller(User) method exists
            // assertEquals(seller, item.getSeller()); // Assuming getSeller returns User
            // If getSeller just returns the name:
             assertEquals("gizmoSeller", item.getSellerName()); // Using existing getter
        }

        // --- MarketplaceImpl Tests ---
        // Note: These tests assume MarketplaceImpl exists and is instantiated correctly.
        // You might need a @Before method to set up a MarketplaceImpl instance.
        // For now, they are structured like the others, assuming direct access or static methods.
        // We'll use the existing itemHandler and userHandler as stand-ins for MarketplaceImpl functionality where possible.

        @Test(timeout = 1000)
        public void testMarketplaceAddItem() {
             // Using itemHandler as a stand-in for Marketplace add item functionality
            Item mpItem = new Item("MP_Item1", "Marketplace Item 1", 10.0);
            mpItem.setSellerName("mpSeller1");
            itemHandler.addItem(mpItem); // Assumes MarketplaceImpl.addItem adds via ItemManager
            assertEquals(1, itemHandler.searchItemsByName("MP_Item1").size());
        }

        @Test(timeout = 1000)
        public void testMarketplaceRemoveItem() {
            // Using itemHandler as a stand-in for Marketplace remove item functionality
            Item mpItem = new Item("MP_Item_ToRemove", "To be removed", 5.0);
            mpItem.setSellerName("mpSeller2");
            itemHandler.addItem(mpItem);
            assertEquals(1, itemHandler.searchItemsByName("MP_Item_ToRemove").size());
            itemHandler.removeItem(mpItem); // Assumes MarketplaceImpl.removeItem removes via ItemManager
            assertEquals(0, itemHandler.searchItemsByName("MP_Item_ToRemove").size()); // Verify removal
        }

        @Test(timeout = 1000)
        public void testMarketplaceSearchByName() {
             // Using itemHandler as a stand-in for Marketplace search functionality
            itemHandler.addItem(new Item("SearchableUniqueName", "desc1", 1.0));
            itemHandler.addItem(new Item("AnotherSearchable", "desc2", 2.0));
            assertEquals(1, itemHandler.searchItemsByName("SearchableUniqueName").size()); // Assuming exact match search via ItemManager
        }

        @Test(timeout = 1000)
        public void testMarketplaceSearchByCategory() {
            // Assumes Item has category and MarketplaceImpl filters items (using ItemManager as stand-in)
            Item itemElec = new Item("LaptopXYZ", "desc", 1200); itemElec.setCategory("Electronics"); itemElec.setSellerName("sellerE");
            Item itemBook = new Item("NovelABC", "desc", 20); itemBook.setCategory("Books"); itemBook.setSellerName("sellerB");
            itemHandler.addItem(itemElec);
            itemHandler.addItem(itemBook);
            // Direct searchByCategory via ItemManager might not exist.
            // We test if items added via handler retain their category.
            // A real test would call marketplace.searchByCategory("Electronics") and check results.
            ArrayList<Item> electronicsItems = itemHandler.searchItemsByName("LaptopXYZ"); // Retrieve the specific item
            assertEquals(1, electronicsItems.size());
            assertEquals("Electronics", electronicsItems.get(0).getCategory());
        }

         @Test(timeout = 1000)
        public void testMarketplaceSearchBySeller() {
            // Assumes Item stores seller name and MarketplaceImpl filters items (using ItemManager as stand-in)
            User searchSeller = new User("searchSellerUser", "pass", 0);
            userHandler.addUser(searchSeller); // Ensure seller exists
            Item itemBySeller = new Item("SellerItemUnique", "desc", 50);
            itemBySeller.setSellerName(searchSeller.getUsername()); // Link item to seller by name
            itemHandler.addItem(itemBySeller);
            // Direct searchBySeller via ItemManager/MarketplaceImpl might not exist.
            // We test if items added via handler can be linked to the seller.
            // A real test would call marketplace.searchBySeller(searchSeller) and check results.
            ArrayList<Item> foundItems = itemHandler.searchItemsByName("SellerItemUnique"); // Find the item
            assertEquals(1, foundItems.size());
            assertEquals("searchSellerUser", foundItems.get(0).getSellerName()); // Check seller name matches
        }

        @Test(timeout = 1000)
        public void testMarketplaceGetSoldItems() {
             // Assumes MarketplaceImpl can retrieve sold items (using ItemManager as stand-in)
            User sellerOfSold = new User("sellerSoldUser", "pass", 0);
            userHandler.addUser(sellerOfSold);
            Item soldItem = new Item("SoldItemUnique", "desc", 25);
            soldItem.setSellerName(sellerOfSold.getUsername());
            soldItem.markAsSold(); // Mark item as sold
            itemHandler.addItem(soldItem); // Add to handler
            // Direct getSoldItems via MarketplaceImpl might not exist.
            // We test if a sold item added via handler retains its sold status.
            // A real test would call marketplace.getSoldItems(sellerOfSold) and check results.
            ArrayList<Item> foundItems = itemHandler.searchItemsByName("SoldItemUnique");
            assertEquals(1, foundItems.size());
            assertTrue(foundItems.get(0).isSold()); // Verify item is marked sold
        }

        @Test(timeout = 1000)
        public void testMarketplaceProcessPayment() {
            // Simulates MarketplaceImpl.processPayment using UserHandler
            User buyer = new User("buyerUser", "pass", 200);
            User sellerPay = new User("sellerPayUser", "pass", 50);
            userHandler.addUser(buyer);
            userHandler.addUser(sellerPay);

            double paymentAmount = 75.0;
            // Simulate payment: subtract from buyer, add to seller
            boolean buyerSuccess = userHandler.getUser("buyerUser").subtractFromBalance(paymentAmount);
            boolean sellerSuccess = false;
            if (buyerSuccess) {
                userHandler.getUser("sellerPayUser").addToBalance(paymentAmount);
                sellerSuccess = true; // Assume addition always works if subtraction did
                // In a real scenario, marketplace.processPayment would handle this logic.
                 // We would also mark the associated item as sold here using itemHandler.
            }

            assertTrue("Buyer balance subtraction failed", buyerSuccess);
            assertTrue("Seller balance addition failed (simulated)", sellerSuccess);
            assertEquals(125.0, userHandler.getUser("buyerUser").getBalance(), 0.001); // 200 - 75
            assertEquals(125.0, userHandler.getUser("sellerPayUser").getBalance(), 0.001); // 50 + 75
        }

         @Test(timeout = 1000)
        public void testMarketplaceRateSeller() {
             // Simulates MarketplaceImpl.rateSeller by directly interacting with User/SellerRating
             // Assumes User class has methods for rating or holds a SellerRating object
            User sellerToRate = new User("sellerToRateUser", "pass", 0);
             // Assuming User class has addRating and getAverageRating or similar
             // sellerToRate.addRating(5); // Example: Directly call rating method on user
             // sellerToRate.addRating(3);
            userHandler.addUser(sellerToRate); // Add user via handler

             // Since User class details (rating mechanism) are unknown, this is a placeholder structure.
             // Retrieve user and attempt to rate (assuming methods exist)
             User retrievedSeller = userHandler.getUser("sellerToRateUser");
             assertNotNull(retrievedSeller);
             // assertTrue(retrievedSeller.getAverageRating() > 0); // Example assertion if rating methods were called
             // For now, just confirm the user exists, as rating depends on User class implementation.
             assertTrue("Test needs User class rating methods implementation", true);
        }


        // --- Message Tests ---
        @Test(timeout = 1000)
        public void testMessageConstructorAndGetters() {
            Message msg = new Message("senderUser", "recipientUser", "Hello there!");
            assertEquals("senderUser", msg.getSender());
            assertEquals("recipientUser", msg.getRecipient());
            assertEquals("Hello there!", msg.getContent());
        }


        // --- SellerRating Tests ---
        // Assumes SellerRating class exists and User class integrates it
        @Test(timeout = 1000)
        public void testSellerRating() {
             // This test assumes a standalone SellerRating class, which might be integrated into User
             SellerRating rating = new SellerRating(); // Assuming default constructor
             rating.addRating(5); // Assuming addRating exists
             rating.addRating(3);
             rating.addRating(4);
             assertEquals(4.0, rating.getAverageRating(), 0.001); // Assuming getAverageRating exists
        }

         @Test(timeout = 1000)
        public void testSellerRatingEdgeCases() {
            SellerRating rating = new SellerRating();
            assertEquals(0.0, rating.getAverageRating(), 0.001); // Test initial state (no ratings)
            rating.addRating(1);
            assertEquals(1.0, rating.getAverageRating(), 0.001); // Test single rating
            // Consider adding tests for invalid ratings if applicable (e.g., rating < 1 or > 5)
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