/**
 * Item Class
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Reyansh Sawant
 * @version April 6th, 2025
 */
public class Item implements ItemInterface {
    private int itemIDnum;
    private String itemName;
    private String itemDescription;
    private double itemPrice;
    private String itemPicturePath;
    private String sellerUsername;
    private String category;
//    private String picturePath; // maybe implement this for extra credit

    public Item(String name, String description, double price) {
        this.itemName = name;
        this.itemDescription = description;
        this.itemPrice = price;
        this.itemPicturePath = null;
    }
    public Item(String name, String description, double price, String category) {
        this.itemName = name;
        this.itemDescription = description;
        this.itemPrice = price;
        this.category = category;
        this.itemPicturePath = null;
    }


    @Override
    public void createItem(String name, String description, double price) {
        this.itemName = name;
        this.itemDescription = description;
        this.itemPrice = price;
    }

    @Override
    public void deleteItem(int itemId) {
        this.itemIDnum = itemId;
    }

    @Override
    public String searchItem(String keyword) {
        // Logic to search item
        return null;
    }

    public void setItemId(int itemId) {
        this.itemIDnum = itemId;
    }
    public int getItemId() {
        return itemIDnum;
    }

    public String getName() {
        return itemName;
    }
    public String getDescription() {
        return itemDescription;
    }
    public double getPrice() {
        return itemPrice;
    }
    public String getPicturePath() {
        return itemPicturePath;
    }

    public void setName(String name) {
        this.itemName = name;
    }
    public void setDescription(String description) {
        this.itemDescription = description;
    }
    public void setPrice(double price) {
        this.itemPrice = price;
    }
    public void setPicturePath(String picturePath) {
        this.itemPicturePath = picturePath;
    }
    public String getSeller() {
        return sellerUsername;
    }
    public String getSellerName() {
        return sellerUsername;
    }
    public void setSellerName(String seller) {
        this.sellerUsername = seller;
    }

    public void setSeller(String seller) {
        this.sellerUsername = seller;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



    // // Method to upload a picture
    // public boolean uploadPicture(String filePath) {
    //     File file = new File(filePath);
    //     if (file.exists() && !file.isDirectory()) {
    //         this.picturePath = filePath;
    //         return true; // Picture uploaded successfully
    //     }
    //     return false; // File not found or invalid
    // }

    // // Method to display the picture (just file path for now
    // public String displayPicture() {
    //     if (picturePath != null) {
    //         return "Picture Path: " + picturePath;
    //     }
    //     return "No picture uploaded for this item.";
    // }
}