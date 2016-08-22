package main;

        import java.lang.String;
        import java.util.Scanner;

public class Main
{
    private static final String machineName = "AWESOM-O 2000"; // Vending machine title

    // Default paths for stock or add stock
    private static final String defaultStockFilePath = "stock.xml";
    private static final String defaultAddStockFilePath = "addStock.xml";

    private static Machine machine = new Machine(); //instance of the wending machine
    private static Scanner input = new Scanner(System.in); // user input scanner

    public static void main(String[] args)
    {
        System.out.println(machineName);

        // Check current stock
        machine.restock(defaultStockFilePath);
        machine.printAllItems();
        printHelp();

        // Loops waiting for user input and executes commands
        while(true)
        {
            System.out.println("Please enter a command or type help for the list of available commands");
            executeCommand();
        }
    }

    // Executes user commands
    private static void executeCommand()
    {
        String command = input.next();
        switch(command)
        {
            case "view":
                machine.printAllItems();
                break;
            case "help":
                printHelp();
                break;
            case "select":
                selectItem();
                break;
            case "status":
                machine.printStatus();
                break;
            case "restock":
                machine.restock(defaultStockFilePath);
                System.out.println("Restock completed");
                machine.printStatus();
                break;
            case "addstock":
                machine.addStock(defaultAddStockFilePath);
                System.out.println("Stock updated");
                machine.printStatus();
                break;
            case "exit":
                machine.saveStock(defaultStockFilePath);
                System.out.println("Exiting");
                System.exit(0);
            default:
                System.out.println("Invalid command entered");
                break;
        }
    }

    // Handles item selection and buying
    private static void selectItem()
    {
        machine.printAllItems();
        System.out.println("Enter ID of the chosen item");

        int id;

        // gets item ID
        try
        {
            id = input.nextInt();
        }
        catch(Exception e)
        {
            // Prints error if invalid id (not int) has been entered.
            System.out.println("Error: Invalid ID");
            input.next();
            return;
        }

        // Selects item or exits if item is out of stock or not found
        if(!machine.selectItem(id)){return;}

        System.out.println("Please enter the correct amount of coins or type refund.\n" +
                "Only 1p 2p 5p 10p 20p 50p 1£ 2£ coin format is accepted");

        // Loops until correct coins amount have been entered or refund has been entered
        while(true)
        {
            String coin = input.next();

            // Refunds money if refund command has been entered
            if(coin.equals("refund"))
            {
                machine.refund();
                return;
            }

            machine.insertCoin(coin); // Inserts coin

            // Checks whether correct amount has been entered
            if(machine.checkRemainingAmount())
            {
                // If impossible to give correct change refunds money and returns
                if(!machine.getChange())
                {
                    System.out.println("Unable to provide correct change." +
                            "Please try again and insert exact amount of coins.");
                    machine.refund();
                    return;
                }

                // Buys item
                machine.buyItem(id);
                return;
            }
        }
    }

    // Prints help
    private static void printHelp() {
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////");
        System.out.println("List of available commands:");
        System.out.println("help         - prints the list of available commands");
        System.out.println("view         - prints all items in this " + machineName);
        System.out.println("select       - selects an item you want to buy");
        System.out.println("status       - prints all items and cashier contents");
        System.out.println("restock      - restock the current coins and items and update prices");
        System.out.println("addstock      - add stock to the current coins and items and update prices");
        System.out.println("exit         - power off the machine and save current coins and items");
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////");
    }

}