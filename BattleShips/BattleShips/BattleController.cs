using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Linq;

namespace BattleShips
{
    // Class controlling the Battle field
    public class BattleController
    {

        private List<Ship> ships = new List<Ship>();    // list of ships
        private IEnumerable<string> commands;           // list of battle commands
        private int dimension = -1;                     // dimension. Set to -1 to indicate it has not been set

        // Constructor for battle controller
        // *param* IEnumerable<string> input lines      list of commands to execute
        public BattleController(IEnumerable<string> inputLines)
        {
            commands = inputLines;
        }


        // Starts battle
        public void StartBattle()
        {
            Console.WriteLine("Starting Battle");

            // Loops through all commands trying to execute them
            foreach (string command in commands)
            {
                // if dimension not set try to parse it as its the first line
                if (dimension == -1)
                {
                    // if impossible to parse. Or dimension too small. Exit 
                    if (!int.TryParse(command, out dimension) || dimension < 2)
                    {
                        Console.WriteLine("Error: Invalid battle field size. \nExiting");
                        Environment.Exit(1);
                    }

                    continue;
                }

                ExecuteCommand(command); // executes command
            }
        }

        // gets battle/ship status as a string
        // *returns* string battle status
        public string getBattleStatusAsString()
        {
            string strBattleStatus = string.Empty;

            // Loops through all the ship appending their individuoal statuses
            foreach (Ship ship in ships)
            {
                strBattleStatus += ship.GetStatusAsString() + Environment.NewLine;
            }

            return strBattleStatus;
        }

        // Checks if coordinate is withing battle dimension
        // *param* int x    coordinate x
        // *param* int y    coordinate y
        // *return* bool    true if coordinate is withing range
        private bool IsCorrectCoordinate(int x, int y)
        {
            if (x > dimension || x < 0 ||
               y > dimension || y < 0)
            {
                return false;
            }

            return true;
        }

        // Processes input string
        // *param* string command   input command
        // *return& string[]    processes string array
        private string[] ProcessCommand(string command)
        {
            // Removes all the { } and whitespace simbols from the command
            string[] charsToRemove = new string[] { "(", ")", " " };
            foreach (var c in charsToRemove)
            {
                command = command.Replace(c, string.Empty);
            }

            // Splits string at , to get individual valuesa
            return command.Split(',');
        }

        
        // Atempts to move a ship
        // *param* string    move command
        private void MoveShip(string command)
        {
            Console.WriteLine("Attempting to move ship");
            
            string[] split = ProcessCommand(command); // processes input command

            int x, y; // x,y ship coordinates

            // Tries to parse source ship coordinates
            if (!int.TryParse(split[0], out x) || !int.TryParse(split[1], out y))
            {
                Console.WriteLine("Failed to move. Invalid coordinate type.");
                return;
            }

            // Checks if coordinates are withing range
            if (!IsCorrectCoordinate(x, y))
            {
                Console.WriteLine("Failed to move. Source ship cordinates out of bound.");
                return;
            }

            // Tries to find matching ship that is alive
            Ship ship = ships.FirstOrDefault(s => s.GetStartCoordinates().x == x && s.GetStartCoordinates().y == y && s.IsAlive());

            // if ship not found return
            if (ship == null)
            {
                Console.WriteLine("Failed to move. Source ship not found or destroyed.");
                return;
            }
            
            // Process move command
            foreach(char c in split[2])
            {
                switch(char.ToUpper(c))
                {
                    case 'R':   // Rotates right if R command
                        Console.WriteLine("Rotating right");
                        ship.RotateRight();
                        break;
                    case 'L':   // Rotates left if L command
                        Console.WriteLine("Rotating left");
                        ship.RotateLeft();
                        break;
                    case 'M':   // Moves forward if M command
                        if(ship.Move(ships)) // Atempts to move forward
                        {
                            Console.WriteLine("Moving forward");
                        }
                        else
                        {
                            Console.WriteLine("Invalid move forward. Aborting");
                        }

                        break;
                    default:
                        Console.WriteLine("Invalid move command. Ignoring");
                        break;
                }
            }

        }
        
