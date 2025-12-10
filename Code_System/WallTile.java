package Code_System;

import Code_System.Player;
import Code_System.Tile;

//Inheritance: Extends Tile abstract class
//Polymorphism: Provides specific implementation of abstract methods
public class WallTile extends Tile {

// Constructor
public WallTile(int x, int y) {
   super(x, y, false);  // WallTiles are NOT walkable
}

// Polymorphism: Specific implementation for WallTile
@Override
public void onStep(Player player) {
   // This shouldn't be called since walls aren't walkable
   // But if somehow called, just block
   System.out.println("Cannot step on wall at (" + getX() + ", " + getY() + ")");
}

// Polymorphism: Specific implementation for WallTile
@Override
public String getType() {
   return "WALL";
}
}