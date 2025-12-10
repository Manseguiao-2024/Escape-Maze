package Code_System;

import java.util.ArrayList;
import java.util.Stack;

/**
 * LevelHistoryManager - Uses Stack (LIFO) data structure
 * Tracks level progression history for undo, restart, and progress tracking
 * 
 * Stack (LIFO = Last In, First Out):
 * - Like a stack of plates: last one added is first one removed
 * - push() adds to top, pop() removes from top
 * - Perfect for tracking history where most recent is most important
 */
public class LevelHistoryManager {
    // SECTION 1: PROPERTIES
    private Stack<LevelState> levelHistory;
    private int maxHistorySize;
    
    
    // SECTION 2: INNER CLASS - LevelState
    public static class LevelState {
        // Level information
        private int levelNumber;           // Which level (1-5)
        private int playerHearts;          // Hearts remaining
        private int timeRemaining;         // Seconds left on timer
        private ArrayList<Character> collectedLetters;  // Letters collected
        private long timestamp;            // When this state was recorded
        private boolean levelCompleted;    // Did player complete the level?
        
        /**
         * Constructor - Creates a snapshot of current level state
         * @param levelNumber Current level (1-5)
         * @param playerHearts Hearts remaining
         * @param timeRemaining Seconds left
         * @param collectedLetters Letters collected so far
         * @param completed Whether level was completed
         */
        public LevelState(int levelNumber, int playerHearts, int timeRemaining, 
                         ArrayList<Character> collectedLetters, boolean completed) {
            this.levelNumber = levelNumber;
            this.playerHearts = playerHearts;
            this.timeRemaining = timeRemaining;
            // Create copy of letters to prevent external modification
            this.collectedLetters = new ArrayList<>(collectedLetters);
            this.timestamp = System.currentTimeMillis(); // Current time in milliseconds
            this.levelCompleted = completed;
        }
        
        // Getter methods - Allow read access to private fields
        public int getLevelNumber() { return levelNumber; }
        public int getPlayerHearts() { return playerHearts; }
        public int getTimeRemaining() { return timeRemaining; }
        public ArrayList<Character> getCollectedLetters() { 
            // Return copy to prevent external modification
            return new ArrayList<>(collectedLetters); 
        }
        public long getTimestamp() { return timestamp; }
        public boolean isLevelCompleted() { return levelCompleted; }
        
        /**
         * toString - Converts LevelState to readable string
         * Used for debugging and display
         */
        @Override
        public String toString() {
            return "Level " + levelNumber + 
                   " | Hearts: " + playerHearts + 
                   " | Time: " + timeRemaining + "s" +
                   " | Letters: " + collectedLetters.size() +
                   " | Completed: " + (levelCompleted ? "YES" : "NO");
        }
    }
    
    // ==========================================
    // SECTION 3: CONSTRUCTORS
    // ==========================================
    
    /**
     * Default Constructor
     * Creates manager with default max size of 10 states
     */
    public LevelHistoryManager() {
        this.levelHistory = new Stack<>();
        this.maxHistorySize = 10;  // Store last 10 level attempts
        System.out.println("LevelHistoryManager created (max size: 10)");
    }
    
    /**
     * Overloaded Constructor
     * Creates manager with custom max size
     * @param maxSize Maximum number of states to store
     */
    public LevelHistoryManager(int maxSize) {
        this.levelHistory = new Stack<>();
        this.maxHistorySize = maxSize;
        System.out.println("LevelHistoryManager created (max size: " + maxSize + ")");
    }
    
    // ==========================================
    // SECTION 4: STACK OPERATIONS
    // ==========================================
    
