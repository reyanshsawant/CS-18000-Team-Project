import java.util.ArrayList;

public class Item {
  private double price;
  private String name;
  private String description;
  private int id;

  public Item(double price, String name, String description, int id) {
    this.price = price;
    this.name = name;
    this.description = description;
    this.id = id;
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

  public int getID() {
    return id;
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
  
  public void setID(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Item Name: " + name + " Price: " + price + " Description " + description;
  }
}
