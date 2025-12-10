package Code_System;
//week 1 task
public class Player {
  // 1. Properties
  private int x;
  private int y;
  private int hearts;
  private int maxHearts;
  private String collectedLetters;
  private String currentBoxPosition;  // Add this field
  // 2. Constructor
  public Player(int startX, int startY) {
      this.x = startX;
      this.y = startY;
      this.maxHearts = 3;
      this.hearts = maxHearts;
      this.collectedLetters = "";
      System.out.println("Player at position (" + x + ", " + y + ")");
  }

  // 3. Movement
public boolean move(int deltaX, int deltaY, int maxRows, int maxCols) {
  int newX = x + deltaX;
  int newY = y + deltaY;

  if (newX >= 0 && newX < maxRows && newY >= 0 && newY < maxCols) {
      x = newX;
      y = newY;
      System.out.println("Player moved to (" + x + ", " + y + ")");
      return true;
  } else {
      System.out.println("Move blocked: Out of bounds.");
      return false;
  }
}
//position
public void setPosition(int newX, int newY) {
  this.x = newX;
  this.y = newY;
  System.out.println("Player repositioned to (" + x + ", " + y + ")");
}

  // 4. Damage
  public void takeDamage(int damage) {
      hearts -= damage;
      if (hearts < 0) hearts = 0;
      System.out.println("Player took " + damage + " damage. Hearts left: " + hearts);
      if (hearts == 0) System.out.println("Player died!");
  }

  // 6. Letter Collection
  public void collectLetter(char letter) {
      collectedLetters += letter;
      System.out.println("Collected letter: '" + letter + "'. Current letters: " + collectedLetters);
  }
  
  public void setCurrentBoxPosition(String position) {
	    this.currentBoxPosition = position;
	}
  public String getCurrentBoxPosition() {
	    return this.currentBoxPosition;
	}

  // 7. Getters
  public int getX() { return x; }
  public int getY() { return y; }
  public int getHearts() { return hearts; }
  public int getMaxHearts() { return maxHearts; }
  public String getCollectedLetters() { return collectedLetters; }

}