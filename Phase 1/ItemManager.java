import java.io.*;
import java.util.ArrayList;
/**
 * ItemManager Class
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Reyansh Sawant
 * @version April 6th, 2025
 */
public class ItemManager {
    private ArrayList<Item> items = new ArrayList<>();
    private int itemIdCounter = 0;
    private final String itemsFile = "items.txt";

    public ItemManager() {
        loadItems();
    }

    //adds items to items arraylist
    public synchronized void addItem(Item item) {
        item.setItemId(++itemIdCounter);
        items.add(item);
        saveItems();
    }

    //delets items from certain seller
    public synchronized void deleteItemsBySeller(String seller) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getSellerName().equals(seller)) {
                items.remove(i);
                i--; // Adjust index after removal
            }
        }
        saveItems();
    }

    //seller deletes specific item
    public synchronized void deleteItemByNameAndSeller(String itemName, String seller) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().equalsIgnoreCase(itemName) && item.getSellerName().equals(seller)) {
                items.remove(i);
                break;
            }
        }
        saveItems();
    }

    //removing item from ID
    public synchronized void removeItem(int itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItemId() == itemId) {
                items.remove(i);
                break;
            }
        }
        saveItems();
    }

    //gets item from id
    public Item getItem(int itemId) {
        for (Item item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    //creates an arraylist of items that match search
    public synchronized ArrayList<Item> searchItemsByName(String name) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(item);
            }
        }
        return result;
    }

    //" " match price range
    public synchronized ArrayList<Item> searchItemsByPriceRange(double minPrice, double maxPrice) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getPrice() >= minPrice && item.getPrice() <= maxPrice) {
                result.add(item);
            }
        }
        return result;
    }

    //load items from the items.txt file
    private void loadItems() {
        items.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(itemsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    Item item = new Item(parts[0], parts[1], Double.parseDouble(parts[2]));
                    item.setPicturePath(parts[3]);
                    item.setSellerName(parts[4]);
                    items.add(item);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Items file not found. Starting with an empty item list.");
        } catch (IOException e) {
            System.out.println("Error reading items file.");
            e.printStackTrace();
        }
    }

    //write items to the items.txt file
    private void saveItems() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(itemsFile))) {
            for (Item item : items) {
                writer.write(item.getName() + "," + item.getDescription() + "," + item.getPrice() 
                    + "," + item.getPicturePath() + "," + item.getSellerName());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to items file.");
            e.printStackTrace();
        }
    }
}