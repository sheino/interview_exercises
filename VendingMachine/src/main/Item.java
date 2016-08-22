package main;

import java.lang.String;

// Cass representing Item
public class Item
{
    public String name; // Item name
    public int stock; // Amount of items in stock
    public double price; // Price of an item

    // Item constructor
    // Param - String name - name of the item
    // Param - double price - price of the item
    // Param - int stock - amount of items in stock
    public Item(String name, double price, int stock)
    {
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    // Checks whether item is in stock
    // return - boolean - true if there is at least one item in stock
    public boolean isAvailable()
    {
        return this.stock > 0;
    }
}
