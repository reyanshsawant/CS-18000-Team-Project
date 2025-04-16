import java.io.*;
import java.util.ArrayList;

public class SoldItemManager {
    private final String soldItemsFile = "sold_items.txt";

    public SoldItemManager() {
        File file = new File(soldItemsFile);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating sold items file.");
        }
    }

    public synchronized void recordSale(Item item) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(soldItemsFile, true))) {
            writer.write(item.getName() + "," + item.getDescription() + "," + item.getPrice() + ","
                    + item.getCategory() + "," + item.getSeller());
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
                String[] parts = line.split(",", 5);
                if (parts.length == 5 && parts[4].equals(sellerUsername)) {
                    Item item = new Item(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
                    item.setSeller(parts[4]);
                    soldItems.add(item);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading sold items.");
        }
        return soldItems;
    }
}
