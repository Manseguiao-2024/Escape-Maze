package Code_System;

import Code_System.Player;

//Abstract Base Class 
public abstract class Tile {
// Encapsulation: Private fields with controlled access
private int x;
private int y;
private boolean isVisible;
private boolean isWalkable;
private boolean isVanished;

// Constructor
public Tile(int x, int y, boolean isWalkable) {
   this.x = x;
   this.y = y;
   this.isVisible = false;  // Starts hidden
   this.isWalkable = isWalkable;
   this.isVanished = false;
}

// Encapsulation: Getter methods
public int getX() {
   return x;
}

public int getY() {
   return y;
}

public boolean isVisible() {
   return isVisible;
}

public boolean isWalkable() {
   return isWalkable;
}

public boolean isVanished() {
   return isVanished;
}

// Encapsulation: Setter methods
public void setVisible(boolean visible) {
   this.isVisible = visible;
}

protected void setWalkable(boolean walkable) {
   this.isWalkable = walkable;
}

public void setVanished(boolean vanished) {
   this.isVanished = vanished;
}

// Abstraction: Abstract method - forces subclasses to implement
// Polymorphism: Each subclass will have different behavior
public abstract void onStep(Player player);

// Abstraction: Abstract method for tile type identification
public abstract String getType();

// Common method that can be overridden (Polymorphism)
public void reveal() {
   this.isVisible = true;
}
}
