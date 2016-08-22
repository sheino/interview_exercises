using System;
using System.Collections.Generic;
using System.Linq;

namespace BattleShips
{

    // Class representing a ship
    public class Ship
    {
        private Coordinate coordinates; // current ship coordinates
        private Coordinate startCoordinates; // start coordinates used for references
        private int dirIndex; // index to a  current direction in possibleDir arraya
        private int dimensions; // dimensions
        private bool alive = true; // boolean flag indicating whether ship was shot
        private char[] possibleDir = { 'N', 'W', 'S', 'E' }; // possible directions

        // ship constructor
        //*param*   int x   x coordinate
        //*param*   int y   y coordinate
        //*param*   char dir    direction
        //*param*   int dim     dimension of a battle field
        //*throws*  ArgumentException when passed incorrect direction
        public Ship(int x, int y, char dir, int dim)
        {
            dir = char.ToUpper(dir); // convert direction to upper case

            if (!possibleDir.Contains(dir)) // checks whether correct dir has been passed
            {
                throw new ArgumentException(); // throws Argument exception if not
            }

            coordinates = new Coordinate(x, y);
            startCoordinates = new Coordinate(x, y);

            dirIndex = Array.IndexOf(possibleDir, dir); // gets the index to possibleDir array
            dimensions = dim;
        }
               
        // Rotates ship right
        public void RotateRight()
        {
            // just decrements the index
            if (dirIndex == 0)
            {
                dirIndex = 3;
            }
            else
            {
                dirIndex -= 1;
            }
        }

        // Rotates ship left
        public void RotateLeft()
        {
            // just increments direction index
            if (dirIndex == 3)
            {
                dirIndex = 0;
            }
            else
            {
                dirIndex += 1;
            }
        }

        // Gets ship status as a string
        // *return* string  ship status
        public string GetStatusAsString()
        {
            string strStatus = string.Format("({0}, {1}, {2})", coordinates.x, coordinates.y, possibleDir[dirIndex]);

            // Appends SUNK to the end of a string if ship has been hit
            if (!alive)
            {
                strStatus += " SUNK";
            }

            return strStatus;
        }

        // Checks whether ship was hit by a shot
        // *return* bool    true if ship was shot
        public bool IsHit(int x, int y)
        {
            if (coordinates.x == x && coordinates.y == y)
            {
                alive = false; // sets flag that ship was sunk
                return true;
            }

            return false;
        }

        // Checks whether ship is still operational
        // *return* bool    true if ship is still alive
        public bool IsAlive()
        {
            return alive;
        }

        // Gets current ship coordinates
        // *return* Coordinate  ship coordinates
        public Coordinate GetCoordinates()
        {
            return coordinates;
        }

        // Gets start coodinates
        // used when comman refers to the ship  that has already moved
        // in order to avoid manually calculating ship coordinates when adding more operations to input file
        // *return* Coordinates start ship coordinates
        public Coordinate GetStartCoordinates()
        {
            return startCoordinates;
        }


        // Checks whether move is available
        // *param* Coordinate coord    coordinates to check
        // *param* List<Ship> ships    ship coordinates to check to avoid collision
        // *return* bool    true if move is available
        private bool IsMoveAvailable(Coordinate coord, List<Ship> ships)
        {
            // Checks whether coordinates are withing battle field size
            if (coord.x > dimensions || coord.x < 0 ||
                coord.y > dimensions || coord.y < 0)
            {
                return false;
            }

            // Loops through all the ships in the battlefield
            if (ships.Count() > 0)
            {
                foreach (Ship ship in ships)
                {   
                    // Checks if ship is allive and compares its coordinates to givent coordinates                   
                    if (ship.IsAlive() && ship.GetCoordinates().x == coord.x && ship.GetCoordinates().y == coord.y)
                    {
                        return false;
                    }
                }
            }

            return true;
        }


        // Attempts to move ship forward
        // *param*  List<Ship>  list of ships to be aware of in order to avoid collision
        // *return* bool    true if move was successfull
        public bool Move(List<Ship> ships)
        {
            // Temporary coordinates to avoid making an invalid move
            Coordinate tempCoordinates = new Coordinate(coordinates.x, coordinates.y);

            // Based on direction updates coordinates
            switch (possibleDir[dirIndex])
            {
                case 'N':
                    tempCoordinates.y += 1;
                    break;
                case 'W':
                    tempCoordinates.x -= 1;
                    break;
                case 'S':
                    tempCoordinates.y -= 1;
                    break;
                case 'E':
                    tempCoordinates.x += 1;
                    break;
            }

            // Checks if move is available and no ships are in the way
            if(IsMoveAvailable(tempCoordinates, ships))
            {
                // update the actual coordinates
                coordinates.x = tempCoordinates.x;
                coordinates.y = tempCoordinates.y;

                return true;
            }

            return false;
        }
    }
}
