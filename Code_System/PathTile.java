package Code_System;

import Code_System.Player;
import Code_System.Tile;

//Inheritance: Extends Tile abstract class
//Polymorphism: Provides specific implementation of abstract methods
public class PathTile extends Tile {

// Constructor
public PathTile(int x, int y) {
   super(x, y, true);  // PathTiles are walkable
}

// Polymorphism: Specific implementation for PathTile
@Override
public void onStep(Player player) {
   // Normal path - no special effect
   // Just a regular walkable tile
}

// Polymorphism: Specific implementation for PathTile
@Override
public String getType() {
   return "PATH";
}
}