    /**
     * Pushes a level state onto the history stack (LIFO - adds to top)
     * Automatically removes oldest entry if max size exceeded
     * 
     * @param levelNumber Current level
     * @param playerHearts Hearts remaining
     * @param timeRemaining Time left in seconds
     * @param collectedLetters Letters collected
     * @param completed Whether level was completed
     */
    public void pushLevelState(int levelNumber, int playerHearts, int timeRemaining,
                               ArrayList<Character> collectedLetters, boolean completed) {
        // Create snapshot of current state
        LevelState state = new LevelState(levelNumber, playerHearts, timeRemaining, 
                                         collectedLetters, completed);
        
        // Push to top of stack (LIFO)
        levelHistory.push(state);
        
        System.out.println(" Level state saved: " + state);
        
        // If stack exceeds max size, remove oldest entry (bottom of stack)
        if (levelHistory.size() > maxHistorySize) {
            // Temporary stack to access bottom element
            Stack<LevelState> temp = new Stack<>();
            
            // Move all except oldest to temp stack
            while (levelHistory.size() > 1) {
                temp.push(levelHistory.pop());
            }
            
            // Remove oldest (now at top)
            levelHistory.clear();
            
            // Restore all elements
            while (!temp.isEmpty()) {
                levelHistory.push(temp.pop());
            }
            
            System.out.println(" History limit reached - oldest entry removed");
        }
    }
    
    /**
     * Pops the most recent level state from stack (LIFO - removes from top)
     * @return Most recent LevelState, or null if stack is empty
     */
    public LevelState popLevelState() {
        if (levelHistory.isEmpty()) {
            System.out.println(" No level history to pop");
            return null;
        }
        
        // Pop from top of stack (LIFO)
        LevelState state = levelHistory.pop();
        System.out.println(" Level state popped: " + state);
        return state;
    }
    
    /**
     * Peeks at the most recent level state without removing it
     * @return Most recent LevelState, or null if stack is empty
     */
    public LevelState peekLastLevel() {
        if (levelHistory.isEmpty()) {
            return null;
        }
        // Peek at top without removing (Stack method)
        return levelHistory.peek();
    }
    
    /**
     * Checks if history stack is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return levelHistory.isEmpty();
    }
    
    /**
     * Gets the current size of history stack
     * @return Number of stored level states
     */
    public int getHistorySize() {
        return levelHistory.size();
    }
    
    // ==========================================
    // SECTION 5: HISTORY MANAGEMENT
    // ==========================================
    
    /**
     * Gets the previous level's state (second-to-top) without removing
     * Useful for "restart previous level" feature
     * @return Previous LevelState, or null if not available
     */
    public LevelState getPreviousLevel() {
        // Need at least 2 entries to have a "previous"
        if (levelHistory.size() < 2) {
            System.out.println(" No previous level available");
            return null;
        }
        
        // Temporarily pop top to access second-to-top
        LevelState top = levelHistory.pop();
        LevelState previous = levelHistory.peek();
        levelHistory.push(top);  // Put top back
        
        return previous;
    }
    
    /**
     * Clears all level history
     * Used when starting new game
     */
    public void clearHistory() {
        levelHistory.clear();
        System.out.println(" Level history cleared");
    }
    
