import java.util.ArrayList;

public class ItemManager {
    private ArrayList<Item> items;

    public ItemManager() {
        items = new ArrayList<Item>();
    }

    public void addItem(Item newItem) {
        items.add(newItem);
    }

    public void removeItem(int id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getID() == id) {
                items.remove(i);
                break;
            }
        }
    }
  
    public ArrayList<Item> searchByName(String name) {
        ArrayList<Item> results = new ArrayList<Item>();
        for (Item item : items) {
            if (item.getName().equals(name)) {
                results.add(item);
            }
        }
        return results;
    }

    public ArrayList<Item> searchBySeller(String seller) {
        ArrayList<Item> results = new ArrayList<Item>();
        for (Item item : items) {
            if (item.getSellerUsername().equals(seller)) {
                results.add(item);
            }
        }
        return results;
    }

    public ArrayList<Item> getAllItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }
}
