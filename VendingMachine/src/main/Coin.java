package main;

// Class representing coin
public class Coin
{
    public double value; // coin value
    public int stock = 0; // amount of coins in stock

    // Coin constructor
    // param double value - value of the coin
    public Coin(double value)
    {
        this.value = value;
    }

    // Checks whether coin is in stock
    // return - boolean - true if there is at least one coin in stock
    public boolean isInStock(){return this.stock > 0;}
}
