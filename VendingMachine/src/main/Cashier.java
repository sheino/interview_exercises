package main;

import java.math.BigDecimal;
import java.util.*;

// Class representing the cashier in the machine which handles all the coins.
public class Cashier
{
    private Map<String, Coin> coins = new HashMap<>(); // Dictionary of coins inside the cashier
    private Map<String, Coin> pendingCoins = new HashMap<>(); // Dictionary of pending coins inserted by user

    // Dictionary of coins for change after user bought an item used to reset coins in case correct change cant be given
    private Map<String, Coin> changeCoins = new HashMap<>();

    // all available coin denominations
    private ArrayList<String> acceptedCoins = new ArrayList<>(Arrays.asList("2.00", "1.00", "0.50", "0.20",
            "0.10", "0.05", "0.02", "0.01"));

    // Constructor for cashier
    public Cashier()
    {
        // Initialises all coin dictionaries based on all available denominations
        for (String acceptedCoin : acceptedCoins)
        {
            this.coins.put(acceptedCoin, new Coin(Double.parseDouble(acceptedCoin)));
            this.pendingCoins.put(acceptedCoin, new Coin(Double.parseDouble(acceptedCoin)));
            this.changeCoins.put(acceptedCoin, new Coin(Double.parseDouble(acceptedCoin)));
        }
    }

    // Stocks coin. Used by Stock controller
    // Param - String value - coin value
    // Param - int amount - amount of stock
    public void stockCoin(String value, int amount){this.coins.get(value).stock = amount;}

    // Adds stock to the current coin stock. Used by Stock controller
    // Param - String value - coin value
    // Param - int amount - amount of stock
    public void addCoin(String value, int amount) {this.coins.get(value).stock += amount;}

    // Sets all stock of coin denominations to 0
    public void clearContents()
    {
        for (Map.Entry<String, Coin> entry : this.coins.entrySet())
        {
            entry.getValue().stock = 0;
        }
    }

    // Sets all change coin stock denominations to 0
    private void clearChangeCoins()
    {
        for (Map.Entry<String, Coin> entry : this.changeCoins.entrySet())
        {
            entry.getValue().stock = 0;
        }
    }

    // Reverts changes that were done when trying to give correct change. Called if that is impossible
    private void resetChangeCoins()
    {
        for (Map.Entry<String, Coin> entry : this.changeCoins.entrySet())
        {
            this.coins.get(entry.getKey()).stock -= entry.getValue().stock * entry.getValue().value;
            entry.getValue().stock = 0;
        }
    }

    // Adds user inserted coins into coin stock
    public void takePendingCoins()
    {
        for (Map.Entry<String, Coin> entry : this.pendingCoins.entrySet())
        {
            this.coins.get(entry.getKey()).stock += entry.getValue().stock;
            entry.getValue().stock = 0; // sets pending coin stock to 0
        }
    }

    // Attempts to give user the correct change in coins
    // Param - BigDecimal change - change that need to be given
    // Returns - ArrayList<String> - list of coin denominations that is given to the user
    public ArrayList<String> getChange(BigDecimal change)
    {
        ArrayList<String> changeList = new ArrayList<>();

        // Loops through all accepted coin denominations. Starting with biggest coin
        for (int i = 0; i < this.acceptedCoins.size(); i++)
        {
            String coinName = this.acceptedCoins.get(i);

            // Using Big decimal for more precise float arithmetic operations
            BigDecimal coinValue = new BigDecimal(Double.parseDouble(coinName));

            // Loop while remaining change to be give is bigger than coin value
            while (change.compareTo(coinValue) == 1)
            {
                // if coin is not in stock break
                if (!this.coins.get(coinName).isInStock())
                {
                    break;
                }

                change = change.subtract(coinValue); //  subtract coin value from remaining change to be given
                this.coins.get(coinName).stock += -1; // reduce stock of coin
                this.changeCoins.get(coinName).stock += 1; // add coin to  expected change dictionary
                changeList.add(coinName); // add coin to the change list which will be give to user
            }

            // if remaining change is > 0.01 return change list
            if (change.compareTo(new BigDecimal(0.01)) == -1)
            {
                this.clearChangeCoins(); // clears change coin
                return changeList;
            }
        }

        // If impossible to give correct change reset change coins and return null
        this.resetChangeCoins();

        return null;
    }

    // Returns all user inserted coins
    // Returns - ArrayList<String> - list of user inserted coins
    public ArrayList<String> getRefund()
    {
        ArrayList<String> refundedCoins = new ArrayList<>();
        for (Map.Entry<String, Coin> entry : this.pendingCoins.entrySet())
        {
            for (int i = 0; i < entry.getValue().stock; i++)
            {
                refundedCoins.add(entry.getKey());
            }

            entry.getValue().stock = 0; // sets pending coin stock to 0
        }

        return refundedCoins;
    }

    // Adds inserted coin to pending Coin dictionary
    // Param - Sting coin - coin value
    // Returns - Boolean - true if correct coin has been inserted false otherwise
    public boolean addPendingCoin(String coin)
    {
        // if coin is not accepted return false
        if (!acceptedCoins.contains(coin))
        {
            return false;
        }

        this.pendingCoins.get(coin).stock += 1;
        return true;
    }

    // Gets sum of all coins currently in the machine
    // Returns - double - sum of all coins
    public double getContentsSum()
    {
        double totalCoinSum = 0;
        for (Map.Entry<String, Coin> entry : this.coins.entrySet())
        {
            totalCoinSum +=  getCoinSum(entry.getKey());
        }

        return totalCoinSum;
    }

    // Gets total sum of coin denomination in stock
    // Param - String coin - coin value
    // Returns - double - coin sum
    public double getCoinSum(String coin) {
        return this.coins.get(coin).stock * this.coins.get(coin).value;
    }

    // Gets sum of user inserted coins
    // Returns - double - total sum of inserted coins
    public double getPendingCoinsSum()
    {
        double totalPendingCoinSum = 0;
        for (Map.Entry<String, Coin> entry : this.pendingCoins.entrySet())
        {
            totalPendingCoinSum += entry.getValue().stock * entry.getValue().value;
        }

        return totalPendingCoinSum;
    }

    // Returns copy of coin stock. So that it would not be possible to manipulate contents outside of cashier
    // Returns - Map<String, Coin> - coin stock dictionary
    public Map<String, Coin> getContents()
    {
        Map<String, Coin> copy = new HashMap<>();
        copy.putAll(this.coins);
        return copy;
    }
}