    /**
     * Counts how many levels were successfully completed in history
     * @return Number of completed levels
     */
    public int getCompletedLevelCount() {
        int count = 0;
        
        // Iterate through all states in stack
        for (LevelState state : levelHistory) {
            if (state.isLevelCompleted()) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Counts how many level attempts failed in history
     * @return Number of failed attempts
     */
    public int getFailedAttemptCount() {
        int count = 0;
        
        // Iterate through all states in stack
        for (LevelState state : levelHistory) {
            if (!state.isLevelCompleted()) {
                count++;
            }
        }
        
        return count;
    }
    
    // ==========================================
    // SECTION 6: DISPLAY & DEBUG
    // ==========================================
    
    /**
     * Prints the entire level history
     * Shows oldest first (reverses stack order for readability)
     */
    public void printHistory() {
        System.out.println("\n=================================");
        System.out.println("      LEVEL HISTORY");
        System.out.println("=================================");
        
        if (levelHistory.isEmpty()) {
            System.out.println("No history recorded");
            System.out.println("=================================\n");
            return;
        }
        
        // Temporary stack to reverse order (show oldest first)
        Stack<LevelState> temp = new Stack<>();
        
        // Pop all to temp (reverses order)
        while (!levelHistory.isEmpty()) {
            temp.push(levelHistory.pop());
        }
        
        int index = 1;
        // Print and restore to original stack
        while (!temp.isEmpty()) {
            LevelState state = temp.pop();
            System.out.println(index + ". " + state);
            levelHistory.push(state);  // Restore to original stack
            index++;
        }
        
        System.out.println("=================================");
        System.out.println("Total attempts: " + levelHistory.size());
        System.out.println("=================================\n");
    }
    
    /**
     * Gets a summary of player's progress
     * @return Summary string with latest level, completed, failed, total
     */
    public String getProgressSummary() {
        if (levelHistory.isEmpty()) {
            return "No progress recorded";
        }
        
        LevelState latest = peekLastLevel();
        int completed = getCompletedLevelCount();
        int failed = getFailedAttemptCount();
        
        return "Latest: Level " + latest.getLevelNumber() + 
               " | Completed: " + completed + 
               " | Failed: " + failed +
               " | Total Attempts: " + getHistorySize();
    }
    
    // ==========================================
    // SECTION 7: ADVANCED FEATURES
    // ==========================================
    
    /**
     * Checks if player is stuck (failed same level 3+ times in a row)
     * Can be used to trigger hints or mercy logic
     * @return true if player failed same level 3+ times consecutively
     */
    public boolean isPlayerStuck() {
        // Need at least 3 attempts to determine if stuck
        if (levelHistory.size() < 3) {
            return false;
        }
        
        // Temporary stack to examine last 3 attempts
        Stack<LevelState> temp = new Stack<>();
        int sameLevel = 0;
        int checkLevel = -1;
        
        // Pop last 3 entries to examine
        for (int i = 0; i < Math.min(3, levelHistory.size()); i++) {
            LevelState state = levelHistory.pop();
            temp.push(state);
            
            // First iteration: record which level we're checking
            if (checkLevel == -1) {
                checkLevel = state.getLevelNumber();
            }
            
            // Count if same level and not completed
            if (state.getLevelNumber() == checkLevel && !state.isLevelCompleted()) {
                sameLevel++;
            }
        }
        
        // Restore stack (put everything back)
        while (!temp.isEmpty()) {
            levelHistory.push(temp.pop());
        }
        
        // Player is stuck if failed same level 3+ times
        return sameLevel >= 3;
    }
    
    // ==========================================
    // SECTION 8: GETTERS & SETTERS
    // ==========================================
    
    /**
     * Gets the maximum history size
     * @return Max number of states that can be stored
     */
    public int getMaxHistorySize() { 
        return maxHistorySize; 
    }
    
    /**
     * Sets a new maximum history size
     * @param size New max size
     */
    public void setMaxHistorySize(int size) { 
        this.maxHistorySize = size; 
        System.out.println(" Max history size updated to: " + size);
    }

 // Add these variables at the top of the class
    private int resetCount = 0;
    private int currentStreak = 0;
    private int longestStreak = 0;

    /**
     * Increment reset counter when player returns to level 1
     */
    public void incrementResetCount() {
        resetCount++;
        currentStreak = 0; // Reset streak
        System.out.println("📉 Reset to Level 1 (Total resets: " + resetCount + ")");
    }

    /**
     * Update streak when level is completed
     */
    public void updateStreak(int level) {
        currentStreak = level;
        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
            System.out.println("🔥 New longest streak: " + longestStreak + " levels");
        }
    }

    /**
     * Get total number of resets to level 1
     */
    public int getResetCount() {
        return resetCount;
    }

    /**
     * Get longest streak (highest level reached without reset)
     */
    public int getLongestStreak() {
        return longestStreak;
    }

    
}
