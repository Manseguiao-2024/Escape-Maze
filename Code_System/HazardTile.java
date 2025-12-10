package Code_System;

import Code_System.Player;
import Code_System.Tile;

//Inheritance: Extends Tile abstract class
//Polymorphism: Provides specific implementation of abstract methods
public class HazardTile extends Tile {
// Encapsulation: Private field specific to HazardTile
private int damageAmount;

// Constructor with default damage
public HazardTile(int x, int y) {
   this(x, y, 1);  // Default 1 damage (matches GameManager)
}

// Constructor with custom damage
public HazardTile(int x, int y, int damageAmount) {
   super(x, y, true);  // HazardTiles are walkable but dangerous
   this.damageAmount = damageAmount;
}

// Encapsulation: Getter for damage amount
public int getDamageAmount() {
   return damageAmount;
}

// Encapsulation: Setter for damage amount
public void setDamageAmount(int damageAmount) {
   this.damageAmount = damageAmount;
}

// Polymorphism: Specific implementation for HazardTile
@Override
public void onStep(Player player) {
   System.out.println("\nHAZARD! You stepped on a dangerous tile!");
   player.takeDamage(damageAmount);
}

// Polymorphism: Specific implementation for HazardTile
@Override
public String getType() {
   return "HAZARD";
}
}
