package Code_System;

import java.util.LinkedList;
import java.util.Queue;

/**
 * TileCollapseManager - Uses Queue (FIFO) data structure
 * Manages vanishing tiles - tiles collapse in order they were stepped on
 * 
 * Queue (FIFO = First In, First Out):
 * - First tile stepped on is first to collapse
 * - Perfect for progressive tile vanishing mechanic
 */
public class TileCollapseManager {

    // =====================================
    // SECTION 1: PROPERTIES
    // =====================================
    
    /**
     * Queue to store tiles waiting to collapse (FIFO order)
     * Tiles are added when player steps on them
     */
    private Queue<TilePosition> collapseQueue;
    
    /**
     * Reference to the maze to access and modify tiles
     */
    private Maze maze;
    
    /**
     * Time between each tile collapse (milliseconds)
     * Default: 5000ms = 5 seconds
     */
    private long collapseInterval;
    
    /**
     * Timestamp of when last tile collapsed
     * Used to track when next collapse should happen
     */
    private long lastCollapseTime;
    
    /**
     * Whether collapse system is currently active
     * Activated when timer reaches 2 minutes
     */
    private boolean isActive;
    
    /**
     * Counter for total tiles that have collapsed
     */
    private int tilesCollapsedCount;

    // =====================================
    // SECTION 2: INNER CLASS - TilePosition
    // =====================================
    
    /**
     * TilePosition - Stores position and timestamp of a tile
     * Used to track when tiles were stepped on
     */
    public static class TilePosition {
        private int x;
        private int y;
        private long timestamp; // When tile was added to queue

        /**
         * Constructor - Records position and current time
         */
        public TilePosition(int x, int y) {
            this.x = x;
            this.y = y;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public int getX() { return x; }
        public int getY() { return y; }
        public long getTimestamp() { return timestamp; }

        /**
         * Gets age of this tile position (how long ago it was stepped on)
         */
        public long getAge() {
            return System.currentTimeMillis() - timestamp;
        }
    }

    // =====================================
    // SECTION 3: CONSTRUCTOR
    // =====================================
    
    /**
     * Constructor - Initializes collapse manager
     * @param maze Reference to the game's maze
     */
    public TileCollapseManager(Maze maze) {
        this.maze = maze;
        this.collapseQueue = new LinkedList<>(); // Queue implementation
        this.collapseInterval = 5000; // 5 seconds between collapses
        this.lastCollapseTime = System.currentTimeMillis();
        this.isActive = false;
        this.tilesCollapsedCount = 0;
        
        System.out.println("TileCollapseManager initialized");
    }

    // =====================================
    // SECTION 4: QUEUE OPERATIONS
    // =====================================
    
    /**
     * Adds a tile to the collapse queue (FIFO - add to back)
     * Called when player steps on a tile
     * @param x X coordinate of tile
     * @param y Y coordinate of tile
     */
    public void enqueueTile(int x, int y) {
        TilePosition pos = new TilePosition(x, y);
        collapseQueue.offer(pos); // FIFO - add to back
        System.out.println("Tile queued for collapse: (" + x + ", " + y + ")");
    }

    /**
     * Removes and returns next tile to collapse (FIFO - remove from front)
     * @return Next tile position, or null if empty
     */
    private TilePosition dequeueNextTile() {
        return collapseQueue.poll(); // FIFO - remove from front
    }

    /**
     * Peeks at next tile without removing it
     * @return Next tile position, or null if empty
     */
    public TilePosition peekNextTile() {
        return collapseQueue.peek();
    }

    /**
     * Checks if collapse queue is empty
     * @return true if no tiles waiting to collapse
     */
    public boolean isQueueEmpty() {
        return collapseQueue.isEmpty();
    }

    /**
     * Gets number of tiles waiting to collapse
     * @return Queue size
     */
    public int getQueueSize() {
        return collapseQueue.size();
    }

    // =====================================
    // SECTION 5: COLLAPSE MECHANICS
    // =====================================
    
