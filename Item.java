public class Item {
  private double price;
  private String sellerUsername;
  private String name;
  private String description;
  private int id;

  public Item(double price, String sellerUsername, String name, String description, int id) {
    this.price = price;
    this.sellerUsername = sellerUsername;
    this.name = name;
    this.description = description;
    this.id = id;
  }

  public double getPrice() {
    return price;
  }

  public String getSellerUsername() {
    return sellerUsername;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getID() {
    return id;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setSellerUsername(String sellerUsername) {
    this.sellerUsername = sellerUsername;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public void setID(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Item Name: " + name + ", ID: " + id + ", Price: " + price + ", Description: " + description + ", Seller: " + sellerUsername;
  }
}
