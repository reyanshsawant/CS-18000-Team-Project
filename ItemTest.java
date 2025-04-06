import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ItemTest {
    private Item item;

    @Before
    public void setUp() {
        item = new Item(19.99, "seller1", "Laptop", "High performance laptop", 101);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals(19.99, item.getPrice(), 0.001);
        assertEquals("seller1", item.getSellerUsername());
        assertEquals("Laptop", item.getName());
        assertEquals("High performance laptop", item.getDescription());
        assertEquals(101, item.getID());
    }

    @Test
    public void testSetters() {
        item.setPrice(15.99);
        assertEquals(15.99, item.getPrice(), 0.001);

        item.setSellerUsername("newSeller");
        assertEquals("newSeller", item.getSellerUsername());

        item.setName("Tablet");
        assertEquals("Tablet", item.getName());

        item.setDescription("Portable device");
        assertEquals("Portable device", item.getDescription());

        item.setID(202);
        assertEquals(202, item.getID());
    }

    @Test
    public void testToString() {
        String expected = "Item Name: Laptop, ID: 101, Price: 19.99, Description: High performance laptop, Seller: seller1";
        assertEquals(expected, item.toString());
    }

    @Test
    public void testZeroPrice() {
        item.setPrice(0.0);
        assertEquals(0.0, item.getPrice(), 0.001);
    }

    @Test
    public void testNegativePrice() {
        item.setPrice(-5.0);
        assertEquals(-5.0, item.getPrice(), 0.001);
    }

    @Test
    public void testEmptyStrings() {
        item.setName("");
        assertEquals("", item.getName());

        item.setDescription("");
        assertEquals("", item.getDescription());

        item.setSellerUsername("");
        assertEquals("", item.getSellerUsername());
    }

    @Test
    public void testNullStrings() {
        item.setName(null);
        assertNull(item.getName());

        item.setDescription(null);
        assertNull(item.getDescription());

        item.setSellerUsername(null);
        assertNull(item.getSellerUsername());
    }
}
