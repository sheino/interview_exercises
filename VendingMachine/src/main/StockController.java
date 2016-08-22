package main;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

// Controls physical stock (reads and updates stock files)
public class StockController
{
    private ArrayList<Item> items; // items in stock
    private Cashier cashier; // cashier instance that handles money

    // Constructor
    // param - ArrayList<Item> items - stock items from machine
    // param - Cashier cashier - cashier instance from machine
    public StockController(ArrayList<Item> items, Cashier cashier)
    {
        this.items = items;
        this.cashier = cashier;
    }

    // Writes current coin and item stock into xml file. Called when exiting machine
    // param - string filePath - stock file location
    public void saveStock(String filePath)
    {
        try {
            // initialises doc builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Root element
            Element rootElement = doc.createElement("wendingMachineStock");
            doc.appendChild(rootElement);

            // Items element
            Element itemsElement = doc.createElement("items");
            rootElement.appendChild(itemsElement);

            // Coins element
            Element coinsElement = doc.createElement("coins");
            rootElement.appendChild(coinsElement);

            // Add all item elements
            for(Item item : this.items)
            {
                Element itemElement = doc.createElement("item");

                itemElement.setAttribute("name", item.name);
                itemElement.setAttribute("price", Double.toString(item.price));
                itemElement.setAttribute("stock", Integer.toString(item.stock));

                itemsElement.appendChild(itemElement);
            }

            Map<String, Coin> cashierContents = cashier.getContents();
            SortedSet<String> keys = new TreeSet<>(cashierContents.keySet()); // Gets sorted key set to write coin elements incrementally

            // Add all coin elements
            for (String key : keys)
            {
                Element coinElement = doc.createElement("coin");

                coinElement.setAttribute("value", key);
                coinElement.setAttribute("stock", Integer.toString(cashierContents.get(key).stock));

                coinsElement.appendChild(coinElement);
            }

            // write the contents into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            System.out.println("Stock saved");
        }
        catch(Exception e)
        {
            System.out.println("Error saving a stock. Please check stock.xml");
        }
    }

    // Add stock to the current coin and item stock from xml file
    // param - string filePath - add stock file location
    public void addStock(String filePath)
    {
        try
        {
            // initialises doc reader
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Reads all items from the file
            NodeList items = doc.getElementsByTagName("item");
            for(int i = 0; i < items.getLength(); i++)
            {
                Node itemNode = items.item(i);

                if(itemNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element itemElement = (Element) itemNode;

                    // Checks whether item to be added already exists
                    boolean newItem = true;
                    for(Item item : this.items)
                    {
                        // Updates existing stock and price of the item
                        String name = itemElement.getAttribute("name");
                        if(item.name.equals(name))
                        {
                            newItem = false;
                            item.stock += Integer.parseInt(itemElement.getAttribute("stock"));
                            item.price = Double.parseDouble(itemElement.getAttribute("price"));
                            break;
                        }
                    }

                    // Adds new item if its not currently in stock
                    if(newItem)
                    {
                        this.items.add(new Item(itemElement.getAttribute("name"),
                                           Double.parseDouble(itemElement.getAttribute("price")),
                                           Integer.parseInt(itemElement.getAttribute("stock"))));
                    }
                }
            }

            // Reads all coin values from the file
            NodeList coins = doc.getElementsByTagName("coin");
            for(int i = 0; i < coins.getLength(); i++)
            {
                Node coinNode = coins.item(i);
                if(coinNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    // adds coin stock to already existing stock
                    Element coinElement = (Element) coinNode;
                    this.cashier.addCoin(coinElement.getAttribute("value"),
                            Integer.parseInt(coinElement.getAttribute("stock")));
                }
            }
        }catch (Exception e)
        {
            System.out.println("ERROR: Stock contaminated. Please check addStock.xml");
        }

    }

    // Clears contents of current coin and item stock and adds them new from xml file
    // param - string filePath - stock file location
    public void restock(String filePath)
    {
        //Clears item stock and cashier contents
        this.items.clear();
        this.cashier.clearContents();

        try
        {
            // initialises doc reader
            File xmlFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();


            // Reads all items from the file
            NodeList items = doc.getElementsByTagName("item");
            for(int i = 0; i < items.getLength(); i++)
            {
                Node itemNode = items.item(i);

                if(itemNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    // adds items to item list
                    Element itemElement = (Element) itemNode;
                    this.items.add(new Item(itemElement.getAttribute("name"),
                                            Double.parseDouble(itemElement.getAttribute("price")),
                                            Integer.parseInt(itemElement.getAttribute("stock"))));
                }
            }

            // Reads all coins from the file
            NodeList coins = doc.getElementsByTagName("coin");
            for(int i = 0; i < coins.getLength(); i++)
            {
                Node coinNode = coins.item(i);

                if(coinNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    // stocks coins in cashier
                    Element coinElement = (Element) coinNode;
                    this.cashier.stockCoin(coinElement.getAttribute("value"),
                                           Integer.parseInt(coinElement.getAttribute("stock")));
                }
            }
        }catch (Exception e)
        {
            System.out.println("ERROR: Stock contaminated. Please check stock.xml");
        }
    }
}
