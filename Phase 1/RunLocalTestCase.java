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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * runLocalTestCase.java
 *
 * purdue university -- cs18000 -- spring 2025 -- team project01
 *
 * @author arjun anilkumar
 * @version april 6th, 2025
 */
@RunWith(Enclosed.class)
public class RunLocalTestCase {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);

        if (result.wasSuccessful()) {
            System.out.println("cool - all tests passed");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    /**
     * public test cases for the marketplace app
     *
     * <p>purdue university -- cs18000 -- spring 2025</p>
     */
    public static class TestCase {
        private final PrintStream standardOut = System.out;
        private final InputStream standardIn = System.in;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testInput;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOutput;

        @Before
        public void setUpOutput() {
            testOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOutput));
        }

        @After
        public void resetSystemIO() {
            System.setIn(standardIn);
            System.setOut(standardOut);
        }

        private String getProgramOutput() {
            return testOutput.toString();
        }

        private void provideInput(String inputStr) {
            testInput = new ByteArrayInputStream(inputStr.getBytes());
            System.setIn(testInput);
        }

        @Test(timeout = 1000)
        public void userCreationTest() {
            // setup
            MainDatabase db = new MainDatabase();
            UserManager userHandler = db.getUserManager();

            // add some test users
            userHandler.addUser(new User("testUser1", "pass123", 100.0));
            userHandler.addUser(new User("testUser2", "pass456", 200.0));

            // check if they exist
            assertNotNull("user1 should exist", userHandler.getUser("testUser1"));
            assertNotNull("user2 should exist", userHandler.getUser("testUser2"));

            // just checking output for no reason
            String output = getProgramOutput();
            assertEquals("", output.trim());
        }

        @Test(timeout = 1000)
        public void itemListingTest() {
            // init db
            MainDatabase db = new MainDatabase();
            ItemManager itemHandler = db.getItemManager();

            // add some items
            Item laptop = new Item("MacBook", "apple laptop", 1299.99);
            laptop.setSellerName("seller1");

            Item phone = new Item("iPhone", "latest model", 999.99);
            phone.setSellerName("seller2");

            itemHandler.addItem(laptop);
            itemHandler.addItem(phone);

            // verify
            assertEquals("should find 1 macbook", 1, itemHandler.searchItemsByName("MacBook").size());
            assertEquals("should find 1 iphone", 1, itemHandler.searchItemsByName("iPhone").size());
        }
        @Test(timeout = 1000)
        public void testAddItems() {
            MainDatabase db = new MainDatabase();
            ItemManager itemHandler = db.getItemManager();

            Item item = new Item("TestItem", "description", 50.0);
            item.setSellerName("testSeller");
            itemHandler.addItem(item);

            assertEquals("should find 1 test item", 1, itemHandler.searchItemsByName("TestItem").size());
        }
        @Test(timeout = 1000)
        public void purchaseTest() {
            // setup db
            MainDatabase db = new MainDatabase();
            UserManager userHandler = db.getUserManager();
            ItemManager itemHandler = db.getItemManager();

            // create users and item
            userHandler.addUser(new User("seller", "sell123", 500.0));
            userHandler.addUser(new User("buyer", "buy123", 1000.0));

            Item car = new Item("Tesla", "Model 3", 40000.0);
            car.setSellerName("seller");
            itemHandler.addItem(car);

            // simulate purchase
            User buyer = userHandler.getUser("buyer");
            Item purchasedItem = itemHandler.searchItemsByName("Tesla").get(0);

            userHandler.updateUserBalance(buyer.getUsername(),
                    buyer.getBalance() - purchasedItem.getPrice());

            User seller = userHandler.getUser(purchasedItem.getSellerName());
            userHandler.updateUserBalance(seller.getUsername(),
                    seller.getBalance() + purchasedItem.getPrice());

            itemHandler.deleteItemByNameAndSeller(purchasedItem.getName(),
                    purchasedItem.getSellerName());

            // check balances
            assertEquals("buyer's new balance", 1000.0 - 40000.0, buyer.getBalance(), 0.01);
            assertEquals("seller's new balance", 500.0 + 40000.0, seller.getBalance(), 0.01);
        }

        @Test(timeout = 1000)
        public void messageFunctionTest() {
            // setup
            MainDatabase db = new MainDatabase();
            MessageManager msgManager = db.getMessageManager();

            // send some messages
            msgManager.sendMessage("userA", "userB", "hey");
            msgManager.sendMessage("userB", "userA", "what's up?");

            // check messages
            ArrayList<Message> userAMessages = msgManager.getMessagesForUser("userA");
            assertEquals("userA should have 1 message", 1, userAMessages.size());
            assertEquals("message content check", "what's up?", userAMessages.get(0).getContent());
        }

        @Test(timeout = 1000)
        public void itemSearchTest() {
            MainDatabase db = new MainDatabase();
            ItemManager itemHandler = db.getItemManager();

            // populate some items
            Item item1 = new Item("Chair", "comfy chair", 59.99);
            item1.setSellerName("ikea");

            Item item2 = new Item("Desk", "large desk", 199.99);
            item2.setSellerName("ikea");

            itemHandler.addItem(item1);
            itemHandler.addItem(item2);

            // test searches
            ArrayList<Item> cheapItems = itemHandler.searchItemsByPriceRange(0.0, 100.0);
            assertEquals("should find 1 cheap item", 1, cheapItems.size());

        }

        @Test(timeout = 1000)
        public void accountDeletionTest() {
            MainDatabase db = new MainDatabase();
            UserManager userHandler = db.getUserManager();

            // create then delete user
            userHandler.addUser(new User("tempUser", "tempPass", 0.0));
            userHandler.deleteUser("tempUser");

            // verify
            assertNull("user should be gone", userHandler.getUser("tempUser"));
        }
    }
}