/**
 * ItemInterface
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Reyansh Sawant
 * @version April 6th, 2025
 */
public interface ItemInterface {
    void createItem(String name, String description, double price);
    void deleteItem(int itemId);
    String searchItem(String keyword);

    int getItemId();
    String getName();
    String getDescription();
    double getPrice();
    String getPicturePath();
    String getSellerName();
    String getCategory();

    void setItemId(int itemId);
    void setName(String name);
    void setDescription(String description);
    void setPrice(double price);
    void setPicturePath(String picturePath);
    void setSellerName(String sellerName);
    void setCategory(String category);
}