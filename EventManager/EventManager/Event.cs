using System;
using System.Collections.Generic;
using System.Linq;

namespace EventManager
{
    // Class representing an event
    public class Event
    {
        public int ID;  // event ID
        public int x;   // event x coordinate
        public int y;   // event y coordinate
        public List<double> tickets; // list of ticket prices

        // Event constructor
        public Event(int ID, int x, int y, List<double> tickets)
        {
            this.ID = ID;
            this.x = x;
            this.y = y;
            this.tickets = tickets;
        }

        // Gets Manhattan distance based on given coordinates
        public int getDistance(int x, int y)
        {
            return Math.Abs(this.x - x) + Math.Abs(this.y - y);
        }

        // Gets cheapest available ticket
        public double getCheapestTicket()
        {
            // If no tickets are present return -1
            if (tickets.Count() == 0) { return -1; }

            return tickets.Min();
        }

    }
}
