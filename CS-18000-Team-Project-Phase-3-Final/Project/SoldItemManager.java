import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
/**
 * ItemManager Class
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Nikhil
 * @version April 6th, 2025
 */

public class SoldItemManager {
    private final String soldItemsFile = "sold_items.txt";
    // File Format: itemName,description,price,category,sellerUsername,buyerUsername

    public SoldItemManager() {
        File file = new File(soldItemsFile);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating sold items file.");
        }
    }

    public synchronized void recordSale(Item item, String buyerUsername) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(soldItemsFile, true))) {
            writer.write(item.getName() + "," + item.getDescription() + "," + item.getPrice() + ","
                    + item.getCategory() + "," + item.getSellerName() + "," + buyerUsername);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing sold item.");
        }
    }

    public ArrayList<Item> getSoldItemsBySeller(String sellerUsername) {
        ArrayList<Item> soldItems = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(soldItemsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6 && parts[4].equals(sellerUsername)) {
                    Item item = new Item(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
                    item.setSellerName(parts[4]);
                    soldItems.add(item);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading or parsing sold items file: " + e.getMessage());
        }
        return soldItems;
    }

    public synchronized boolean hasBuyerPurchasedFromSeller(String buyerUsername, String sellerUsername) {
        try (BufferedReader reader = new BufferedReader(new FileReader(soldItemsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6 && parts[4].equals(sellerUsername) && parts[5].equals(buyerUsername)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading sold items file for purchase check: " + e.getMessage());
        }
        return false;
    }

    public void recordSale(Item selectedItem) {
    }
}
