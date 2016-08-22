Parses input, pupulates the world with battleships based on input.
Plays battleships based on an input.
Outputs file containting battleships statuses and coordinates after the game.

Expected input format

Assumption: each ship has a unique starting coordinate. Which is used when refering  a ship on subsequent operations, 
in order to avoid manually recalculating current ship coordinates if ship moves before next command.

Program should be able to deal with subsequent commands in any order assuming that some ships have been added and dimension is specified in a first line

Add ship
(x, y, char)	ship start coordinates and direction of ship

Move ship
(x, y), char*	ship start coordinate and list of moves

Shoot ship
(x , y), (x, y) source ship start coordinate and target ship current coordinate