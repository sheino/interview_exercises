using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;

namespace BattleShips
{
    class Program
    {
        const string inputFileName = "input.txt";       // input file path
        const string outputFileName = "output.txt";     // output file path

        // Entry point to the program
        static void Main(string[] args)
        {
            Console.WriteLine("Reading input file");

            try
            {
                // Attemps to read the input file
                IEnumerable<string> lines = File.ReadLines(inputFileName);

                // If file is empty exit
                if (lines.Count() == 0)
                {
                    Console.WriteLine(string.Format("Error: {0} is empty.\nExiting", inputFileName));
                    Environment.Exit(1);
                }

                BattleController battleController = new BattleController(lines);  // Initialises battle controller

                battleController.StartBattle();  // starts battle
           
                string status = battleController.getBattleStatusAsString(); // gets battle status as a string

                File.WriteAllText(outputFileName, status); // Writes battle/ship status to the output file
            }
            catch (FileNotFoundException) // Catch file not found exception
            {
                Console.WriteLine(string.Format("Error: {0} not found.\nExiting", inputFileName));
                Environment.Exit(1);
            }
            catch (Exception) // Catch  all other exceptions
            {
                Console.WriteLine("Unknown error.\nExiting");
                Environment.Exit(1);
            }      
        }
    }
}
