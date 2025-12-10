package Code_System;

import Code_System.Player;
import Code_System.Tile;

//Inheritance: Extends Tile abstract class
//Polymorphism: Provides specific implementation of abstract methods
public class ExitTile extends Tile {
// Encapsulation: Private field specific to ExitTile
private boolean challengeTriggered;

// Constructor
public ExitTile(int x, int y) {
   super(x, y, true);  // ExitTiles are walkable
   this.challengeTriggered = false;
}

// Encapsulation: Getter
public boolean isChallengeTriggered() {
   return challengeTriggered;
}

// Polymorphism: Specific implementation for ExitTile
@Override
public void onStep(Player player) {
   System.out.println("\n=================================");
   System.out.println("    EXIT REACHED!");
   System.out.println("=================================");
   System.out.println("Player at position: (" + player.getX() + ", " + player.getY() + ")");
}

// Polymorphism: Specific implementation for ExitTile
@Override
public String getType() {
   return "EXIT";
}

// Helper method to mark challenge as triggered
public void triggerChallenge() {
   this.challengeTriggered = true;
}
}