    /**
     * Activates the collapse system
     * Called when timer reaches 2 minutes (vanishing trigger)
     */
    public void activate() {
        isActive = true;
        lastCollapseTime = System.currentTimeMillis();
        System.out.println("\nWARNING: Tile collapse system ACTIVATED!");
        System.out.println("Tiles will start vanishing in order stepped on!\n");
    }

    /**
     * Deactivates the collapse system
     */
    public void deactivate() {
        isActive = false;
        System.out.println("Tile collapse system deactivated");
    }

    /**
     * Updates collapse system - called every frame in game loop
     * Collapses tiles at regular intervals
     */
    public void update() {
        // Don't update if not active or no tiles to collapse
        if (!isActive || collapseQueue.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastCollapse = currentTime - lastCollapseTime;
        
        // Check if enough time passed to collapse next tile
        if (timeSinceLastCollapse >= collapseInterval) {
            collapseTile();
            lastCollapseTime = currentTime;
        }
    }

    /**
     * Collapses the next tile in queue
     * Makes tile vanished and unwalkable
     */
    private void collapseTile() {
        if (collapseQueue.isEmpty()) {
            return;
        }

        // Get next tile from queue (FIFO - first stepped on)
        TilePosition nextTile = dequeueNextTile();
        
        // Get tile from maze
        Tile tile = maze.getTile(nextTile.getX(), nextTile.getY());
        
        if (tile != null && !tile.isVanished()) {
            // Make tile vanish
            tile.setVanished(true);
            tile.setWalkable(false);
            
            tilesCollapsedCount++;
            
            System.out.println("Tile COLLAPSED at (" + nextTile.getX() + 
                             ", " + nextTile.getY() + ") - Age: " + 
                             nextTile.getAge() + "ms");
        }
    }

    /**
     * Instantly collapses all remaining tiles in queue
     * Called when player gets final question wrong
     */
    public void collapseAllTiles() {
        System.out.println("\n COLLAPSING ALL TILES INSTANTLY! ");
        
        int collapsed = 0;
        while (!collapseQueue.isEmpty()) {
            TilePosition pos = dequeueNextTile();
            Tile tile = maze.getTile(pos.getX(), pos.getY());
            
            if (tile != null) {
                tile.setVanished(true);
                tile.setWalkable(false);
                collapsed++;
            }
        }
        
        tilesCollapsedCount += collapsed;
        System.out.println(" Total tiles collapsed: " + collapsed);
    }

    // =====================================
    // SECTION 6: CONFIGURATION
    // =====================================
    
    /**
     * Sets the interval between tile collapses
     * @param milliseconds Time between collapses in ms
     */
    public void setCollapseInterval(long milliseconds) {
        this.collapseInterval = milliseconds;
        System.out.println("Collapse interval set to: " + milliseconds + "ms");
    }

    /**
     * Speeds up collapse rate (for increasing difficulty)
     * @param multiplier Speed multiplier (0.5 = twice as fast)
     */
    public void accelerateCollapse(double multiplier) {
        collapseInterval = (long)(collapseInterval * multiplier);
        System.out.println("⚡ Collapse speed increased! New interval: " + 
                         collapseInterval + "ms");
    }

    // =====================================
    // SECTION 7: RESET SYSTEM
    // =====================================
    
    /**
     * Resets the collapse manager for new level
     * Clears queue and resets counters
     */
    public void reset() {
        collapseQueue.clear();
        tilesCollapsedCount = 0;
        lastCollapseTime = System.currentTimeMillis();
        isActive = false;
        System.out.println(" TileCollapseManager reset");
    }

    // =====================================
    // SECTION 8: GETTERS
    // =====================================
    
    public boolean isActive() {
        return isActive;
    }

    public int getTilesCollapsedCount() {
        return tilesCollapsedCount;
    }

    public long getCollapseInterval() {
        return collapseInterval;
    }

   
}