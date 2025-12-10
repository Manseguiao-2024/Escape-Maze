package Code_System;

//week 2 task
import java.util.ArrayList; // For tracking path history
import java.util.LinkedList;  // For danger zone method

public class PathManager {

// LinkedList to store the path history
private LinkedList<Position> pathHistory;

// Maximum number of steps to keep in history
private int maxHistorySize;

// Inner class to represent a position on the path
public static class Position {
   private int x;
   private int y;
   private long timestamp;

   // Constructor: stores coordinates and current time
   public Position(int x, int y) {
       this.x = x;
       this.y = y;
       this.timestamp = System.currentTimeMillis();
   }

   // Getter methods
   public int getX() { return x; }
   public int getY() { return y; }
   public long getTimestamp() { return timestamp; }
}

// Constructor: initializes path history and sets max size
public PathManager() {
   pathHistory = new LinkedList<>();
   maxHistorySize = 100; // You can change this if needed
}

// Records a new step in the path
public void recordStep(int x, int y) {
   Position pos = new Position(x, y);
   pathHistory.addLast(pos); // Add to end of list

   // Remove oldest step if history exceeds limit
   if (pathHistory.size() > maxHistorySize) {
       pathHistory.removeFirst();
   }
}

// Returns the most recent position (does not remove it)
public Position getLastPosition() {
   if (pathHistory.isEmpty()) return null;
   return pathHistory.getLast();
}

// Removes and returns the most recent position
public Position undoLastStep() {
   if (pathHistory.isEmpty()) return null;
   return pathHistory.removeLast();
}

// Returns the oldest position (does not remove it)
public Position getOldestPosition() {
   if (pathHistory.isEmpty()) return null;
   return pathHistory.getFirst();
}

// Returns positions older than the given time threshold
public ArrayList<Position> getTilesInDangerZone(long timeThreshold) {
   ArrayList<Position> dangerTiles = new ArrayList<>();
   long currentTime = System.currentTimeMillis();

   for (Position pos : pathHistory) {
       long age = currentTime - pos.getTimestamp();
       if (age > timeThreshold) {
           dangerTiles.add(pos);
       }
   }

   return dangerTiles;
}

// Clears the entire path history
public void clearHistory() {
   pathHistory.clear();
}

// Returns the number of steps in the path
public int getPathLength() {
   return pathHistory.size();
}

// Checks if a specific tile has been visited
public boolean hasVisited(int x, int y) {
   for (Position pos : pathHistory) {
       if (pos.getX() == x && pos.getY() == y) {
           return true;
       }
   }
   return false;
}

// Returns a copy of the path history to avoid external modification
public LinkedList<Position> getPathHistoryCopy() {
   LinkedList<Position> copy = new LinkedList<>();
   for (Position pos : pathHistory) {
       copy.add(pos);
   }
   return copy;
}

// Prints the path history for testing/debugging
public void printPathHistory() {
   System.out.println("=== Path History ===");
   if (pathHistory.isEmpty()) {
       System.out.println("No steps recorded.");
   } else {
       int step = 1;
       long currentTime = System.currentTimeMillis();
       for (Position pos : pathHistory) {
           long age = currentTime - pos.getTimestamp();
           System.out.println("Step " + step + ": (" + pos.getX() + ", " + pos.getY() + ") - Age: " + age + " ms");
           step++;
       }
       System.out.println("Total steps: " + pathHistory.size());
   }
}

}
