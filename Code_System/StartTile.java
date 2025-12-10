package Code_System;

import Code_System.Player;
import Code_System.Tile;

//Inheritance: Extends Tile abstract class
//Polymorphism: Provides specific implementation of abstract methods
public class StartTile extends Tile {

// Constructor
public StartTile(int x, int y) {
   super(x, y, true);  // StartTiles are walkable
}

// Polymorphism: Specific implementation for StartTile
@Override
public void onStep(Player player) {
   // Start tile - no special effect
   // Player spawns here at beginning of level
}

// Polymorphism: Specific implementation for StartTile
@Override
public String getType() {
   return "START";
}
}
