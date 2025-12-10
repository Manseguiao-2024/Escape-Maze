package Code_System;

//week 2 task
import java.util.HashMap;
import java.util.Random;

/**
* Maze - Uses polymorphic Tile hierarchy
* Demonstrates Inheritance and Polymorphism OOP principles
*/
public class Maze {

// 1. Properties
private Tile[][] grid; // Polymorphism: Array of abstract Tile (holds any subclass)
private int width;
private int height;
private int startX, startY;
private int exitX, exitY;
private HashMap<String, Cluebox> clueBoxMap; // HashMap for O(1) clue lookup
private QuestionManager questionManager; // For getting questions

// =============================
// 2. Constructor
// =============================
public Maze(int width, int height) {
   this.width = width;
   this.height = height;
   grid = new Tile[height][width]; // Polymorphism: Can hold any Tile subclass
   clueBoxMap = new HashMap<>();
   questionManager = new QuestionManager();
   initializeGrid();
}

// =============================
// Helper: Generate Clue Key
// =============================
private String generateClueKey(int x, int y) {
   return x + "," + y;
}

private char getLetterForLevel(int level, int index) {
   switch(level) {
       case 1: return "CODER".charAt(index);
       case 2: return "STACK".charAt(index);
       case 3: return "QUEUE".charAt(index);
       case 4: return "ARRAY".charAt(index);
       case 5: return "GRAPH".charAt(index);
       default: 
           System.err.println("Invalid level: " + level);
           return '?';
   }
}

// =============================
// 3. Initialize Grid Method
// =============================
/**
* Initializes grid with PathTile objects
* Polymorphism: Creates specific tile subclass instances
*/
public void initializeGrid() {
   for (int y = 0; y < height; y++) {
       for (int x = 0; x < width; x++) {
           // Polymorphism: Create PathTile (subclass of Tile)
           grid[y][x] = new PathTile(x, y);
       }
   }
}

// =============================
// 4. Place Tile Method
// =============================
/**
* Places a tile at specified position
* Polymorphism: Accepts any Tile subclass
*/
public void placeTile(int x, int y, Tile tile) {
   if (isValidPosition(x, y)) {
       grid[y][x] = tile;
   } else {
       System.out.println("Invalid position: (" + x + ", " + y + ")");
   }
}

// =============================
// 5. Get Tile Method
// =============================
/**
* Gets tile at position
* Polymorphism: Returns Tile reference (could be any subclass)
*/
public Tile getTile(int x, int y) {
   if (isValidPosition(x, y)) {
       return grid[y][x];
   }
   return null;
}

// =============================
// 6. Is Walkable Method
// =============================
public boolean isWalkable(int x, int y) {
   if (!isValidPosition(x, y)) return false;
   Tile tile = grid[y][x];
   if (tile == null) return false;

   // Polymorphism: isWalkable() defined in each subclass
   return tile.isWalkable() && !tile.isVanished();
}

// =============================
// 7. Is Valid Position Method
// =============================
public boolean isValidPosition(int x, int y) {
   return (x >= 0 && x < width) && (y >= 0 && y < height);
}

// =============================
// 8. Generate Maze Method
// =============================
/**
* Generates complete maze layout
* Uses polymorphic tile creation
*/
public void generateMaze(int level) {
   initializeGrid(); // Creates PathTile objects
   clearClueBoxes();
   placeBorderWalls(); // Creates WallTile objects
   placeInternalWalls(level); // Creates WallTile objects
   placeClueBoxes(5, level); // Creates ClueTile objects
   placeHazards(2 + level); // Creates HazardTile objects

   // START tile - Polymorphism: Create StartTile subclass
   startX = 1;
   startY = 1;
   grid[startY][startX] = new StartTile(startX, startY);

   // EXIT tile - Polymorphism: Create ExitTile subclass
   exitX = width - 2;
   exitY = height - 2;
   grid[exitY][exitX] = new ExitTile(exitX, exitY);

   System.out.println("Maze generated for Level " + level + "!");
}

// =============================
// 9. Place Border Walls Method
// =============================
/**
* Places WallTile objects around border
* Polymorphism: Creates WallTile subclass instances
*/
public void placeBorderWalls() {
   for (int y = 0; y < height; y++) {
       // Polymorphism: Create WallTile (subclass of Tile)
       grid[y][0] = new WallTile(0, y); // Left border
       grid[y][width - 1] = new WallTile(width - 1, y); // Right border
   }

   for (int x = 0; x < width; x++) {
       grid[0][x] = new WallTile(x, 0); // Top border
       grid[height - 1][x] = new WallTile(x, height - 1); // Bottom border
   }
}

// =============================
// 10. Place Internal Walls Method
// =============================
/**
* Places random WallTile objects inside maze
* Polymorphism: Creates WallTile subclass instances
*/
public void placeInternalWalls(int level) {
   int wallCount = 50 + (level * 5);
   int placed = 0;
   Random rand = new Random();

   while (placed < wallCount) {
       int randX = 1 + rand.nextInt(width - 2);
       int randY = 1 + rand.nextInt(height - 2);

       // Avoid start/exit area
       if ((randX <= 2 && randY <= 2) || 
           (randX >= width - 3 && randY >= height - 3)) continue;

       // Polymorphism: Check type using getType() (polymorphic method)
       if (grid[randY][randX].getType().equals("PATH")) {
           // Polymorphism: Replace PathTile with WallTile
           grid[randY][randX] = new WallTile(randX, randY);
           placed++;
       }
   }
}

// =============================
// 11. Place Clue Boxes Method
// =============================
/**
* Places ClueTile objects with associated Cluebox data
* Polymorphism: Creates ClueTile subclass instances
*/
public void placeClueBoxes(int count, int level) {
   int placed = 0;
   Random rand = new Random();

   while (placed < count) {
       int randX = 1 + rand.nextInt(width - 2);
       int randY = 1 + rand.nextInt(height - 2);

       // Polymorphism: Check type using polymorphic getType() method
       if (grid[randY][randX].getType().equals("PATH")) {
           // Polymorphism: Replace PathTile with ClueTile
           grid[randY][randX] = new ClueTile(randX, randY);

           // Create and store Cluebox in HashMap
           String key = generateClueKey(randX, randY);
           Cluebox clueBox = createClueBoxForLevel(randX, randY, placed, level);
           clueBoxMap.put(key, clueBox);
           
           placed++;
       }
   }
   
   System.out.println("Placed " + placed + " clue boxes in HashMap for Level " + level);
}

/**
* Creates a Cluebox with question from QuestionManager
*/
private Cluebox createClueBoxForLevel(int x, int y, int index, int level) {
   QuestionManager.Question question = questionManager.getRandomQuestion(level);
   char letter = getLetterForLevel(level, index);
   
   if (question == null) {
       System.err.println("Warning: No question available for level " + level);
       return new Cluebox(x, y, "Error: No question available", "Error", letter);
   }
   
   String questionText = question.getQuestionText();
   String[] choices = question.getChoices();
   int correctIndex = question.getCorrectAnswerIndex();
   String correctAnswer = choices[correctIndex];
   
   return new Cluebox(x, y, questionText, correctAnswer, letter);
}

// =============================
// HashMap Operations
// =============================

public Cluebox getClueBox(int x, int y) {
   String key = generateClueKey(x, y);
   return clueBoxMap.get(key);
}

public boolean hasClueBox(int x, int y) {
   String key = generateClueKey(x, y);
   return clueBoxMap.containsKey(key);
}

public void clearClueBoxes() {
   clueBoxMap.clear();
   System.out.println("Clue boxes cleared from HashMap");
}

// =============================
// 12. Place Hazards Method
// =============================
/**
* Places HazardTile objects in maze
* Polymorphism: Creates HazardTile subclass instances
*/
public void placeHazards(int count) {
   int placed = 0;
   Random rand = new Random();

   while (placed < count) {
       int randX = 1 + rand.nextInt(width - 2);
       int randY = 1 + rand.nextInt(height - 2);
       Tile tile = grid[randY][randX];

       int distX = Math.abs(randX - startX);
       int distY = Math.abs(randY - startY);

       // Polymorphism: Check type using polymorphic getType() method
       if (tile.getType().equals("PATH") && (distX > 3 || distY > 3)) {
           // Polymorphism: Replace PathTile with HazardTile
           grid[randY][randX] = new HazardTile(randX, randY);
           placed++;
       }
   }
}

// =============================
// 13. Print Maze To Console Method
// =============================
/**
* Prints maze using polymorphic getType() method
* Polymorphism: Each tile type returns its own type string
*/
public void printMazeToConsole() {
   System.out.println("=== MAZE LAYOUT ===");
   for (int y = 0; y < height; y++) {
       for (int x = 0; x < width; x++) {
           // Polymorphism: getType() calls appropriate subclass method
           String type = grid[y][x].getType();
           switch (type) {
               case "WALL": System.out.print("█ "); break;
               case "PATH": System.out.print("· "); break;
               case "CLUE": System.out.print("C "); break;
               case "HAZARD": System.out.print("H "); break;
               case "START": System.out.print("S "); break;
               case "EXIT": System.out.print("E "); break;
               default: System.out.print("? "); break;
           }
       }
       System.out.println();
   }
   System.out.println("==================");
}

// =============================
// 14. Get Clue Positions Method
// =============================
public int[][] getCluePositions() {
   int clueCount = 0;

   // Polymorphism: getType() resolves to ClueTile.getType()
   for (int y = 0; y < height; y++) {
       for (int x = 0; x < width; x++) {
           if (grid[y][x].getType().equals("CLUE")) clueCount++;
       }
   }

   int[][] positions = new int[clueCount][2];
   int index = 0;

   for (int y = 0; y < height; y++) {
       for (int x = 0; x < width; x++) {
           if (grid[y][x].getType().equals("CLUE")) {
               positions[index][0] = x;
               positions[index][1] = y;
               index++;
           }
       }
   }

   return positions;
}

// =============================
// 15. Getter Methods
// =============================
public int getWidth() { return width; }
public int getHeight() { return height; }
public int getStartX() { return startX; }
public int getStartY() { return startY; }
public int getExitX() { return exitX; }
public int getExitY() { return exitY; }
public int getClueBoxCount() { return clueBoxMap.size(); }


}