package Code_System;

import Code_System.Player;
import Code_System.Tile;

//Inheritance: Extends Tile abstract class
//Polymorphism: Provides specific implementation of abstract methods
public class ClueTile extends Tile {

// Constructor
public ClueTile(int x, int y) {
   super(x, y, true);  // ClueTiles are walkable
}

// Polymorphism: Specific implementation for ClueTile
@Override
public void onStep(Player player) {
 
}

// Polymorphism: Specific implementation for ClueTile
@Override
public String getType() {
   return "CLUE";
}
}