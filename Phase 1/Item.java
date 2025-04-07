/**
 * Item Class
 *
 * Purdue University -- CS18000 -- Spring 2025 -- Team Project01
 *
 * @author Reyansh Sawant
 * @version April 6th, 2025
 */
public class Item implements ItemInterface {
    private int itemId;
    private String name;
    private String description;
    private double price;
    private String picturePath;
    private String sellerUsername; 
//    private String picturePath; // maybe implement this for extra credit

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.picturePath = null;
    }

    @Override
    public void createItem(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public void deleteItem(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public String searchItem(String keyword) {
        // Logic to search item
        return null;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public double getPrice() {
        return price;
    }
    public String getPicturePath() {
        return picturePath;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
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