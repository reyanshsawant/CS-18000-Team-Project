public class Item {
  private double price;
  private String name;
  private String description;

  public Item(double price, String name, String description) {
    this.price = price;
    this.name = name;
    this.description = description;
  }

  public double getPrice() {
    return price;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
