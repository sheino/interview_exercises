package main;

import java.math.BigDecimal;
import java.util.*;
import java.lang.String;

// Class representing physical wending machine
public class Machine
{
    private ArrayList<Item> items = new ArrayList<>(); // All items in stock
    private Cashier cashier = new Cashier(); // Represents cashier and does all money handling operations
    private StockController stockController = new StockController(items, cashier); // Controls 'physical' stock.

    private Item pendingItem; // item that user selects for buying

    // empty constructor
    public Machine(){}

    // Prints all items currently in stock
    public void printAllItems()
    {
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////");
        System.out.println("Current items:");

        // Loops and prints all items in stock
        for(int i = 0; i < items.size(); i++)
        {
            Item item = items.get(i);
            System.out.printf("ID: %d\t%s\tprice: %.2f£\tcurrently in stock: %d units\n", i, item.name,
                                                                                            item.price,
                                                                                            item.stock);
        }
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////");
    }

    // Prints all items and coins in stock
    public void printStatus()
    {
        this.printAllItems(); // prints current items in stock
        System.out.println("Cashier contents:");

        Map<String, Coin> cashierContents = this.cashier.getContents(); // gets copy if cashier contents
        SortedSet<String> keys = new TreeSet<>(cashierContents.keySet()); // sorts keys

        // Loops and prints all coin denominations and their stock
        for(String key : keys)
        {
            System.out.printf("Coin: %s stock: %d\ttotal sum: %.2f£\n", key, cashierContents.get(key).stock,
                    cashier.getCoinSum(key));
        }

        System.out.printf("Total sum: %.2f£\n", cashier.getContentsSum());
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////");
    }

    // Buys item
    // param - int id - id of an item that user is buying
    public void buyItem(int id)
    {
        this.items.get(id).stock += -1; // reduces the stock of the item
        this.cashier.takePendingCoins(); // Puts inserted coins into cashier coin stock
        System.out.println("Please take your " + this.items.get(id).name + " and your change");
    }

    // refunds inserted coins
    public void refund()
    {
        ArrayList<String> refundedCoins = this.cashier.getRefund(); // gets list of coins that has been inserted

        if(refundedCoins.size() == 0){return;} // if no coins have been entered return

        System.out.println("Coins refunded:");

        // prints list of entered coins
        for(String coin : refundedCoins)
        {
            System.out.print(coin + ' ');
        }

        System.out.println();
    }

    // Inserts coin into cashier
    // param - String coin - coin to be inserted
    public void insertCoin(String coin)
    {
        // if invalid coin has been entered let user know
        if(!cashier.addPendingCoin(coin))
        {
            System.out.println("Invalid coin entered");
        }
    }

    // fresh restock of items and coins
    // param - string filePath - stock location
    public void restock(String filePath){this.stockController.restock(filePath);}

    // adds stock to the current items and coins
    // param - string filePath - location from where you add stock
    public void addStock(String filePath){this.stockController.addStock(filePath);}

    // saves current item and coin stock
    // param - string filePath - location where stock should be saved
    public void saveStock(String filePath){this.stockController.saveStock(filePath);}

    // selects and item that user wants to buy
    // param - int id - ID of a selected item
    // returns boolean - true if item has been found and is in stock
    public boolean selectItem(int id)
    {
        // Checks whether item with given id exists
        if(items.size() > id && id >= 0)
        {
            this.printItem(id);

            // Checks whether item is in stock
            if (!items.get(id).isAvailable())
            {
                System.out.println("Out of stock, please choose different item");
                return false;
            }

            // Selects that item for pending trade
            Item tempItem = this.items.get(id);
            this.pendingItem = new Item(tempItem.name, tempItem.price, tempItem.stock);
            return true;
        }

        System.out.printf("Item with ID - %d not found\n", id);
        return false;
    }


    // Checks cashier whether correct amount of coins has been entered for pending trade item
    // returns boolean - true if enough coins has been entered to buy the item
    public boolean checkRemainingAmount()
    {
        double pendingCoinSum = this.cashier.getPendingCoinsSum(); // gets sum of already inserted coins
        double remainingAmount =  this.pendingItem.price  - pendingCoinSum;

        if(remainingAmount > 0)
        {
            System.out.printf("Total coins inserted %.2f£\t remaining %.2f£\n", pendingCoinSum, remainingAmount);
            return false;
        }

        System.out.printf("Correct amount of coins inserted.\n");
        return true;
    }

    // Gets needed change after buy an item
    // return boolean - true if enough coins are in stock to give correct change
    // return boolean - false if impossible to give correct change
    public boolean getChange()
    {
        // using  BigDecimals here to make subtract operations are more precise
        BigDecimal pendingCoinsSum = new BigDecimal(this.cashier.getPendingCoinsSum()); // gets the inserted coin su,
        BigDecimal change = pendingCoinsSum.subtract(new BigDecimal(this.pendingItem.price)); // gets expected change
        change.setScale(2, BigDecimal.ROUND_DOWN);

        System.out.printf("Your expected change:\t%.2f£\n", change);

        // if no change is needed to give back, return
        if(change.compareTo(new BigDecimal(0)) == 0){return true;}

        ArrayList<String> changeList = cashier.getChange(change); // gets coin denomination list

        // if list is null that means impossible to give correct change.
        if(changeList != null)
        {
            System.out.println("Your coins:");

            for(String coin : changeList)
            {
                System.out.print(coin + " ");
            }

            System.out.println();
            return true;
        }

        return false;
    }

    // prints selected item
    // param - int id - ID of a selected item
    private void printItem(int id)
    {
        Item item = items.get(id);
        System.out.printf("Selected Item - ID: %d\t%s\tprice: %.2f£\tcurrently in stock: %d units\n", id, item.name,
                item.price,
                item.stock);
    }
}
