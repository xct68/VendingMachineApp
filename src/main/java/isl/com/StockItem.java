package isl.com;

public class StockItem {
    private String name;
    private int quantity;
    private double price;

    public StockItem(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return name + " - Qty: " + quantity + " - $" + price;
    }
}