        // Processes shoot ship command
        // *param* string  wshoot ship command
        private void ShootShip(string command)
        {
            Console.WriteLine("Attempting to shoot");

            string[] split = ProcessCommand(command); // process command

            int x1, x2, y1, y2; // coordinates of target and source ship

            // Atempt to parse these coordinates            
            if (!int.TryParse(split[0], out x1) || !int.TryParse(split[1], out y1) ||
                !int.TryParse(split[2], out x2) || !int.TryParse(split[3], out y2))
            {
                Console.WriteLine("Failed to shoot. Invalid coordinate type.");
                return;
            }

            // Check if coordinates are withing range
            if (!IsCorrectCoordinate(x1, y1) || !IsCorrectCoordinate(x2, y2))
            {
                Console.WriteLine("Failed to shoot. Coordinates out of bound.");
                return;
            }

            // Checks if source ship is found and alive
            if (ships.Count(s => s.GetStartCoordinates().x == x1 && s.GetStartCoordinates().y == y1 && s.IsAlive()) < 1)
            {
                Console.WriteLine("Failed to shoot. Source ship not found or destroyed.");
                return;
            }

            // Loops through all the ships checking if they have been hit
            // Note SHOOTING at yourself is allowed. Captain might make mistake :)
            foreach(Ship ship in ships)
            {
                if(ship.IsAlive() && ship.IsHit(x2, y2))
                {
                    Console.WriteLine("HIT!!!");
                    return;
                }
            }

            Console.WriteLine("Miss :(");
        }

        // Processes add ship command
        // *param* string command add ship command
        private void AddShip(string command)
        {
            Console.WriteLine("Adding new ship to the battle");

            string[] split = ProcessCommand(command); // process a command

            int x, y; // Ship coordinates

            // Try parsing the ship coordiantes        
            if (!int.TryParse(split[0], out x) || !int.TryParse(split[1], out y))
            {
                Console.WriteLine("Failed to add new ship. Invalid coordinate type.");
                return;
            }

            // Check if coordiantes are withing range
            if(!IsCorrectCoordinate(x, y))
            {
                Console.WriteLine("Failed to add new ship. Coordinates out of bound.");
                return;
            }
           
            // Check if ship at those coordinates aleardy taken
            if (ships.Count(s => s.GetCoordinates().x == x && s.GetCoordinates().y == y) > 0)
            {
                Console.WriteLine("Failed to add new ship. Ship already exists at that possition.");
                return;
            }

            try
            {
                Ship ship = new Ship(x, y, split[2][0], dimension); // Tries to create a ship
                ships.Add(ship); // adds ship to the list
            }
            catch (ArgumentException) // Is thrown if direction is of incorrect format
            {
                Console.WriteLine("Failed to add a new ship. Invalid direction.");
                return;
            }

            Console.WriteLine("Ship created successfully.");
        }


        // Checks whitch type of command is it and calls respective method
        //*param* string command    command to be proccessed
        private void ExecuteCommand(string command)
        {
            Console.WriteLine(string.Format("Executing {0}", command));

            // Using regular expressions to check the type of command
            string addShipPattern = @"\s*\(\s*\d+\s*,\s*\d+\s*,\s*\D{1}\s*\)";  // expected pattern (int, int, char)
            string moveShipPattern = @"\s*\(\s*\d+\s*,\s*\d+\s*\)\s*,\s*\D*";       // expected pattern (int, int),  char*
            string shootShipPattern = @"\s*\(\s*\d+\s*,\s*\d+\s*\)\s*,\s*\(\s*\d+\s*,\s*\d+\s*\)";  // expected pattern (int, int), (int, int)

            // Add ship if match
            if(Regex.IsMatch(command, addShipPattern))
            {
                AddShip(command);
                return;            
            }

            // shoot ship if match
            if (Regex.IsMatch(command, shootShipPattern))
            {
                ShootShip(command);
                return;
            }

            // move ship if match
            if (Regex.IsMatch(command, moveShipPattern))
            {
                MoveShip(command);
                return;
            }

            Console.WriteLine(string.Format("Invalid command {0}. Skipping.", command));            
        }
    }
}
