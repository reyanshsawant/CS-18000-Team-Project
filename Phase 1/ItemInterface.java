public interface ItemInterface {
    void createItem(String name, String description, double price);
    void deleteItem(int itemId);
    String searchItem(String keyword);
}