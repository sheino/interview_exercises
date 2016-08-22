using System;
using System.Collections.Generic;
using System.Linq;

namespace EventManager
{
    class Program
    {
        const int MAX = 10;     //MAX coordinate
        const int MIN = -10;    // Min coordinate
        const int MaxNumberOfEvents = 20;   // Max number of events
        const int MinNumberOfEvents = 5;    // Min number of events
        const int MaxNumberOfDifferentTickets = 5; // Max number of different tickets
        const int MinNumberOfTickets = 0;          // Min number of tickets
        const int MinTicketPrice = 10;  // Min ticket price
        const int MaxTicketPrice = 50;  // Max ticket price
        const bool AllowMultipleEventsAtSameLocation = false; // Flag indicating whether to allow multiple events at the same location
        static int x;   // users x coordinate
        static int y;   // users y coordinate
        static List<Event> eventsList = new List<Event>();  // list containing all events

        static void Main(string[] args)
        {
            // Loops constantly repeating the program
            while(true)
            {
                Console.WriteLine("///////////////////////////////////////////////////////////////////////////////////////////");
                Console.WriteLine("Please enter your coordinates in a form of X,Y in range of {0} - {1}", MIN, MAX);
                while (!getUserInput()) { } // gets user input
                populateRandomEvents();     // populates random events
                printClosestEvents();       // prints closest events
                Console.WriteLine("///////////////////////////////////////////////////////////////////////////////////////////");
            }          
        }

        // Prints closest events
        static void printClosestEvents()
        {
            Console.WriteLine("5 Closest events from {0},{1} are:", x, y);

            // distances list  Tuple.item 1 is event distance Tuple.item2 is event index in eventList
            List<Tuple<int, int>> distances = new List<Tuple<int, int>>();
            
            // Loop calculating event distances 
            for(int index = 0; index < eventsList.Count(); index++)
            {
                // if no tickets are present do not calculate the distance
                if (eventsList[index].tickets.Count() == 0) { continue; }

                int distance = eventsList[index].getDistance(x, y);
                distances.Add(new Tuple<int, int>(distance, index));
            }

            // Sorts distances in assending order
            distances.Sort(Comparer<Tuple<int, int>>.Default);

            // Prints closes events
            for(int i = 0; i < MinNumberOfEvents; i++)
            {
                Event evnt = eventsList[distances[i].Item2];
                double ticket = evnt.getCheapestTicket();
                Console.WriteLine("Event: ID {0}\t Location {3},{4}\t Distance {1}\t CheapestTicket ${2}",
                                  evnt.ID, distances[i].Item1, ticket.ToString("#.##"), evnt.x, evnt.y);
            }
        }

        // gets user coordinates and returns true if it has corret format
        static bool getUserInput()
        {
            string input = Console.ReadLine();
            string[] coordinates = input.Split(',');

            // Checks whether input has correct format
            if (coordinates.Length != 2 || !int.TryParse(coordinates[0], out x) || !int.TryParse(coordinates[1], out y))
            {
                Console.WriteLine("Invalid coordinates entered. Please try again");
                return false;
            }
            //  Checks whether user coordinates are within range
            else if (x < MIN || x > MAX || y < MIN || y > MAX)
            {
                Console.WriteLine("Coordinates must range between {0} and {1}. Please try again", MIN, MAX);
                return false;
            }
              
            return true;
        }

        // creates random events
        static void populateRandomEvents()
        {
            Random random = new Random();
            List<Tuple<int, int>> existingEventCoordinates = new List<Tuple<int, int>>(); // List used for storing already existant event coordinates
            int xCoord;
            int yCoord;

            // Loops creating random number of events
            for (int i = 0; i < random.Next(MinNumberOfEvents, MaxNumberOfEvents + 1); i++)
            {
                xCoord = random.Next(MIN, MAX + 1); // Random coordinate x
                yCoord = random.Next(MIN, MAX + 1); // Random coordinate y

                if(!AllowMultipleEventsAtSameLocation)
                {
                    // if multiple events are not alowed at the same location randomly generate coordinates that are not used
                    while(existingEventCoordinates.Contains(new Tuple<int, int>(xCoord, yCoord)))
                    {
                        xCoord = random.Next(MIN, MAX + 1);
                        yCoord = random.Next(MIN, MAX + 1);
                    }

                    existingEventCoordinates.Add(new Tuple<int, int>(xCoord, yCoord));
                }

                List<double> tickets = new List<double>(); // List of tickets
                  
                // Loops creating random number of tickets            
                for(int j = 0; j < random.Next(MinNumberOfTickets, MaxNumberOfDifferentTickets + 1); j++)
                {
                    double penies = random.NextDouble();
                    tickets.Add(random.Next(MinTicketPrice, MaxTicketPrice) + penies);
                }

                eventsList.Add(new Event(i, xCoord, yCoord, tickets));
            }
        }
    }
}
