package Code_System;

import java.io.InputStream; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.io.File;

public class GamePanel extends JPanel implements KeyListener {
    
    // ==================== SECTION 1: PROPERTIES ====================
    
    // Core game components
    private GameManager game;
    private Timer gameTimer;
    private boolean running;
    private GameWindow gameWindow;
    
    // Player Animation & Movement
    private BufferedImage[] sprites_down;
    private BufferedImage[] sprites_up;
    private BufferedImage[] sprites_left;
    private BufferedImage[] sprites_right;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private final int FRAME_DELAY = 150;
    private String lastDirection = "down";
    
    // Movement animation properties
    private boolean isAnimating = false;
    private boolean isMoving = false;
    private int animStartX, animStartY;
    private int animTargetX, animTargetY;
    private long animStartTime;
    private final int ANIM_DURATION = Constants.MOVE_ANIMATION_MS;
    
    // Movement delay to prevent key spam
    private long lastMoveTime = 0;
    private final int MOVE_DELAY_MS = Constants.MOVE_DELAY_MS;
    
    // Visual assets (tile images)
    private BufferedImage startTileImage;
    private BufferedImage pathTileImage;
    private BufferedImage wallTileImage;
    private BufferedImage clueTileImage;
    private BufferedImage hazardTileImage;
    private BufferedImage exitTileImage;
    private BufferedImage vanishedTileImage;
    
    // * SETTINGS GEAR PROPERTIES *
    // Adjust these to change settings gear appearance/position
    private int settingsX;                    // X position (calculated in drawTopPanels)
    private int settingsY;                    // Y position (calculated in drawTopPanels)
    private int settingsSize = 40;           // Size of the gear button (change this to resize)
    private boolean settingsHovered = false;  // Track if mouse is over gear
    
 // * SETTINGS MENU PROPERTIES *
    private boolean settingsMenuOpen = false;
    private int hoveredSettingOption = -1;
    private String[] settingOptions = {
        "Resume Game",
        "Restart Level", 
        "Main Menu"
        // ✅ REMOVED "Exit Game" - it's illogical
    };
    
    // Panel layout constants - ADJUST THESE TO CHANGE PANEL ALIGNMENT
    private static final int LEFT_PANEL_X = 35;           // Left panels start X position
    private static final int BOTTOM_PANEL_WIDTH = 180;    // Width of bottom panels
    private static final int BOTTOM_PANEL_HEIGHT = 70;    // Height of bottom panels
    
 // * GAME OVER POPUP PROPERTIES *
    private boolean gameOverButtonHovered = false;  // Track if "Return to Main Menu" is hovered
    
 // ADD these to new file's properties section:
    private boolean victoryPanelShown = false;  // Prevent multiple transitions

    // *** CONFIRMATION DIALOG PROPERTIES ***
    private boolean showingConfirmDialog = false;
    private String confirmDialogType = "";  // "RESTART" or "MAIN_MENU"
    private boolean confirmButtonHovered = false;
    private boolean cancelButtonHovered = false;
    
    // ==================== SECTION 2: CONSTRUCTOR ====================
    
    public GamePanel(GameWindow window) {
        System.out.println("=== INITIALIZING GAMEPANEL ===");
        
        // Setup panel properties
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        this.gameWindow = window;
        // * SETUP MOUSE LISTENERS FOR SETTINGS GEAR *
        // This enables clicking and hovering on the settings gear
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());  // Handle clicks
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseHover(e.getX(), e.getY());  // Handle hover effects
            }
        });
        
        // Load visual assets
        loadCharacterSprites();
        loadTileSprites();
        
        // Initialize game
        game = new GameManager();
        game.startNewGame();

    
        
        // Start rendering timer (60 FPS)
        int delay = 1000 / Constants.FPS; 
        this.gameTimer = new Timer(delay, e -> {
            if (game != null) {
                // ✅ FIX: Repaint even when game is over to show popup
                if (game.isGameRunning() || game.isPaused() || game.isShowingGameOver()) {
                    repaint();
                }
            }
        });
        this.gameTimer.start();
        
        running = true;
        startGameLoop();
        
     // ADD this line in constructor after gameWindow assignment:
        this.victoryPanelShown = false;
        
        System.out.println(" GamePanel initialized!");
        System.out.println("==============================");
    }
    
    
    // ==================== SECTION 3: GETTER ====================
    
    public GameManager getGameManager() {
        return game;
    }
    
 // ADD this method after getGameManager():
    public void stopTimers() {
        running = false;
        if (gameTimer != null) {
            gameTimer.stop();
        }
        System.out.println("🛑 GamePanel timers stopped");
    }
    
    // ==================== SECTION 4: SPRITE LOADING ====================
    
 // Find Section 4: SPRITE LOADING (around line 133)
 // Replace the entire loadCharacterSprites() method with this:

 // === START OF loadCharacterSprites() METHOD ===

    private void loadCharacterSprites() {
        try {
            // Initialize 4 separate arrays of 3 frames each
            sprites_down = new BufferedImage[3];
            sprites_right = new BufferedImage[3];
            sprites_up = new BufferedImage[3];
            sprites_left = new BufferedImage[3];
            
            System.out.println("=== LOADING SPRITES ===");
            
            // Load DOWN sprites (use getResource for JAR compatibility)
            sprites_down[0] = loadSpriteFromResource("/assets/Character/Character_2-1.png.png", "DOWN frame 0");
            sprites_down[1] = loadSpriteFromResource("/assets/Character/Character_2-2.png.png", "DOWN frame 1");
            sprites_down[2] = loadSpriteFromResource("/assets/Character/Character_2-3.png.png", "DOWN frame 2");
            
            // Load RIGHT sprites
            sprites_right[0] = loadSpriteFromResource("/assets/Character/Character_2-1.png.png", "RIGHT frame 0");
            sprites_right[1] = loadSpriteFromResource("/assets/Character/Character_2-2.png.png", "RIGHT frame 1");
            sprites_right[2] = loadSpriteFromResource("/assets/Character/Character_2-3.png.png", "RIGHT frame 2");
            
            // Load UP sprites
            sprites_up[0] = loadSpriteFromResource("/assets/Character/Character_2-1.png.png", "UP frame 0");
            sprites_up[1] = loadSpriteFromResource("/assets/Character/Character_2-4.png.png", "UP frame 1");
            sprites_up[2] = loadSpriteFromResource("/assets/Character/Character_2-4.png.png", "UP frame 2");
            
            // Load LEFT sprites
            sprites_left[0] = loadSpriteFromResource("/assets/Character/Character_2-1.png.png", "LEFT frame 0");
            sprites_left[1] = loadSpriteFromResource("/assets/Character/Character_2-5.png.png", "LEFT frame 1");
            sprites_left[2] = loadSpriteFromResource("/assets/Character/Character_2-6.png.png", "LEFT frame 2");
            
            System.out.println("✓ All directional sprites loaded successfully!\n");
            verifySpritesLoaded();
            
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR during sprite loading: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback initialization
            BufferedImage fallback = createFallbackSprite();
            sprites_down = new BufferedImage[]{fallback, fallback, fallback};
            sprites_up = new BufferedImage[]{fallback, fallback, fallback};
            sprites_left = new BufferedImage[]{fallback, fallback, fallback};
            sprites_right = new BufferedImage[]{fallback, fallback, fallback};
        }
    }

    /**
     * NEW METHOD: Loads sprite from resources folder (works in JAR)
     */
    private BufferedImage loadSpriteFromResource(String resourcePath, String description) {
        try {
            InputStream is = getClass().getResourceAsStream(resourcePath);
            
            if (is == null) {
                System.err.println("✗ NOT FOUND: " + resourcePath + " (" + description + ")");
                return createFallbackSprite();
            }
            
            BufferedImage image = ImageIO.read(is);
            System.out.println("✓ Loaded: " + resourcePath + " (" + description + ")");
            return image;
            
        } catch (Exception e) {
            System.err.println("✗ ERROR loading " + resourcePath + ": " + e.getMessage());
            return createFallbackSprite();
        }
    }
    /**
     * Helper method to load a single sprite file with error handling
     */
    private BufferedImage loadSpriteFile(String basePath, String fileName, String description) {
        try {
            File imgFile = new File(basePath + fileName);
            
            if (imgFile.exists()) {
                BufferedImage image = ImageIO.read(imgFile);
                System.out.println(" Loaded: " + fileName + " (" + description + ")");
                return image;
            } else {
                System.err.println(" NOT FOUND: " + fileName + " (" + description + ")");
                System.err.println("  Full path: " + imgFile.getAbsolutePath());
                return createFallbackSprite();
            }
        } catch (Exception e) {
            System.err.println(" ERROR loading " + fileName + ": " + e.getMessage());
            return createFallbackSprite();
        }
    }

    /**
     * Verifies that all sprite arrays are properly loaded
     */
    private void verifySpritesLoaded() {
        System.out.println("\n=== SPRITE VERIFICATION ===");
        
        System.out.print("DOWN array: ");
        if (sprites_down != null && sprites_down.length == 3) {
            System.out.println(" Length = 3");
            for (int i = 0; i < 3; i++) {
                System.out.println("  Frame " + i + ": " + (sprites_down[i] != null ? "OK" : "NULL"));
            }
        } else {
            System.out.println(" INVALID");
        }
        
        System.out.print("RIGHT array: ");
        if (sprites_right != null && sprites_right.length == 3) {
            System.out.println(" Length = 3");
            for (int i = 0; i < 3; i++) {
                System.out.println("  Frame " + i + ": " + (sprites_right[i] != null ? "OK" : "NULL"));
            }
        } else {
            System.out.println(" INVALID");
        }
        
        System.out.print("UP array: ");
        if (sprites_up != null && sprites_up.length == 3) {
            System.out.println(" Length = 3");
            for (int i = 0; i < 3; i++) {
                System.out.println("  Frame " + i + ": " + (sprites_up[i] != null ? "OK" : "NULL"));
            }
        } else {
            System.out.println(" INVALID");
        }
        
        System.out.print("LEFT array: ");
        if (sprites_left != null && sprites_left.length == 3) {
            System.out.println("✓ Length = 3");
            for (int i = 0; i < 3; i++) {
                System.out.println("  Frame " + i + ": " + (sprites_left[i] != null ? "OK" : "NULL"));
            }
        } else {
            System.out.println(" INVALID");
        }
        
        System.out.println("===========================\n");
    }

    // === END OF loadCharacterSprites() METHOD ===
    
    private void loadTileSprites() {
        createFallbackTiles();
    }
    
    private void createFallbackTiles() {
        int size = 35;
        
        startTileImage = createColoredTile(size, Constants.COLOR_START, "S");
        pathTileImage = createColoredTile(size, Constants.COLOR_PATH, null);
        wallTileImage = createBrickTile(size);
        clueTileImage = createTreasureBoxTile(size);
        hazardTileImage = createColoredTile(size, Constants.COLOR_HAZARD, ":::");
        exitTileImage = createColoredTile(size, Constants.COLOR_EXIT, "E");
        vanishedTileImage = createColoredTile(size, Constants.COLOR_VANISHED, null);
        
        System.out.println(" Placeholder tiles created");
    }
    
    private BufferedImage createColoredTile(int size, Color color, String text) {
        BufferedImage tile = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tile.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(color);
        g2d.fillRect(0, 0, size, size);
        
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawRect(0, 0, size - 1, size - 1);
        
        if (text != null) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (size - fm.stringWidth(text)) / 2;
            int textY = (size + fm.getAscent()) / 2 - 2;
            g2d.drawString(text, textX, textY);
        }
        
        g2d.dispose();
        return tile;
    }
    
    private BufferedImage createBrickTile(int size) {
        BufferedImage tile = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tile.createGraphics();
        
        g2d.setColor(Constants.COLOR_WALL);
        g2d.fillRect(0, 0, size, size);
        
        g2d.setColor(new Color(80, 40, 30));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, size/3, size, size/3);
        g2d.drawLine(0, 2*size/3, size, 2*size/3);
        g2d.drawLine(size/2, 0, size/2, size/3);
        g2d.drawLine(size/4, size/3, size/4, 2*size/3);
        g2d.drawLine(3*size/4, size/3, 3*size/4, 2*size/3);
        g2d.drawLine(size/2, 2*size/3, size/2, size);
        
        g2d.dispose();
        return tile;
    }
    
    private BufferedImage createTreasureBoxTile(int size) {
        BufferedImage tile = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tile.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Constants.COLOR_PATH);
        g2d.fillRect(0, 0, size, size);
        
        int boxSize = (int)(size * 0.7);
        int offset = (size - boxSize) / 2;
        
        g2d.setColor(new Color(200, 160, 80));
        g2d.fillRoundRect(offset, offset + boxSize/4, boxSize, boxSize*3/4, 4, 4);
        g2d.fillRoundRect(offset - 2, offset + boxSize/6, boxSize + 4, boxSize/4, 3, 3);
        
        g2d.setColor(new Color(120, 90, 40));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(offset, offset + boxSize/4, boxSize, boxSize*3/4, 4, 4);
        g2d.drawRoundRect(offset - 2, offset + boxSize/6, boxSize + 4, boxSize/4, 3, 3);
        
        g2d.setColor(new Color(80, 60, 30));
        int lockSize = boxSize / 5;
        g2d.fillOval(size/2 - lockSize/2, size/2 - lockSize/2, lockSize, lockSize);
        g2d.fillRect(size/2 - lockSize/4, size/2, lockSize/2, lockSize);
        
        g2d.dispose();
        return tile;
    }
    
    private BufferedImage createFallbackSprite() {
        BufferedImage fallback = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallback.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Constants.COLOR_PLAYER);
        g2d.fillOval(4, 4, 24, 24);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(4, 4, 24, 24);
        
        g2d.dispose();
        return fallback;
    }
    
    
    // ==================== SECTION 5: GAME LOOP ====================
    
    private void startGameLoop() {
        Timer logicTimer = new Timer(1000 / Constants.FPS, e -> {
            if (running) {
                game.update(0.016);
            }
        });
        logicTimer.start();
    }
    
    
    // ==================== SECTION 6: MAIN RENDERING ====================
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!running) return;

        // ABSOLUTE PRIORITY: Story Frame
        if (game.isShowingStory()) {
            drawStoryFrame(g);
            return;
        }

        // ✅ FIXED: Check for victory state - only transition ONCE
        if (game.isShowingVictory() && !victoryPanelShown) {
            System.out.println("🎉 VICTORY STATE DETECTED in paintComponent");
            System.out.println("🎉 Switching to Victory Panel...");

            victoryPanelShown = true;  // ✅ Set flag BEFORE transitioning

            if (gameWindow != null) {
                // Stop all timers FIRST to prevent further repaints
                stopTimers();
                
                javax.swing.SwingUtilities.invokeLater(() -> {
                    gameWindow.showVictoryScreen();
                });
            } else {
                System.err.println("❌ ERROR: gameWindow is null!");
            }
            return;
        }

        drawMaze(g);
        drawFogOfWar(g);
        drawPlayer(g);
        drawUI(g);
    }
  
    
    // ==================== SECTION 7: MAZE RENDERING ====================
    
    private void drawMaze(Graphics g) {
        Maze maze = game.getMaze();
        if (maze == null) return;
        
        int width = maze.getWidth();
        int height = maze.getHeight();
        
        for (int gridY = 0; gridY < height; gridY++) {
            for (int gridX = 0; gridX < width; gridX++) {
                Tile tile = maze.getTile(gridX, gridY);
                if (tile != null) {
                    drawTile(g, tile, gridX, gridY);
                }
            }
        }
    }
    
    private void drawTile(Graphics g, Tile tile, int gridX, int gridY) {
        int x = Constants.MAZE_OFFSET_X + (gridX * Constants.TILE_SIZE);
        int y = Constants.MAZE_OFFSET_Y + (gridY * Constants.TILE_SIZE);
        int size = Constants.TILE_SIZE;
        
        String type = tile.getType();
        BufferedImage tileImage = null;
        
        if (tile.isVanished()) {
            tileImage = vanishedTileImage;
        } else {
            switch (type) {
                case "START": tileImage = startTileImage; break;
                case "PATH": tileImage = pathTileImage; break;
                case "WALL": tileImage = wallTileImage; break;
                case "CLUE": tileImage = clueTileImage; break;
                case "HAZARD": tileImage = hazardTileImage; break;
                case "EXIT": tileImage = exitTileImage; break;
                default: tileImage = pathTileImage; break;
            }
        }
        
        if (tileImage != null) {
            g.drawImage(tileImage, x, y, size, size, null);
        } else {
            g.setColor(getTileColor(tile));
            g.fillRect(x, y, size, size);
        }
    }
    
    private Color getTileColor(Tile tile) {
        switch (tile.getType()) {
            case "PATH": return Constants.COLOR_PATH;
            case "WALL": return Constants.COLOR_WALL;
            case "CLUE": return Constants.COLOR_CLUE;
            case "HAZARD": return Constants.COLOR_HAZARD;
            case "EXIT": return Constants.COLOR_EXIT;
            case "START": return Constants.COLOR_START;
            default: return Color.GRAY;
        }
    }
    
    /**
     * Draws the fog of war effect - dims tiles outside player's vision range
     */
    private void drawFogOfWar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Maze maze = game.getMaze();
        Player player = game.getPlayer();
        
        if (maze == null || player == null) return;
        
        int playerX = player.getX();
        int playerY = player.getY();
        int width = maze.getWidth();
        int height = maze.getHeight();
        
        // Draw fog on tiles outside visibility range
        for (int gridY = 0; gridY < height; gridY++) {
            for (int gridX = 0; gridX < width; gridX++) {
                // Calculate distance from player (Chebyshev distance = square pattern)
            	int dx = gridX - playerX;
            	int dy = gridY - playerY;
            	double distance = Math.sqrt(dx * dx + dy * dy); 
                
                // If outside visibility range, apply fog
                if (distance > Constants.VISIBILITY_RANGE) {
                    int x = Constants.MAZE_OFFSET_X + (gridX * Constants.TILE_SIZE);
                    int y = Constants.MAZE_OFFSET_Y + (gridY * Constants.TILE_SIZE);
                    
                    g2d.setColor(Constants.COLOR_FOG);
                    g2d.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);
                }
            }
        }
    }
    
  // ==================== SECTION 8: PLAYER RENDERING ====================
    		
    private void drawPlayer(Graphics g) {
        Player player = game.getPlayer();
        if (player == null) return;
        
        int gridX = player.getX();
        int gridY = player.getY();
        
        int pixelX, pixelY;
        
        if (isAnimating) {
            long elapsed = System.currentTimeMillis() - animStartTime;
            float progress = Math.min(1.0f, (float)elapsed / ANIM_DURATION);
            // Use an easing function (cubed in this case) for smooth movement
            progress = (float)(1 - Math.pow(1 - progress, 3)); 
            
            pixelX = (int)(animStartX + (animTargetX - animStartX) * progress);
            pixelY = (int)(animStartY + (animTargetY - animStartY) * progress);
            
            // CRITICAL FIX: Only set isMoving to true DURING animation
            isMoving = true;
            updateAnimationFrame1();
            
            if (progress >= 1.0f) {
                isAnimating = false;
                isMoving = false;
                currentFrame = 0; // Reset to idle frame
            }
        } else {
            pixelX = Constants.MAZE_OFFSET_X + (gridX * Constants.TILE_SIZE);
            pixelY = Constants.MAZE_OFFSET_Y + (gridY * Constants.TILE_SIZE);
            // Ensure isMoving is false when not animating
            isMoving = false; 
            currentFrame = 0; // ADDED: Force idle frame when not moving
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        // 1. DETERMINE WHICH SPRITE ARRAY TO USE
        BufferedImage[] activeSprites;
        
        switch (lastDirection) {
            case "up":
                activeSprites = sprites_up;
                break;
            case "down":
                activeSprites = sprites_down;
                break;
            case "left":
                activeSprites = sprites_left;
                break;
            case "right":
                activeSprites = sprites_right;
                break;
            default:
                // This happens when the game starts, before any movement.
                activeSprites = sprites_down; 
                lastDirection = "down"; // ADDED: Ensure lastDirection is set
                break;
        }
        
        // Check for null array (Error Prevention)
        if (activeSprites == null) {
            System.err.println("ERROR: Sprite array for direction '" + lastDirection + "' is null!");
            System.err.println("DEBUG: Check loadCharacterSprites() initialization");
            return; // Don't draw if array is null
        }

        // 2. GET THE CURRENT FRAME INDEX
        // CRITICAL FIX: Always use frame 0 when idle, cycle frames only when moving
        int frameIndex;
        if (isMoving) {
            // Cycle through frames 0, 1, 2 during movement
            frameIndex = currentFrame;
        } else {
            // Always show frame 0 (idle pose) when stationary
            frameIndex = 0;
        }
        
        // Check for invalid frame index (Error Prevention)
        if (frameIndex < 0 || frameIndex >= activeSprites.length) {
            System.err.println("ERROR: Invalid frame index " + frameIndex + " for direction '" + lastDirection + "'");
            System.err.println("       Array length: " + activeSprites.length);
            frameIndex = 0; // Fallback to frame 0
        }
        
        // Additional null check for the specific frame
        if (activeSprites[frameIndex] == null) {
            System.err.println("ERROR: Sprite at index " + frameIndex + " is null for direction '" + lastDirection + "'");
            System.err.println("       File may not have loaded correctly!");
            // Try to use frame 0 as fallback
            if (frameIndex != 0 && activeSprites[0] != null) {
                frameIndex = 0;
            } else {
                return; // Can't draw anything, skip
            }
        }
        
        // 3. DRAW THE SPRITE
        BufferedImage currentSprite = activeSprites[frameIndex];
        
        g2d.drawImage(currentSprite, pixelX, pixelY, 
                     Constants.TILE_SIZE, Constants.TILE_SIZE, null);
        
        if (Constants.DRAW_PLAYER_SHADOW) {
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillOval(pixelX + 6, pixelY + Constants.TILE_SIZE - 6, 
                        Constants.TILE_SIZE - 12, 6);
        }
    }

    private void updateAnimationFrame1() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= FRAME_DELAY) {
            if (isMoving) {
                // CRITICAL FIX: Cycle only through the 3 frames (0, 1, 2)
                currentFrame = (currentFrame + 1) % 3; 
            } else {
                // CRITICAL FIX: When not moving, always reset to frame 0
                currentFrame = 0;
            }
            lastFrameTime = currentTime;
        }
    }

    		private void updateAnimationFrame() {
    		    long currentTime = System.currentTimeMillis();
    		    if (currentTime - lastFrameTime >= FRAME_DELAY) {
    		        if (isMoving) {
    		            // CRITICAL FIX: Cycle only through the 3 frames (0, 1, 2)
    		            currentFrame = (currentFrame + 1) % 3; 
    		        } else {
    		            // CRITICAL FIX: When not moving, always reset to frame 0
    		            currentFrame = 0;
    		        }
    		        lastFrameTime = currentTime;
    		    }
    		}
    
    
   // ==================== SECTION 9: KEYBOARD INPUT (FIXED) ====================
   

    		public void keyPressed(KeyEvent e) {
    		    int key = e.getKeyCode();

    		    // PRIORITY 0: Block ALL input if game over
    		    if (game.isShowingGameOver()) {
    		        System.out.println("⚠️ Input blocked - game over screen active");
    		        return;
    		    }

    		    // PRIORITY 0.5: Story Frame input
    		    if (game.isShowingStory()) {
    		        System.out.println("📖 Key pressed during story: " + KeyEvent.getKeyText(key));
    		        
    		        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
    		            System.out.println("⏩ SPACE/ENTER detected - calling handleStorySkip()");
    		            
    		            // ✅ Check current story frame BEFORE skipping
    		            int currentFrameBeforeSkip = game.getCurrentStoryFrameIndex();
    		            System.out.println("🔍 Current story frame BEFORE skip: " + currentFrameBeforeSkip);
    		            
    		            game.handleStorySkip();
    		            
    		            // ✅ CRITICAL: After skipping, check if we're in victory state
    		            if (!game.isShowingStory() && game.isShowingVictory()) {
    		                System.out.println("🎉 Story ended - Victory state detected!");
    		                System.out.println("🎉 paintComponent will handle transition to Victory Panel");
    		                repaint();
    		                return;  // ✅ EXIT - Don't process any other logic
    		            }
    		            
    		            // Check if story ended but game should continue (levels 1-4)
    		            if (!game.isShowingStory() && !game.isShowingVictory() && game.isGameRunning()) {
    		                System.out.println("➡️ Story ended - Continuing to level " + game.getCurrentLevel());
    		                repaint();
    		                return;
    		            }
    		            
    		            repaint();
    		            
    		        } else if (key == KeyEvent.VK_ESCAPE) {
    		            System.out.println("⏩ ESC detected - skipping all story");
    		            game.skipAllStory();
    		            
    		            // ✅ Check for victory after skipping
    		            if (game.isShowingVictory()) {
    		                System.out.println("🎉 All story skipped - Victory state active!");
    		                repaint();
    		                return;  // ✅ EXIT immediately
    		            }
    		            
    		            int currentLevel = game.getCurrentLevel();
    		            if (currentLevel == 0) {
    		                game.loadLevel(1);
    		            } else if (currentLevel < game.getMaxLevel()) {
    		                game.loadLevel(currentLevel + 1);
    		            }
    		            
    		            repaint();
    		        }
    		        
    		        return;  // ✅ CRITICAL: Don't process other inputs during story
    		    }

    		    // PRIORITY 1: Final Challenge Feedback
    		    if (game.isShowingFinalFeedback()) {
    		        System.out.println("⚠️ Input blocked - showing final challenge result");
    		        return;
    		    }

    		    // PRIORITY 2: Answer Feedback
    		    if (game.isShowingAnswerFeedback()) {
    		        System.out.println("⚠️ Input blocked - showing answer feedback");
    		        return;
    		    }

    		    // PRIORITY 3: Handle Final Challenge input
    		    if (game.getCurrentGameState().equals(GameManager.STATE_FINAL_CHALLENGE)) {
    		        handleFinalChallengeInput(e);
    		        return;
    		    }

    		    // PRIORITY 4: Handle Settings Menu input
    		    if (settingsMenuOpen) {
    		        handleSettingsInput(key);
    		        return;
    		    }

    		    // PRIORITY 5: Handle question answers
    		    if (game.isWaitingForAnswer()) { 
    		        if (key == KeyEvent.VK_A || key == KeyEvent.VK_B || 
    		            key == KeyEvent.VK_C || key == KeyEvent.VK_D) {
    		            
    		            char selectedAnswer = (char) key; 
    		            System.out.println("[GamePanel] Submitting answer: " + selectedAnswer);
    		            game.submitAnswer(selectedAnswer);
    		            repaint();
    		        }
    		        return;
    		    }

    		    // PRIORITY 6: Normal gameplay - movement controls
    		    
    		    // Block input while animation is running
    		    if (isAnimating) {
    		        return; 
    		    }
    		    
    		    long currentTime = System.currentTimeMillis();
    		    
    		    // Block input if a move was just executed (anti-key spam)
    		    if (currentTime - lastMoveTime < MOVE_DELAY_MS) {
    		        return;
    		    }
    		    
    		    int currentX = game.getPlayerX();
    		    int currentY = game.getPlayerY();
    		    
    		    int newX = currentX;
    		    int newY = currentY;
    		    String direction = lastDirection;
    		    
    		    switch (key) {
    		        case KeyEvent.VK_W:
    		        case KeyEvent.VK_UP:
    		            newY = currentY - 1;
    		            direction = "up";
    		            break;
    		            
    		        case KeyEvent.VK_S:
    		        case KeyEvent.VK_DOWN:
    		            newY = currentY + 1;
    		            direction = "down";
    		            break;
    		            
    		        case KeyEvent.VK_A:
    		        case KeyEvent.VK_LEFT:
    		            newX = currentX - 1;
    		            direction = "left";
    		            break;
    		            
    		        case KeyEvent.VK_D:
    		        case KeyEvent.VK_RIGHT:
    		            newX = currentX + 1;
    		            direction = "right";
    		            break;
    		            
    		        case KeyEvent.VK_P:
    		        case KeyEvent.VK_ESCAPE:
    		            // Only allow pause if:
    		            // - Game is running
    		            // - NOT showing game over
    		            // - NOT waiting for answer
    		            // - NOT showing feedback
    		            if (game.isGameRunning() && 
    		                !game.isShowingGameOver() && 
    		                !game.isWaitingForAnswer() && 
    		                !game.isShowingAnswerFeedback() &&
    		                !game.isShowingFinalFeedback()) {
    		                game.togglePause();
    		                repaint();
    		            }
    		            return;
    		            
    		        default:
    		            return;
    		    }
    		    
    		    // Process movement
    		    if (newX != currentX || newY != currentY) {
    		        boolean moved = game.movePlayer(newX, newY);
    		        
    		        if (moved) {
    		            lastDirection = direction;
    		            lastMoveTime = currentTime;
    		            
    		            animStartX = Constants.MAZE_OFFSET_X + (currentX * Constants.TILE_SIZE);
    		            animStartY = Constants.MAZE_OFFSET_Y + (currentY * Constants.TILE_SIZE);
    		            animTargetX = Constants.MAZE_OFFSET_X + (newX * Constants.TILE_SIZE);
    		            animTargetY = Constants.MAZE_OFFSET_Y + (newY * Constants.TILE_SIZE);
    		            animStartTime = System.currentTimeMillis();
    		            
    		            currentFrame = 0;
    		            isAnimating = true;
    		            isMoving = true;
    		            
    		            System.out.println("DEBUG: Moving " + direction + " - Frame reset to 0");
    		        }
    		    }
    		}
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    
    // ==================== SECTION 10: MOUSE INPUT HANDLERS ====================
   
    
    /**
     * Main mouse click handler - routes clicks to appropriate handlers
     * ADJUST THIS: Add more click handlers here for other UI elements
     */
    
    private void handleMouseClick(int mouseX, int mouseY) {
        // PRIORITY 1: Confirmation dialog clicks (highest priority)
        if (showingConfirmDialog) {
            handleConfirmDialogClick(mouseX, mouseY);
            return;  // Exit immediately after handling
        }
        
        // Handle game over "Return to Main Menu" button click
        if (game.isShowingGameOver()) {
            handleGameOverClick(mouseX, mouseY);
            return;
        }
        
        // Handle settings menu option clicks (when menu is open)
        if (settingsMenuOpen) {
            handleSettingsMenuClick(mouseX, mouseY);
            return;
        }
        
        // Check if settings gear was clicked (when menu is NOT open)
        if (isPointInRect(mouseX, mouseY, settingsX, settingsY, settingsSize, settingsSize)) {
            openSettingsMenu();
        }
    }

    /**
     * Handles clicks on settings menu options
     * This is called when the settings menu is open and user clicks an option
     */
    private void handleSettingsMenuClick(int mouseX, int mouseY) {
        int dw = 750;
        int dx = (Constants.SCREEN_WIDTH - dw) / 2;
        int dy = (Constants.SCREEN_HEIGHT - 480) / 2;
        int optionY = dy + 210;  // ✅ FIXED: was 160
        int optionHeight = 48;   // ✅ FIXED: was 50
        int optionSpacing = 10;  // ✅ FIXED: was 12
        
        for (int i = 0; i < settingOptions.length; i++) {
            int oy = optionY + i * (optionHeight + optionSpacing);
            
            if (mouseX >= dx + 50 && mouseX <= dx + dw - 50 &&
                mouseY >= oy && mouseY <= oy + optionHeight) {
                
                System.out.println("🖱️ Menu option " + i + " clicked: " + settingOptions[i]);
                
                switch (i) {
                    case 0: // Resume Game
                        System.out.println("▶️ Resume Game");
                        closeSettingsMenu();
                        game.resumeGame();
                        break;
                        
                    case 1: // Restart Level
                        System.out.println("🔄 Restart Level - showing confirmation");
                        confirmDialogType = "RESTART";
                        showingConfirmDialog = true;
                        settingsMenuOpen = false;
                        hoveredSettingOption = -1;
                        repaint();
                        break;
                        
                    case 2: // Main Menu
                        System.out.println("🏠 Main Menu - showing confirmation");
                        confirmDialogType = "MAIN_MENU";
                        showingConfirmDialog = true;
                        settingsMenuOpen = false;
                        hoveredSettingOption = -1;
                        repaint();
                        break;
                    // ✅ case 3 (Exit Game) REMOVED
                }
                
                break;
            }
        }
    }
    
    /**
     * Main mouse hover handler - provides visual feedback
     * ADJUST THIS: Add more hover handlers here for other UI elements
     */
    private void handleMouseHover(int mouseX, int mouseY) {
        // PRIORITY 1: Confirmation dialog hover
        if (showingConfirmDialog) {
            checkConfirmDialogHover(mouseX, mouseY);
            return;
        }
        
        // Check game over button hover
        if (game.isShowingGameOver()) {
            checkGameOverHover(mouseX, mouseY);
            return;
        }
        
        // Check settings menu hover (when menu is open)
        if (settingsMenuOpen) {
            checkSettingsHover(mouseX, mouseY);
            return;
        }
        
        // Check settings gear hover (normal gameplay)
        boolean wasHovered = settingsHovered;
        settingsHovered = isPointInRect(mouseX, mouseY, settingsX, settingsY, settingsSize, settingsSize);
        
        if (settingsHovered) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
        
        if (wasHovered != settingsHovered) {
            repaint();
        }
    }

    /**
     * Checks if mouse is hovering over settings menu options
     */
    private void checkSettingsHover(int mouseX, int mouseY) {
        int dw = 750;
        int dx = (Constants.SCREEN_WIDTH - dw) / 2;
        int dy = (Constants.SCREEN_HEIGHT - 480) / 2;
        int optionY = dy + 210;      // ✅ FIXED: matches drawing
        int optionHeight = 48;       // ✅ FIXED: matches drawing
        int optionSpacing = 10;      // ✅ FIXED: matches drawing
        
        int oldHover = hoveredSettingOption;
        hoveredSettingOption = -1;
        
        for (int i = 0; i < settingOptions.length; i++) {
            int oy = optionY + i * (optionHeight + optionSpacing);
            
            if (mouseX >= dx + 60 && mouseX <= dx + dw - 60 &&  // ✅ Exact match with drawing
                mouseY >= oy && mouseY <= oy + optionHeight) {
                hoveredSettingOption = i;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;
            }
        }
        
        if (hoveredSettingOption == -1) {
            setCursor(Cursor.getDefaultCursor());
        }
        
        if (oldHover != hoveredSettingOption) {
            repaint();
        }
    }
    
    /**
     * Checks if mouse is hovering over question choices
     * OPTIONAL: Add this feature if you want hover effects on answer choices
     */
    private void checkChoiceHover(int mouseX, int mouseY) {
        // TODO: Implement hover effect for question choices if desired
        // This would require adding a hoveredChoiceIndex field
    }

    /**
     * Helper method to check if a point is inside a rectangle
     * UTILITY: Use this for all click/hover detection
     */
    private boolean isPointInRect(int px, int py, int rx, int ry, int rw, int rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    
    // ==================== SECTION 11: SETTINGS MENU LOGIC ====================
    // * THIS SECTION CONTROLS SETTINGS MENU BEHAVIOR *
    
    /**
     * Opens the settings menu
     * CALLED BY: Mouse click on settings gear
     */
    private void openSettingsMenu() {
        System.out.println("Settings button clicked!");
        settingsMenuOpen = true;
        hoveredSettingOption = -1;
        
        // *** ADD THIS: Tell GameManager settings are open ***
        game.setSettingsMenuOpen(true);
        
        repaint();
    }

    /**
     * Closes the settings menu
     * CALLED BY: ESC key, menu selections, or clicking outside
     */
    private void closeSettingsMenu() {
        settingsMenuOpen = false;
        hoveredSettingOption = -1;
        repaint();
        requestFocusInWindow();
    }
    
    /**
     * Handles keyboard/click input for settings menu
     * UPDATED: Now connects to Main Menu and Exit
     */
    private void handleSettingsInput(int key) {
        if (showingConfirmDialog) {
            if (key == KeyEvent.VK_ESCAPE) {
                System.out.println("🔙 ESC pressed - closing confirmation dialog");
                showingConfirmDialog = false;
                confirmDialogType = "";
                settingsMenuOpen = true;
                repaint();
            }
            return;
        }
        
        switch (key) {
            case KeyEvent.VK_1:
            case KeyEvent.VK_NUMPAD1:
                closeSettingsMenu();
                game.resumeGame();
                break;
                
            case KeyEvent.VK_2:
            case KeyEvent.VK_NUMPAD2:
                System.out.println("🔄 Restart Level selected - showing confirmation");
                confirmDialogType = "RESTART";
                showingConfirmDialog = true;
                settingsMenuOpen = false;
                repaint();
                break;
                
            case KeyEvent.VK_3:
            case KeyEvent.VK_NUMPAD3:
                System.out.println("🏠 Main Menu selected - showing confirmation");
                confirmDialogType = "MAIN_MENU";
                showingConfirmDialog = true;
                settingsMenuOpen = false;
                repaint();
                break;
            // ✅ case KeyEvent.VK_4 (Exit Game) REMOVED
                
            case KeyEvent.VK_ESCAPE:
                closeSettingsMenu();
                game.resumeGame();
                break;
        }
    }
    // ==================== SECTION 12: UI RENDERING COORDINATOR ====================
    
    /**
     * Main UI coordinator - determines what to draw based on game state
     * ORDER: Base panels → Settings menu → Question dialog → Pause screen
     */
    private void drawUI(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // PRIORITY 1: Game Over (blocks everything)
        if (game.isShowingGameOver()) {
            drawGameOverPopup(g2d);
            return;
        }
        
        // PRIORITY 2: Final Challenge Feedback
        if (game.isShowingFinalFeedback()) {
            drawFinalChallengeResultPopup(g2d);
            game.updateFinalFeedback();
            return;
        }
        
        // PRIORITY 3: Answer Feedback (draw over base UI)
        if (game.isShowingAnswerFeedback()) {
            drawTopPanels(g2d);
            drawAnswerFeedbackPopup(g2d);
            return;  // *** EXIT - don't draw pause menu ***
        }
        
        // PRIORITY 4: Final Challenge Dialog
        if (game.getCurrentGameState().equals(GameManager.STATE_FINAL_CHALLENGE)) {
            drawTopPanels(g2d);
            drawFinalChallengeDialog(g);
            return;
        }
        
        // PRIORITY 5: Confirmation Dialog (NEW!)
        if (showingConfirmDialog) {
            drawTopPanels(g2d);
            drawConfirmDialog(g2d);
            return;
        }
        
        // PRIORITY 6: Settings Menu
        if (settingsMenuOpen) {
            drawTopPanels(g2d);
            drawSettingsMenu(g2d);
            return;
        }
        
        // PRIORITY 7: Question Dialog
        if (game.isWaitingForAnswer()) {
            drawTopPanels(g2d);
            drawQuestionBox(g2d);
            return;
        }
        
        // PRIORITY 8: Normal UI with warning and mercy popup
        drawTopPanels(g2d);
        drawWarningPopup(g2d);
        drawMercyDeactivatedPopup(g2d);
        
        // PRIORITY 9: Pause Menu (only if explicitly paused)
        if (game.isPaused() && game.isGameRunning() && !game.isShowingAnswerFeedback()) {
            drawPauseMenu(g2d);
        }
    }

    
    
    // ==================== SECTION 13: TOP PANEL RENDERING ====================
    // * ADJUST PANEL POSITIONS AND SIZES HERE *
    
 // * SINGLE TOP BAR WITH ALL INFO *

    private void drawTopPanels(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // === SINGLE TOP BAR BACKGROUND ===
        int barHeight = 90;
        int barY = 0;
        int barWidth = Constants.SCREEN_WIDTH;
        
        // Draw full-width bar background
        g2d.setColor(new Color(101, 67, 33));
        g2d.fillRect(0, barY, barWidth, barHeight);
        
        // Bottom border
        g2d.setColor(new Color(255, 204, 102));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, barHeight, barWidth, barHeight);
        
        // === LEFT SECTION: Collected Letters ===
        int leftX = 110;
        int sectionY = 15;
        
        // Label
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(248, 248, 248));
        g2d.drawString("Collected letter", leftX, sectionY + 15);
        
        // Clues count
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String clueText = "Clues: " + game.getCollectedClueCount() + " / " + game.getTotalClueCount();
        g2d.drawString(clueText, leftX, sectionY + 35);
        
        // Letters display
        ArrayList<Character> letters = game.getCollectedLetters();
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        
        int letterX = leftX;
        int letterY = sectionY + 60;
        int letterSpacing = 26;
        
        for (int i = 0; i < game.getTotalClueCount(); i++) {
            if (i < letters.size()) {
                g2d.setColor(new Color(255, 215, 0));
                g2d.drawString(String.valueOf(letters.get(i)), letterX, letterY);
            } else {
                g2d.setColor(new Color(100, 90, 80));
                g2d.drawString("_", letterX, letterY);
            }
            letterX += letterSpacing;
        }
        
        // === CENTER SECTION: TIME ===
        int centerX = barWidth / 2 - 100;
        
        // Label
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(248, 248, 248));
        String timeLabel = "Time";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(timeLabel, centerX + (200 - fm.stringWidth(timeLabel))/2, sectionY + 35);
        
        // Level display
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String levelText = "Level " + game.getCurrentLevel() + " / " + game.getMaxLevel();
        fm = g2d.getFontMetrics();
        g2d.drawString(levelText, centerX + (200 - fm.stringWidth(levelText))/2, sectionY + 15);
        
        // Time value
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(new Color(255, 215, 0));
        String time = game.getFormattedTime();
        fm = g2d.getFontMetrics();
        g2d.drawString(time, centerX + (200 - fm.stringWidth(time))/2, sectionY + 60);
        
        // === RIGHT SECTION: Hearts ===
        int rightX = barWidth - 280;
        
        // Label
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(248, 248, 248));
        g2d.drawString("Hearts", rightX, sectionY + 15);
        
        // Mercy status
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String mercyText = "Mercy status: " + (game.isMercyActive() ? "Active" : "Inactive");
        Color mercyColor = game.isMercyActive() ? new Color(255, 215, 0) : new Color(150, 60, 60);
        g2d.setColor(mercyColor);
        g2d.drawString(mercyText, rightX, sectionY + 35);
        
        // Hearts display
        drawHearts(g2d, rightX, sectionY + 45);
        
        // === SETTINGS GEAR ===
        settingsX = barWidth - 100;
        settingsY = (barHeight - settingsSize) / 2;
        drawSettingsGear(g2d, settingsX, settingsY);
    }
    
    
    // ==================== SECTION 14: BOTTOM PANEL RENDERING ====================
    // don't remove 
    private void drawBottomPanels(Graphics g) {
       
    }
    
    
    // ==================== SECTION 15: UI COMPONENT DRAWING ====================
    
    /**
     * Draws a standard panel background with label
     * ADJUST: Modify colors, fonts, or styling here
     */
    private void drawPanel(Graphics2D g2d, int x, int y, int w, int h, String label) {
        // Background
        g2d.setColor(new Color(165, 145, 125));
        g2d.fillRoundRect(x, y, w, h, 8, 8);
        
        // Border
        g2d.setColor(new Color(80, 70, 60));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, w, h, 8, 8);
        
        // Label
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(new Color(40, 35, 30));
        FontMetrics fm = g2d.getFontMetrics();
        
        int labelX = x + (w - fm.stringWidth(label)) / 2;
        int labelY = y + 18;
        
        g2d.drawString(label, labelX, labelY);
    }
    
    /**
     * Draws heart icons for player health
     */
    private void drawHearts(Graphics2D g2d, int x, int y) {
        int current = game.getPlayerHearts();
        int max = game.getMaxHearts();
        int size = 20;
        int spacing = 28;
        
        for (int i = 0; i < max; i++) {
            Color color = (i < current) ? new Color(220, 40, 40) : new Color(100, 90, 80);
            drawHeart(g2d, x + (i * spacing), y, size, color, i < current);
        }
    }
    
    /**
     * Draws a single heart icon
     */
    private void drawHeart(Graphics2D g2d, int x, int y, int size, Color color, boolean filled) {
        g2d.setColor(color);
        int cs = size / 2;
        
        if (filled) {
            g2d.fillOval(x, y, cs, cs);
            g2d.fillOval(x + cs, y, cs, cs);
            int[] xp = {x, x + size, x + size/2};
            int[] yp = {y + cs/2, y + cs/2, y + size};
            g2d.fillPolygon(xp, yp, 3);
        } else {
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, cs, cs);
            g2d.drawOval(x + cs, y, cs, cs);
            int[] xp = {x, x + size, x + size/2};
            int[] yp = {y + cs/2, y + cs/2, y + size};
            g2d.drawPolygon(xp, yp, 3);
        }
    }
    
    /**
     * Draws the settings gear button
     * * ADJUST: Change colors, size, or icon design here *
     */
    private void drawSettingsGear(Graphics2D g2d, int x, int y) {
        int size = settingsSize;  // Use the settingsSize property

        // Background with hover effect
        if (settingsHovered) {
            g2d.setColor(new Color(185, 165, 145));  // Lighter when hovered
        } else {
            g2d.setColor(new Color(165, 145, 125));  // Normal color
        }
        g2d.fillRoundRect(x, y, size, size, 8, 8);

        // Border
        g2d.setColor(new Color(101, 67, 33));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, size, size, 8, 8);

        // Gear icon - proper gear shape
        int cx = x + size/2;
        int cy = y + size/2;
        g2d.setColor(new Color(101, 67, 33));
        
        // Enable anti-aliasing for smoother gear
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw gear teeth (8 teeth around the circle)
        int outerRadius = 12;
        int innerRadius = 8;
        int centerRadius = 4;
        
        Polygon gear = new Polygon();
        for (int i = 0; i < 16; i++) {
            double angle = Math.toRadians(i * 22.5);
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            int px = cx + (int)(radius * Math.cos(angle));
            int py = cy + (int)(radius * Math.sin(angle));
            gear.addPoint(px, py);
        }
        g2d.fill(gear);

        // Center hole
        g2d.setColor(settingsHovered ? new Color(101, 67, 33) : new Color(165, 145, 125));
        g2d.fillOval(cx - centerRadius, cy - centerRadius, centerRadius * 2, centerRadius * 2);

        // Hover highlight effect
        if (settingsHovered) {
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(x - 2, y - 2, size + 4, size + 4, 10, 10);
        }
    }
    
    // ==================== SECTION 16: SETTINGS MENU RENDERING ====================
    // * THIS DRAWS THE SETTINGS MENU OVERLAY *
    
    /**
     * Draws the settings menu using the same UI style as question dialog
     * ADJUST: Change menu dimensions, colors, or layout here
     */
    private void drawSettingsMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dialog dimensions
        int dw = 750;
        int dh = 480;
        int dx = (Constants.SCREEN_WIDTH - dw) / 2;
        int dy = (Constants.SCREEN_HEIGHT - dh) / 2;
        
        // ==========================================================
        // DARK OVERLAY
        // ==========================================================
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        
        // ==========================================================
        // CREAM BACKGROUND (different shade from quiz)
        // ==========================================================
        Color creamBg = new Color(245, 235, 220); // Warmer cream
        g2d.setColor(creamBg);
        g2d.fillRoundRect(dx, dy, dw, dh, 20, 20);
        
        // Inner shadow for depth
        g2d.setColor(new Color(220, 210, 195));
        g2d.fillRoundRect(dx + 2, dy + 2, dw - 4, dh - 4, 18, 18);
        g2d.setColor(creamBg);
        g2d.fillRoundRect(dx + 2, dy + 2, dw - 4, dh - 4, 18, 18);
        
        // ==========================================================
        // BORDER (rich brown)
        // ==========================================================
        g2d.setColor(new Color(90, 70, 50)); // Rich brown border
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(dx, dy, dw, dh, 20, 20);
        
        // Inner border highlight (YELLOW)
        g2d.setColor(new Color(255, 215, 0)); // Golden yellow
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(dx + 2, dy + 2, dw - 4, dh - 4, 19, 19);
        
        // ==========================================================
        // TITLE BAR (dark brown)
        // ==========================================================
        Color titleBarColor = new Color(80, 60, 45);
        g2d.setColor(titleBarColor);
        g2d.fillRoundRect(dx + 20, dy + 20, dw - 40, 70, 15, 15);
        
        // Title bar border (brown outer)
        g2d.setColor(new Color(60, 45, 35));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(dx + 20, dy + 20, dw - 40, 70, 15, 15);
        
        // Title bar inner border (YELLOW)
        g2d.setColor(new Color(255, 215, 0)); // golden yellow
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(dx + 23, dy + 23, dw - 46, 64, 13, 13);
        
        // Title text (different font - Verdana)
        g2d.setFont(new Font("Verdana", Font.BOLD, 30));
        g2d.setColor(new Color(255, 240, 200)); // Light cream
        String title = "SETTINGS MENU";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = dx + (dw - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, dy + 65);
        
        // ==========================================================
        // INFO TEXT
        // ==========================================================
        int infoAreaY = dy + 140;
        g2d.setFont(new Font("Verdana", Font.ITALIC, 18));
        g2d.setColor(new Color(100, 80, 60)); // Medium brown
        String infoLine1 = "Game Paused";
        fm = g2d.getFontMetrics();
        g2d.drawString(infoLine1, dx + (dw - fm.stringWidth(infoLine1)) / 2, infoAreaY);
        
        g2d.setFont(new Font("Verdana", Font.PLAIN, 14));
        String infoLine2 = "Select an option below:";
        fm = g2d.getFontMetrics();
        g2d.drawString(infoLine2, dx + (dw - fm.stringWidth(infoLine2)) / 2, infoAreaY + 25);
        
        // ==========================================================
        // OPTIONS BUTTONS (brown and cream style)
        // ==========================================================
        int optionY = dy + 210;  // ✅ Start position
        int optionHeight = 48;   // ✅ Button height
        int optionSpacing = 10;  // ✅ Space between buttons
        
        g2d.setFont(new Font("Verdana", Font.BOLD, 16));
        
        // Draw each option
        for (int i = 0; i < settingOptions.length; i++) {
            String optionText = "(" + (i + 1) + ") " + settingOptions[i];
            
            // Button background (cream with brown on hover)
            if (i == hoveredSettingOption) {
                g2d.setColor(new Color(200, 180, 160)); // Warmer cream on hover
            } else {
                g2d.setColor(new Color(240, 230, 215)); // Light cream
            }
            g2d.fillRoundRect(dx + 60, optionY, dw - 120, optionHeight, 10, 10);
            
            // Button border (brown)
            g2d.setColor(new Color(90, 70, 50));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(dx + 60, optionY, dw - 120, optionHeight, 10, 10);
            
            // Button highlight
            if (i == hoveredSettingOption) {
                g2d.setColor(new Color(120, 100, 80, 80));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(dx + 61, optionY + 1, dw - 122, optionHeight - 2, 9, 9);
            }
            
            // Option text (dark brown)
            g2d.setColor(new Color(60, 45, 35));
            g2d.drawString(optionText, dx + 80, optionY + 32);
            
            optionY += optionHeight + optionSpacing;
        }
        
        // ==========================================================
        // INSTRUCTIONS (bottom)
        // ==========================================================
        g2d.setFont(new Font("Verdana", Font.ITALIC, 12));
        g2d.setColor(new Color(100, 80, 60));
        String inst = "Click an option or press 1-3 • ESC to close";
        fm = g2d.getFontMetrics();
        g2d.drawString(inst, dx + (dw - fm.stringWidth(inst)) / 2, dy + dh - 20);
    }
    
    
    // ==================== SECTION 17: QUESTION DIALOG RENDERING ====================
    
    /**
     * Draws the question dialog when player encounters a clue box
     */
    private void drawQuestionBox(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        QuestionManager.Question q = game.getCurrentQuestion();

        if (q == null || !game.isWaitingForAnswer()) {
            return;
        }

        // Dialog dimensions
        int dw = 700, dh = 450;
        int dx = (Constants.SCREEN_WIDTH - dw) / 2;
        int dy = (Constants.SCREEN_HEIGHT - dh) / 2;

        // ==========================================================
        // DARK OVERLAY
        // ==========================================================
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        // ==========================================================
        // SOFT YELLOW BACKGROUND (New aesthetic shade)
        // ==========================================================
        Color lightCream = new Color(255, 244, 210); // soft yellow-cream
        g2d.setColor(lightCream);
        g2d.fillRoundRect(dx, dy, dw, dh, 25, 25);

        // ==========================================================
        // BORDER (Your requested #C48A3E tone)
        // ==========================================================
        g2d.setColor(new Color(101, 67, 33));  // #C48A3E
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRoundRect(dx, dy, dw, dh, 25, 25);

        // ==========================================================
        // TITLE BAR (enhanced)
        // ==========================================================
        Color titleBrown = new Color(101, 67, 33);
        g2d.setColor(titleBrown);
        g2d.fillRoundRect(dx + 15, dy + 15, dw - 30, 65, 18, 18);

        // TITLE TEXT
        g2d.setFont(new Font("Serif", Font.BOLD, 34));
        g2d.setColor(new Color(255, 204, 102));
        String title = "CLUE BOX DISCOVERED!";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, dx + (dw - fm.stringWidth(title)) / 2, dy + 58);

        // ==========================================================
        // QUESTION TEXT (Better spacing)
        // ==========================================================
        g2d.setFont(new Font("Georgia", Font.BOLD | Font.ITALIC, 30));
        g2d.setColor(new Color(50, 40, 30));
        drawWrappedText(g2d, q.getQuestionText(), dx + 45, dy + 132, dw - 90);

        // ==========================================================
        // ANSWER CHOICES
        // ==========================================================
        String[] choices = q.getChoices();
        int cy = dy + 210;

        g2d.setFont(new Font("Arial", Font.BOLD, 17));

        for (int i = 0; i < choices.length; i++) {
            char key = (char) ('A' + i);
            String choiceText = "(" + key + ") " + choices[i];

            // Button background (light cream)
            g2d.setColor(new Color(255, 251, 238));
            g2d.fillRoundRect(dx + 35, cy, dw - 70, 44, 12, 12);

            // Border
            g2d.setColor(new Color(101, 67, 33));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(dx + 35, cy, dw - 70, 44, 12, 12);

            // Text
            g2d.setColor(new Color(40, 30, 25));
            g2d.drawString(choiceText, dx + 55, cy + 28);

            cy += 55;
        }

        // ==========================================================
        // INSTRUCTIONS
        // ==========================================================
        g2d.setFont(new Font("Arial", Font.ITALIC, 13));
        g2d.setColor(new Color(70, 60, 50));
        String inst = "Press A, B, C, or D to answer";

        fm = g2d.getFontMetrics();
        g2d.drawString(inst, dx + (dw - fm.stringWidth(inst)) / 2, dy + dh - 13);
    }

    
    // ==================== SECTION 18: PAUSE MENU RENDERING ====================
    
    /**
     * Draws the pause screen overlay
     */
    private void drawPauseMenu(Graphics g) {
    	 
    }
    
    
    // ==================== SECTION 19: UTILITY METHODS ====================
    
    /**
     * Draws the vanishing tiles warning popup
     * This appears at 2:10 remaining for 5 seconds
     */
    private void drawWarningPopup(Graphics2D g2) {
        if (!game.isWarningVisible()) {
            return; // Don't draw if not visible
        }

        // === POSITIONING ===
        int popupX = 0;  // 10px from left edge
        int popupY = 100; // Below top bar
        int popupWidth = Constants.SCREEN_WIDTH - 20;  // Full width minus margins
        int popupHeight = 100; // Increased height for better readability

        // === WARNING COLORS (Orange/Red theme for urgency) ===
        Color outerBorder = new Color(255, 69, 0);        // Red-Orange (urgent)
        Color mainBg = new Color(255, 140, 0);            // Dark orange
        Color innerBg = new Color(255, 228, 196);         // Bisque (light peach)
        Color textDark = new Color(139, 0, 0);            // Dark red text
        
        // === DRAW OUTER BORDER (Bright Red-Orange) ===
        g2.setColor(outerBorder);
        g2.fillRoundRect(popupX, popupY, popupWidth, popupHeight, 20, 20);
        
        // === DRAW MAIN BACKGROUND (Orange warning) ===
        g2.setColor(mainBg);
        g2.fillRoundRect(popupX + 5, popupY + 5, popupWidth - 10, popupHeight - 10, 15, 15);
        
        // === DRAW INNER BACKGROUND (Light peach) ===
        g2.setColor(innerBg);
        g2.fillRoundRect(popupX + 12, popupY + 12, popupWidth - 24, popupHeight - 24, 10, 10);
        
        // === DECORATIVE INNER BORDER ===
        g2.setColor(outerBorder);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(popupX + 12, popupY + 12, popupWidth - 24, popupHeight - 24, 10, 10);
        
        // === DRAW WARNING TEXT ===
        // Main warning text (large and bold)
        g2.setFont(new Font("Verdana", Font.BOLD, 26));
        String mainText = "⚠ WARNING: TILES VANISHING IN 2 MINUTES! ⚠";
        FontMetrics fm1 = g2.getFontMetrics();
        int textX1 = popupX + (popupWidth - fm1.stringWidth(mainText)) / 2;
        int textY1 = popupY + 42;
        
        // Text shadow for main warning
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(mainText, textX1 + 1, textY1 + 1);
        g2.setColor(textDark);
        g2.drawString(mainText, textX1, textY1);

        // Sub message text (smaller)
        g2.setFont(new Font("Verdana", Font.PLAIN, 18));
        String subText = "Collect all the clues as soon as possible!";
        FontMetrics fm2 = g2.getFontMetrics();
        int textX2 = popupX + (popupWidth - fm2.stringWidth(subText)) / 2;
        int textY2 = popupY + 72;
        
        // Text shadow for sub message
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(subText, textX2 + 1, textY2 + 1);
        g2.setColor(textDark);
        g2.drawString(subText, textX2, textY2);
    }

    
   
    // Returns to main menu from game
    
   private void returnToMainMenu() {
       if (gameWindow != null) {
           System.out.println("Returning to Main Menu...");
           
           // Stop the game
           running = false;
           if (gameTimer != null) {
               gameTimer.stop();
           }
           
           // Switch back to main menu
           gameWindow.returnToMainMenu();
       } else {
           System.err.println("❌ Cannot return to menu - GameWindow reference is null");
       }
   }

   /**
    * Exits the game completely
    */
   private void exitGame() {
       System.out.println("Exiting game...");
       
       // Stop the game
       running = false;
       if (gameTimer != null) {
           gameTimer.stop();
       }
       
       // Close the application
       System.exit(0);
   }
    /**
     * Draws text with word wrapping
     */
    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        String line = "";
        
        for (String word : words) {
            String test = line + word + " ";
            if (fm.stringWidth(test) > maxWidth && !line.isEmpty()) {
                g.drawString(line, x, y);
                y += fm.getHeight() + 5;
                line = word + " ";
            } else {
                line = test;
            }
        }
        
        if (!line.isEmpty()) {
            g.drawString(line, x, y);
        }
    }

    

//==================== SECTION 20: FINAL CHALLENGE ====================
//*** THIS HANDLES THE FINAL WORD INPUT DIALOG *

/**
* Handles keyboard input during Final Challenge mode
*/
private void handleFinalChallengeInput(KeyEvent e) {
 char c = e.getKeyChar();
 
 // Handle special keys
 if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
     game.appendFinalAnswer((char) 8);  // Backspace character
 } 
 else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
     game.appendFinalAnswer('\n');  // Enter triggers submission
 }
 // Handle letter input
 else if (Character.isLetter(c)) {
     game.appendFinalAnswer(c);
 }
 
 repaint();
}

/**
* Draws the Final Challenge dialog where player types the word
*/
private void drawFinalChallengeDialog(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // Dialog dimensions
    int dw = 750;
    int dh = 500;
    int dx = (Constants.SCREEN_WIDTH - dw) / 2;
    int dy = (Constants.SCREEN_HEIGHT - dh) / 2;
    
    // Dark overlay
    g2d.setColor(new Color(0, 0, 0, 220)); 
    g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
    
    
    // Main dialog background (brown like clue box)
    g2d.setColor(new Color(255, 244, 210));
    g2d.fillRoundRect(dx, dy, dw, dh, 20, 20);
    
    // Inner BORDER
    g2d.setColor(new Color(101, 67, 33));  // #C48A3E
    g2d.setStroke(new BasicStroke(5));
    g2d.drawRoundRect(dx, dy, dw, dh, 25, 25);

    
    // Title bar (brown background like clue box header)
    g2d.setColor(new Color(101, 67, 33));
    g2d.fillRoundRect(dx + 30, dy + 30, dw - 60, 70, 12, 12);
    
    // Title text with gold color and shadow effect
    g2d.setFont(new Font("Serif", Font.BOLD, 32));
    String title = "FINAL CHALLENGE FOR LEVEL " + game.getCurrentLevel();
    FontMetrics fm = g2d.getFontMetrics();
    int titleX = dx + (dw - fm.stringWidth(title)) / 2;
    
    // Shadow
    g2d.setColor(new Color(0, 0, 0, 100));
    g2d.drawString(title, titleX + 2, dy + 73);
    
    // Gold text
    g2d.setColor(new Color(255, 215, 102));
    g2d.drawString(title, titleX, dy + 71);
    
    // Instructions
    g2d.setFont(new Font("Georgia", Font.BOLD | Font.ITALIC, 20));
    g2d.setColor(new Color(40, 30, 20));
    String inst1 = "Arrange the letters you collected to spell the correct word!";
    fm = g2d.getFontMetrics();
    g2d.drawString(inst1, dx + (dw - fm.stringWidth(inst1)) / 2, dy + 150);
    
    // Show collected letters
    g2d.setFont(new Font("Arial", Font.PLAIN, 15));
    g2d.setColor(new Color(60, 50, 40));
    String inst2 = "Your collected letters:";
    fm = g2d.getFontMetrics();
    g2d.drawString(inst2, dx + (dw - fm.stringWidth(inst2)) / 2, dy + 190);
    
    // Display collected letters (shuffled order)
    ArrayList<Character> letters = game.getCollectedLetters();
    g2d.setFont(new Font("Arial", Font.BOLD, 32));
    g2d.setColor(new Color(0, 0, 0));
    
    int letterSpacing = 50;
    int letterX = dx + (dw - (letters.size() * letterSpacing)) / 2;
    int letterY = dy + 235;
    
    for (char letter : letters) {
        g2d.drawString(String.valueOf(letter), letterX, letterY);
        letterX += letterSpacing;
    }
    
    // Input box background (brown like clue box options)
    int inputBoxY = dy + 280;
    int inputBoxHeight = 70;
    g2d.setColor(new Color(245, 235, 210));
    g2d.fillRoundRect(dx + 70, inputBoxY, dw - 140, inputBoxHeight, 10, 10);
    
    // Input box border (dark brown)
    g2d.setColor(new Color(101, 67, 33));
    g2d.setStroke(new BasicStroke(3));
    g2d.drawRoundRect(dx + 70, inputBoxY, dw - 140, inputBoxHeight, 10, 10);
    
    // Display current input
    String currentInput = game.getFinalAnswerInput();
    g2d.setFont(new Font("Arial", Font.BOLD, 36));
    g2d.setColor(new Color(255, 170, 0));
    fm = g2d.getFontMetrics();
    
    // Center the input text
    int inputTextX = dx + (dw - fm.stringWidth(currentInput)) / 2;
    g2d.drawString(currentInput, inputTextX, inputBoxY + 48);
    
    // Blinking cursor
    if (System.currentTimeMillis() % 1000 < 500) {
        int cursorX = inputTextX + fm.stringWidth(currentInput) + 5;
        g2d.setColor(new Color(101, 67, 33));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(cursorX, inputBoxY + 20, cursorX, inputBoxY + 55);
    }
    
    // Warning message
    g2d.setFont(new Font("Arial", Font.BOLD, 18));
    g2d.setColor(new Color(180, 60, 60));
    String warning = "WARNING: Wrong answer = Restart from Level 1!";
    fm = g2d.getFontMetrics();
    g2d.drawString(warning, dx + (dw - fm.stringWidth(warning)) / 2, dy + 390);
    
    // Controls instruction
    g2d.setFont(new Font("Arial", Font.ITALIC, 16));
    g2d.setColor(new Color(60, 50, 40));
    String controls = "Type your answer • BACKSPACE to delete • ENTER to submit";
    fm = g2d.getFontMetrics();
    g2d.drawString(controls, dx + (dw - fm.stringWidth(controls)) / 2, dy + dh - 40);
    
    // Max length indicator
    int currentLength = currentInput.length();
    int maxLength = GameManager.MAX_FINAL_ANSWER_LENGTH;
    String lengthText = currentLength + " / " + maxLength;
    g2d.setFont(new Font("Arial", Font.PLAIN, 14));
    g2d.setColor(new Color(80, 70, 60));
    fm = g2d.getFontMetrics();
    g2d.drawString(lengthText, dx + dw - 100, inputBoxY + inputBoxHeight + 25);
}

//==================== SECTION 21: ANSWER FEEDBACK POPUP ====================

/**
* Draws the answer feedback popup (CORRECT or WRONG)
* Appears for 0.5 seconds after answering a question
*/
private void drawAnswerFeedbackPopup(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    boolean isCorrect = game.wasLastAnswerCorrect();
    
    // Dialog dimensions - centered
    int dw = 880;
    int dh = 590;
    int dx = (Constants.SCREEN_WIDTH - dw) / 2;  // Centered horizontally
    int dy = (Constants.SCREEN_HEIGHT - dh) / 2;  // Centered vertically
    
    // ==========================================================
    // DARK OVERLAY WITH GRID PATTERN
    // ==========================================================
    // Dark semi-transparent overlay
    g2d.setColor(new Color(0, 0, 0, 180));
    g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
    
    // Draw grid pattern on overlay
    g2d.setColor(new Color(30, 30, 30, 100));
    int gridSize = 40;
    for (int x = 0; x < Constants.SCREEN_WIDTH; x += gridSize) {
        g2d.drawLine(x, 0, x, Constants.SCREEN_HEIGHT);
    }
    for (int y = 0; y < Constants.SCREEN_HEIGHT; y += gridSize) {
        g2d.drawLine(0, y, Constants.SCREEN_WIDTH, y);
    }
    
    // ==========================================================
    // SOFT YELLOW BACKGROUND (matching quiz dialog)
    // ==========================================================
    Color lightCream = new Color(255, 244, 210); // soft yellow-cream
    g2d.setColor(lightCream);
    g2d.fillRoundRect(dx, dy, dw, dh, 25, 25);
    
    // ==========================================================
    // BORDER (GREEN for correct, RED for wrong)
    // ==========================================================
    // Main border color changes based on answer
    Color borderColor = isCorrect ? new Color(76, 175, 80) : new Color(220, 53, 69); // Green or Red
    g2d.setColor(borderColor);
    g2d.setStroke(new BasicStroke(5));
    g2d.drawRoundRect(dx, dy, dw, dh, 25, 25);
    
    // ==========================================================
    // TITLE BAR (dark brown background matching quiz)
    // ==========================================================
    Color titleBrown = new Color(101, 67, 33);
    g2d.setColor(titleBrown);
    g2d.fillRoundRect(dx + 15, dy + 15, dw - 30, 80, 18, 18);
    
    // Title bar border
    g2d.setColor(new Color(80, 50, 25));
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRoundRect(dx + 15, dy + 15, dw - 30, 80, 18, 18);
    
    // ==========================================================
    // BANNER TEXT (GREEN for Correct, RED for Wrong)
    // ==========================================================
    String bannerText = isCorrect ? "CORRECT!" : "WRONG!";
    g2d.setFont(new Font("Georgia", Font.BOLD, 64));
    
    // Green for correct, red for wrong
    Color bannerColor = isCorrect ? new Color(76, 175, 80) : new Color(220, 53, 69); // Green or Red
    g2d.setColor(bannerColor);
    
    FontMetrics fm = g2d.getFontMetrics();
    int bannerX = dx + (dw - fm.stringWidth(bannerText)) / 2;
    g2d.drawString(bannerText, bannerX, dy + 75);
    
    // ==========================================================
    // MESSAGE BOX (light cream background matching quiz)
    // ==========================================================
    int boxY = dy + 220;
    int boxHeight = 200;
    
    // Message box background (light cream)
    g2d.setColor(new Color(255, 251, 238));
    g2d.fillRoundRect(dx + 35, boxY, dw - 70, boxHeight, 12, 12);
    
    // Message box border (GREEN for correct, RED for wrong)
    Color messageBoxBorderColor = isCorrect ? new Color(76, 175, 80) : new Color(220, 53, 69); // Green or Red
    g2d.setColor(messageBoxBorderColor);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRoundRect(dx + 35, boxY, dw - 70, boxHeight, 12, 12);
    
    // ==========================================================
    // MESSAGE TEXT (dark brown sans-serif)
    // ==========================================================
    g2d.setFont(new Font("Arial", Font.BOLD, 36));
    g2d.setColor(new Color(40, 30, 25)); // Dark brown text
    
    if (isCorrect) {
        String message1 = "You got the letter : " + game.getEarnedLetter();
        fm = g2d.getFontMetrics();
        int msg1X = dx + (dw - fm.stringWidth(message1)) / 2;
        g2d.drawString(message1, msg1X, boxY + 120);
    } else {
        String message1 = "Punishment: Lose one heart";
        fm = g2d.getFontMetrics();
        int msg1X = dx + (dw - fm.stringWidth(message1)) / 2;
        g2d.drawString(message1, msg1X, boxY + 120);
    }
    
    // ==========================================================
    // BOTTOM MESSAGE (light brown italic, matching quiz)
    // ==========================================================
    g2d.setFont(new Font("Arial", Font.ITALIC, 24));
    g2d.setColor(new Color(70, 60, 50)); // Light brown
    
    String bottomMsg1;
    if (isCorrect) {
        bottomMsg1 = "Great job! You got it right!";
    } else {
        bottomMsg1 = "Wrong answer! Be careful next time.";
    }
    
    fm = g2d.getFontMetrics();
    int bottom1X = dx + (dw - fm.stringWidth(bottomMsg1)) / 2;
    g2d.drawString(bottomMsg1, bottom1X, dy + dh - 50);
}

//==================== FINAL CHALLENGE RESULT POPUP ====================

/**
* Draws the result popup after submitting final challenge answer
* GREEN theme = Correct (proceed to next level)
* RED theme = Wrong (reset to level 1)
*/
private void drawFinalChallengeResultPopup(Graphics g) {
  Graphics2D g2d = (Graphics2D) g;
  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

  boolean isCorrect = game.wasFinalAnswerCorrect();

  // Dialog dimensions
  int dw = 880;
  int dh = 590;
  int dx = (Constants.SCREEN_WIDTH - dw) / 2;
  int dy = (Constants.SCREEN_HEIGHT - dh) / 2;

  // Dark overlay
  g2d.setColor(new Color(0, 0, 0, 200));
  g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

  // === COLORS BASED ON CORRECT/WRONG ===
  Color outerBorder, innerBg, titleColor, boxBg, boxBorder;
  
  if (isCorrect) {
      // ✅ SUCCESS COLORS (Green/Gold theme)
      outerBorder = new Color(34, 139, 34);      // Forest green outer
      innerBg = new Color(240, 255, 240);         // Very light mint green
      titleColor = new Color(0, 100, 0);          // Dark green
      boxBg = new Color(144, 238, 144);           // Light green
      boxBorder = new Color(34, 139, 34);         // Forest green
  } else {
      // ❌ FAILURE COLORS (Red/Dark theme)
      outerBorder = new Color(178, 34, 34);      // Fire brick red
      innerBg = new Color(255, 240, 240);         // Very light pink/red
      titleColor = new Color(139, 0, 0);          // Dark red
      boxBg = new Color(255, 182, 193);           // Light pink
      boxBorder = new Color(178, 34, 34);         // Fire brick red
  }

  // === OUTER BORDER (Thick and bold) ===
  g2d.setColor(outerBorder);
  g2d.fillRoundRect(dx, dy, dw, dh, 20, 20);

  // === INNER BACKGROUND ===
  g2d.setColor(innerBg);
  g2d.fillRoundRect(dx + 12, dy + 12, dw - 24, dh - 24, 15, 15);

  // === DECORATIVE INNER BORDER ===
  g2d.setColor(outerBorder);
  g2d.setStroke(new BasicStroke(3));
  g2d.drawRoundRect(dx + 12, dy + 12, dw - 24, dh - 24, 15, 15);

  // === TOP TITLE ===
  g2d.setFont(new Font("Verdana", Font.BOLD, 48));
  g2d.setColor(titleColor);
  String title = isCorrect ? "PROCEED TO" : "RESET TO";
  FontMetrics fm = g2d.getFontMetrics();
  int titleX = dx + (dw - fm.stringWidth(title)) / 2;
  
  // Title shadow
  g2d.setColor(new Color(0, 0, 0, 100));
  g2d.drawString(title, titleX + 2, dy + 102);
  g2d.setColor(titleColor);
  g2d.drawString(title, titleX, dy + 100);

  // === LEVEL TEXT ===
  g2d.setFont(new Font("Verdana", Font.BOLD, 52));
  String levelText;
  if (isCorrect) {
      int nextLevel = game.getCurrentLevel() + 1;
      if (nextLevel > game.getMaxLevel()) {
          levelText = "VICTORY!";
      } else {
          levelText = "LEVEL " + nextLevel + "!";
      }
  } else {
      levelText = "LEVEL 1";
  }
  fm = g2d.getFontMetrics();
  int levelX = dx + (dw - fm.stringWidth(levelText)) / 2;
  
  // Level shadow
  g2d.setColor(new Color(0, 0, 0, 100));
  g2d.drawString(levelText, levelX + 2, dy + 167);
  g2d.setColor(titleColor);
  g2d.drawString(levelText, levelX, dy + 165);

  // === MESSAGE BOX ===
  int boxY = dy + 235;
  int boxHeight = 200;

  // Box background (colored)
  g2d.setColor(boxBg);
  g2d.fillRoundRect(dx + 30, boxY, dw - 60, boxHeight, 15, 15);

  // Box border (thick and colored)
  g2d.setColor(boxBorder);
  g2d.setStroke(new BasicStroke(5));
  g2d.drawRoundRect(dx + 30, boxY, dw - 60, boxHeight, 15, 15);

  // === MESSAGE TEXT ===
  g2d.setFont(new Font("Verdana", Font.BOLD, 52));
  String mainMessage = isCorrect ? "YOU GOT IT RIGHT!" : "So close! Keep going!";
  
  // Determine message color (darker for readability)
  Color messageColor = isCorrect ? new Color(0, 80, 0) : new Color(139, 0, 0);
  
  fm = g2d.getFontMetrics();
  int msgX = dx + (dw - fm.stringWidth(mainMessage)) / 2;
  
  // Message shadow
  g2d.setColor(new Color(0, 0, 0, 80));
  g2d.drawString(mainMessage, msgX + 2, boxY + 122);
  g2d.setColor(messageColor);
  g2d.drawString(mainMessage, msgX, boxY + 120);

  // === BOTTOM MESSAGE ===
  g2d.setFont(new Font("Verdana", Font.BOLD, 28));
  g2d.setColor(titleColor);

  String bottomMsg = isCorrect ? "GOOD LUCK TO THE NEXT LEVEL!" : "Be careful when you return!";
  fm = g2d.getFontMetrics();
  int bottomX = dx + (dw - fm.stringWidth(bottomMsg)) / 2;
  
  // Bottom shadow
  g2d.setColor(new Color(0, 0, 0, 80));
  g2d.drawString(bottomMsg, bottomX + 1, dy + dh - 68);
  g2d.setColor(titleColor);
  g2d.drawString(bottomMsg, bottomX, dy + dh - 70);
}

//==================== SECTION 22: GAME OVER POPUP HANDLERS ====================

/**
* Handles clicks on the Game Over "Return to Main Menu" button
*/
private void handleGameOverClick(int mouseX, int mouseY) {
  // Calculate button dimensions (must match drawGameOverPopup)
  int dw = 880;
  int dh = 590;
  int dx = (Constants.SCREEN_WIDTH - dw) / 2;
  int dy = (Constants.SCREEN_HEIGHT - dh) / 2;

  int buttonWidth = 300;
  int buttonHeight = 50;
  int buttonX = dx + (dw - buttonWidth) / 2;
  int buttonY = dy + dh - 110;

  // Check if click is within button bounds
  if (isPointInRect(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight)) {
     System.out.println("Game Over - Return to Main Menu clicked");
     game.closeGameOver();
     returnToMainMenu();
  }
}

/**
* Checks if mouse is hovering over Game Over button
*/
private void checkGameOverHover(int mouseX, int mouseY) {
  // Calculate button dimensions (must match drawGameOverPopup)
  int dw = 880;
  int dh = 590;
  int dx = (Constants.SCREEN_WIDTH - dw) / 2;
  int dy = (Constants.SCREEN_HEIGHT - dh) / 2;

  int buttonWidth = 300;
  int buttonHeight = 50;
  int buttonX = dx + (dw - buttonWidth) / 2;
  int buttonY = dy + dh - 110;

  boolean wasHovered = gameOverButtonHovered;
  gameOverButtonHovered = isPointInRect(mouseX, mouseY, buttonX, buttonY, buttonWidth, buttonHeight);

  // Change cursor when hovering
  if (gameOverButtonHovered) {
     setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  } else {
     setCursor(Cursor.getDefaultCursor());
  }

  // Repaint if hover changed
  if (wasHovered != gameOverButtonHovered) {
     repaint();
  }
}

/**
* Draws the Game Over popup (OUT OF HEART, OUT OF TIME, or FELL)
* RED theme for clear visual indication of failure
*/
private void drawGameOverPopup(Graphics g) {
  System.out.println("🎨 DRAWING GAME OVER POPUP - Reason: " + game.getGameOverReason());
    
  Graphics2D g2d = (Graphics2D) g;
  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
  String reason = game.getGameOverReason();

  // Dialog dimensions
  int dw = 880;
  int dh = 590;
  int dx = (Constants.SCREEN_WIDTH - dw) / 2;
  int dy = (Constants.SCREEN_HEIGHT - dh) / 2;

  // Dark overlay
  g2d.setColor(new Color(0, 0, 0, 200));
  g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

  // === OUTER BORDER (Bold red for game over) ===
  g2d.setColor(new Color(178, 34, 34));  // Fire brick red
  g2d.fillRoundRect(dx, dy, dw, dh, 20, 20);

  // === INNER BACKGROUND (Light red tint) ===
  g2d.setColor(new Color(255, 240, 240));  // Very light pink/red
  g2d.fillRoundRect(dx + 12, dy + 12, dw - 24, dh - 24, 15, 15);

  // === DECORATIVE INNER BORDER ===
  g2d.setColor(new Color(178, 34, 34));
  g2d.setStroke(new BasicStroke(3));
  g2d.drawRoundRect(dx + 12, dy + 12, dw - 24, dh - 24, 15, 15);

  // === TOP BANNER ===
  g2d.setFont(new Font("Verdana", Font.BOLD, 72));
  g2d.setColor(new Color(139, 0, 0));  // Dark red
  String bannerText = "GAME OVER!";
  FontMetrics fm = g2d.getFontMetrics();
  int bannerX = dx + (dw - fm.stringWidth(bannerText)) / 2;
  
  // Banner shadow
  g2d.setColor(new Color(0, 0, 0, 120));
  g2d.drawString(bannerText, bannerX + 3, dy + 133);
  g2d.setColor(new Color(139, 0, 0));
  g2d.drawString(bannerText, bannerX, dy + 130);

  // === MESSAGE BOX ===
  int boxY = dy + 180;
  int boxHeight = 200;

  // Box background (light red/pink)
  g2d.setColor(new Color(255, 182, 193));  // Light pink
  g2d.fillRoundRect(dx + 30, boxY, dw - 60, boxHeight, 15, 15);

  // Box border (red)
  g2d.setColor(new Color(178, 34, 34));
  g2d.setStroke(new BasicStroke(5));
  g2d.drawRoundRect(dx + 30, boxY, dw - 60, boxHeight, 15, 15);

  // === MESSAGE TEXT (varies by reason) ===
  g2d.setFont(new Font("Verdana", Font.BOLD, 42));
  g2d.setColor(new Color(139, 0, 0));  // Dark red

  String mainMessage = "";
  switch (reason) {
     case "HEART":
         mainMessage = "OUT OF HEART!";
         break;
     case "TIME":
         mainMessage = "OUT OF TIME!";
         break;
     case "FALL":
         mainMessage = "You fell from a vanishing tile!";
         break;
     default:
         mainMessage = "GAME OVER!";
         break;
  }

  fm = g2d.getFontMetrics();
  int msgX = dx + (dw - fm.stringWidth(mainMessage)) / 2;
  
  // Message shadow
  g2d.setColor(new Color(0, 0, 0, 80));
  g2d.drawString(mainMessage, msgX + 2, boxY + 112);
  g2d.setColor(new Color(139, 0, 0));
  g2d.drawString(mainMessage, msgX, boxY + 110);

  // === BOTTOM MESSAGE ===
  g2d.setFont(new Font("Verdana", Font.BOLD, 24));
  g2d.setColor(new Color(139, 0, 0));
  String bottomMsg1 = "Be careful next time!";
  fm = g2d.getFontMetrics();
  int bottom1X = dx + (dw - fm.stringWidth(bottomMsg1)) / 2;
  
  // Bottom shadow
  g2d.setColor(new Color(0, 0, 0, 80));
  g2d.drawString(bottomMsg1, bottom1X + 1, dy + 422);
  g2d.setColor(new Color(139, 0, 0));
  g2d.drawString(bottomMsg1, bottom1X, dy + 420);

  // === "RETURN TO MAIN MENU" BUTTON ===
  int buttonWidth = 300;
  int buttonHeight = 50;
  int buttonX = dx + (dw - buttonWidth) / 2;
  int buttonY = dy + dh - 110;

  // Button background (brown theme to match your style)
  if (gameOverButtonHovered) {
     g2d.setColor(new Color(120, 100, 80));  // Lighter brown
  } else {
     g2d.setColor(new Color(100, 80, 60));   // Brown
  }
  g2d.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 12, 12);

  // Button border
  g2d.setColor(new Color(70, 60, 50));
  g2d.setStroke(new BasicStroke(3));
  g2d.drawRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 12, 12);

  // Button text
  g2d.setFont(new Font("Verdana", Font.BOLD, 18));
  g2d.setColor(Color.WHITE);
  String buttonText = "Click to return to menu";
  fm = g2d.getFontMetrics();
  int textX = buttonX + (buttonWidth - fm.stringWidth(buttonText)) / 2;
  int textY = buttonY + (buttonHeight + fm.getAscent()) / 2 - 2;
  
  // Button text shadow
  g2d.setColor(new Color(0, 0, 0, 100));
  g2d.drawString(buttonText, textX + 1, textY + 1);
  g2d.setColor(Color.WHITE);
  g2d.drawString(buttonText, textX, textY);

  // Hover effect
  if (gameOverButtonHovered) {
     g2d.setColor(new Color(255, 255, 255, 100));
     g2d.setStroke(new BasicStroke(3));
     g2d.drawRoundRect(buttonX - 2, buttonY - 2, buttonWidth + 4, buttonHeight + 4, 14, 14);
  }
}

/**
* Draws the mercy deactivation notification popup
* WARNING style with red/brown theme
*/
private void drawMercyDeactivatedPopup(Graphics2D g2) {
  if (!game.isShowingMercyDeactivated()) {
      return;
  }
  
  // === POSITIONING ===
  int popupX = 0;
  int popupY = 100;  // Below top bar
  int popupWidth = Constants.SCREEN_WIDTH - 20;
  int popupHeight = 100;
  
  // === WARNING COLORS (Red/Orange theme) ===
  Color outerBorder = new Color(178, 34, 34);      // Fire brick red
  Color mainBg = new Color(255, 140, 0);            // Dark orange (warning)
  Color innerBg = new Color(255, 228, 196);         // Bisque (light peach)
  Color textDark = new Color(139, 0, 0);            // Dark red text
  
  // === DRAW OUTER BORDER (Red) ===
  g2.setColor(outerBorder);
  g2.fillRoundRect(popupX, popupY, popupWidth, popupHeight, 20, 20);
  
  // === DRAW MAIN BACKGROUND (Orange warning) ===
  g2.setColor(mainBg);
  g2.fillRoundRect(popupX + 5, popupY + 5, popupWidth - 10, popupHeight - 10, 15, 15);
  
  // === DRAW INNER BACKGROUND (Light) ===
  g2.setColor(innerBg);
  g2.fillRoundRect(popupX + 12, popupY + 12, popupWidth - 24, popupHeight - 24, 10, 10);
  
  // === DECORATIVE INNER BORDER ===
  g2.setColor(outerBorder);
  g2.setStroke(new BasicStroke(2));
  g2.drawRoundRect(popupX + 12, popupY + 12, popupWidth - 24, popupHeight - 24, 10, 10);
  
  // === DRAW TEXT ===
  // Main text (line 1)
  g2.setFont(new Font("Verdana", Font.BOLD, 26));
  String line1 = " Caution! Mercy has been deactivated. ";
  FontMetrics fm1 = g2.getFontMetrics();
  int text1X = popupX + (popupWidth - fm1.stringWidth(line1)) / 2;
  int text1Y = popupY + 42;
  
  // Text shadow
  g2.setColor(new Color(0, 0, 0, 100));
  g2.drawString(line1, text1X + 1, text1Y + 1);
  g2.setColor(textDark);
  g2.drawString(line1, text1X, text1Y);
  
  // Sub text (line 2)
  g2.setFont(new Font("Verdana", Font.PLAIN, 18));
  String line2 = "You have gathered every clue.";
  FontMetrics fm2 = g2.getFontMetrics();
  int text2X = popupX + (popupWidth - fm2.stringWidth(line2)) / 2;
  int text2Y = popupY + 72;
  
  // Sub text shadow
  g2.setColor(new Color(0, 0, 0, 100));
  g2.drawString(line2, text2X + 1, text2Y + 1);
  g2.setColor(textDark);
  g2.drawString(line2, text2X, text2Y);
}

//==================== SECTION 23: STORY FRAME RENDERING ====================

/**
* Draws the story frame with image, animated text, and controls
*/
private void drawStoryFrame(Graphics g) {
  Graphics2D g2d = (Graphics2D) g;
  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  
  StoryFrame frame = game.getCurrentStoryFrame();
  if (frame == null) {
      System.err.println("❌ Story frame is null!");
      return;
  }
  
  // Full screen black background
  g2d.setColor(Color.BLACK);
  g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
  
  // Story image (scaled to fit screen)
  BufferedImage image = frame.getImage();
  if (image != null) {
      int imgWidth = Constants.SCREEN_WIDTH;
      int imgHeight = Constants.SCREEN_HEIGHT - 180;
      g2d.drawImage(image, 0, 0, imgWidth, imgHeight, null);
  }
  
  // Text box area
  int textBoxY = Constants.SCREEN_HEIGHT - 180;
  int textBoxHeight = 140;
  
  g2d.setColor(new Color(0, 0, 0, 220));
  g2d.fillRect(0, textBoxY, Constants.SCREEN_WIDTH, textBoxHeight);
  
  g2d.setColor(new Color(100, 100, 100));
  g2d.setStroke(new BasicStroke(2));
  g2d.drawLine(0, textBoxY, Constants.SCREEN_WIDTH, textBoxY);
  
  // Story text with typewriter effect
  g2d.setColor(Color.WHITE);
  g2d.setFont(new Font("Arial", Font.PLAIN, 22));
  
  String visibleText = frame.getVisibleText();
  int textX = 50;
  int textY = textBoxY + 40;
  int maxWidth = Constants.SCREEN_WIDTH - 100;
  
  drawWrappedStoryText(g2d, visibleText, textX, textY, maxWidth);
  
  // Blinking cursor
  if (frame.isCursorVisible()) {
      FontMetrics fm = g2d.getFontMetrics();
      String[] lines = wrapStoryText(visibleText, fm, maxWidth);
      int cursorX = textX;
      int cursorY = textY;
      
      if (lines.length > 0) {
          String lastLine = lines[lines.length - 1];
          cursorX = textX + fm.stringWidth(lastLine);
          cursorY = textY + (lines.length - 1) * (fm.getHeight() + 5);
      }
      
      g2d.setColor(new Color(255, 215, 0));
      g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
      g2d.drawString("█", cursorX + 5, cursorY);
  }
  
  // Continue prompt
  if (frame.isTypingComplete()) {
      float alpha = Math.min(1.0f, (System.currentTimeMillis() % 2000) / 1000.0f);
      if (alpha > 1.0f) alpha = 2.0f - alpha;
      
      g2d.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
      g2d.setFont(new Font("Arial", Font.ITALIC, 18));
      String prompt = "Press ENTER to continue  •  ESC to skip all";
      FontMetrics fm = g2d.getFontMetrics();
      int promptX = (Constants.SCREEN_WIDTH - fm.stringWidth(prompt)) / 2;
      g2d.drawString(prompt, promptX, Constants.SCREEN_HEIGHT - 25);
  } else {
      g2d.setColor(new Color(200, 200, 200, 180));
      g2d.setFont(new Font("Arial", Font.ITALIC, 16));
      String prompt = "Press SPACE to complete text  •  ESC to skip all";
      FontMetrics fm = g2d.getFontMetrics();
      int promptX = (Constants.SCREEN_WIDTH - fm.stringWidth(prompt)) / 2;
      g2d.drawString(prompt, promptX, Constants.SCREEN_HEIGHT - 25);
  }
}

private void drawWrappedStoryText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
  FontMetrics fm = g2d.getFontMetrics();
  String[] lines = wrapStoryText(text, fm, maxWidth);
  
  int lineHeight = fm.getHeight() + 5;
  
  for (int i = 0; i < lines.length; i++) {
      g2d.drawString(lines[i], x, y + (i * lineHeight));
  }
}

private String[] wrapStoryText(String text, FontMetrics fm, int maxWidth) {
  java.util.ArrayList<String> lines = new java.util.ArrayList<>();
  String[] words = text.split(" ");
  String currentLine = "";
  
  for (String word : words) {
      String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
      
      if (fm.stringWidth(testLine) <= maxWidth) {
          currentLine = testLine;
      } else {
          if (!currentLine.isEmpty()) {
              lines.add(currentLine);
          }
          currentLine = word;
      }
  }
  
  if (!currentLine.isEmpty()) {
      lines.add(currentLine);
  }
  
  return lines.toArray(new String[0]);
}

//==================== CONFIRMATION DIALOG METHODS ====================

/**
* Handles clicks on confirmation dialog buttons
*/
private void handleConfirmDialogClick(int mouseX, int mouseY) {
  int dialogW = 600;
  int dialogH = 250;
  int dialogX = (Constants.SCREEN_WIDTH - dialogW) / 2;
  int dialogY = (Constants.SCREEN_HEIGHT - dialogH) / 2;
  
  int buttonWidth = 200;
  int buttonHeight = 50;
  int buttonY = dialogY + dialogH - 80;
  int confirmButtonX = dialogX + 80;
  int cancelButtonX = dialogX + dialogW - 280;
  
  // Check Confirm button
  if (isPointInRect(mouseX, mouseY, confirmButtonX, buttonY, buttonWidth, buttonHeight)) {
      System.out.println("✅ Confirm button clicked - Type: " + confirmDialogType);
      handleConfirmAction();
      return;
  }
  
  // Check Cancel button (or click outside dialog = cancel)
  if (isPointInRect(mouseX, mouseY, cancelButtonX, buttonY, buttonWidth, buttonHeight) ||
      !isPointInRect(mouseX, mouseY, dialogX, dialogY, dialogW, dialogH)) {
      System.out.println("❌ Cancel button clicked or clicked outside");
      showingConfirmDialog = false;
      confirmDialogType = "";
      settingsMenuOpen = true;  // Reopen settings menu
      repaint();
  }
}

/**
* Executes the confirmed action
*/
/**
* Executes the confirmed action
* ✅ FIXED: Properly resumes game after restart
*/
/**
* Executes the confirmed action
* ✅ FIXED: Properly resumes game after restart
*/
/**
* Executes the confirmed action
* ✅ FIXED: Properly resumes game after restart
*/
private void handleConfirmAction() {
    if (confirmDialogType.equals("RESTART")) {
        System.out.println("🔄 Restarting level " + game.getCurrentLevel());
        showingConfirmDialog = false;
        confirmDialogType = "";
        
        // ✅ FIX: Store current level before restarting
        int currentLevel = game.getCurrentLevel();
        
        // ✅ FIX: Close settings menu state
        settingsMenuOpen = false;
        hoveredSettingOption = -1;
        game.setSettingsMenuOpen(false);
        
        // ✅ FIX: Reload the level (this resets everything)
        game.loadLevel(currentLevel);
        
        // ✅ FIX: CRITICAL - Resume the game so timer runs
        game.resumeGame();
        
        System.out.println("✅ Level " + currentLevel + " restarted and resumed");
        repaint();
        requestFocusInWindow();
        
    } else if (confirmDialogType.equals("MAIN_MENU")) {
        System.out.println("🏠 Returning to main menu");
        showingConfirmDialog = false;
        confirmDialogType = "";
        closeSettingsMenu();
        returnToMainMenu();
    }
}

/**
* Checks if mouse is hovering over confirmation dialog buttons
*/
private void checkConfirmDialogHover(int mouseX, int mouseY) {
  int dialogW = 600;
  int dialogH = 250;
  int dialogX = (Constants.SCREEN_WIDTH - dialogW) / 2;
  int dialogY = (Constants.SCREEN_HEIGHT - dialogH) / 2;
  
  int buttonWidth = 200;
  int buttonHeight = 50;
  int buttonY = dialogY + dialogH - 80;
  int confirmButtonX = dialogX + 80;
  int cancelButtonX = dialogX + dialogW - 280;
  
  boolean wasConfirmHovered = confirmButtonHovered;
  boolean wasCancelHovered = cancelButtonHovered;
  
  confirmButtonHovered = isPointInRect(mouseX, mouseY, confirmButtonX, buttonY, buttonWidth, buttonHeight);
  cancelButtonHovered = isPointInRect(mouseX, mouseY, cancelButtonX, buttonY, buttonWidth, buttonHeight);
  
  if (confirmButtonHovered || cancelButtonHovered) {
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  } else {
      setCursor(Cursor.getDefaultCursor());
  }
  
  if (wasConfirmHovered != confirmButtonHovered || wasCancelHovered != cancelButtonHovered) {
      repaint();
  }
}

/**
* Draws the custom confirmation dialog
*/
private void drawConfirmDialog(Graphics2D g2d) {
  if (!showingConfirmDialog) return;
  
  int dialogW = 600;
  int dialogH = 250;
  int dialogX = (Constants.SCREEN_WIDTH - dialogW) / 2;
  int dialogY = (Constants.SCREEN_HEIGHT - dialogH) / 2;
  
  // Even darker overlay for second layer
  g2d.setColor(new Color(0, 0, 0, 240));
  g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
  
  // Main dialog background
  g2d.setColor(new Color(194, 178, 153));
  g2d.fillRoundRect(dialogX, dialogY, dialogW, dialogH, 20, 20);
  
  // Border
  g2d.setColor(new Color(70, 60, 50));
  g2d.setStroke(new BasicStroke(4));
  g2d.drawRoundRect(dialogX, dialogY, dialogW, dialogH, 20, 20);
  
  // Title bar
  g2d.setColor(new Color(139, 115, 85));
  g2d.fillRoundRect(dialogX + 15, dialogY + 15, dialogW - 30, 60, 15, 15);
  
  g2d.setColor(new Color(70, 60, 50));
  g2d.setStroke(new BasicStroke(2));
  g2d.drawRoundRect(dialogX + 15, dialogY + 15, dialogW - 30, 60, 15, 15);
  
  // Title text
  g2d.setFont(new Font("Arial", Font.BOLD, 24));
  g2d.setColor(Color.WHITE);
  String title;
  if (confirmDialogType.equals("RESTART")) {
      title = "Restart Level?";
  } else if (confirmDialogType.equals("MAIN_MENU")) {
      title = "Return to main menu?";
  } else if (confirmDialogType.equals("EXIT")) {
      title = "Exit Game?";
  } else {
      title = "Confirm Action?";
  }
  
  FontMetrics fm = g2d.getFontMetrics();
  int titleX = dialogX + (dialogW - fm.stringWidth(title)) / 2;
  g2d.drawString(title, titleX, dialogY + 55);
//Message
g2d.setFont(new Font("Arial", Font.BOLD, 18));
g2d.setColor(new Color(50, 40, 30));
String message;
if (confirmDialogType.equals("RESTART")) {
   message = "Progress will be lost.";
} else if (confirmDialogType.equals("EXIT")) {
   message = "Are you sure?";
} else {
   message = "All progress will be lost.";
}

fm = g2d.getFontMetrics();
int messageX = dialogX + (dialogW - fm.stringWidth(message)) / 2;
g2d.drawString(message, messageX, dialogY + 130);

//Buttons
int buttonWidth = 200;
int buttonHeight = 50;
int buttonY = dialogY + dialogH - 80;
int confirmButtonX = dialogX + 80;
int cancelButtonX = dialogX + dialogW - 280;

//Confirm button
if (confirmButtonHovered) {
   g2d.setColor(new Color(139, 115, 85));
} else {
   g2d.setColor(new Color(119, 95, 65));
}
g2d.fillRoundRect(confirmButtonX, buttonY, buttonWidth, buttonHeight, 12, 12);

g2d.setColor(new Color(70, 60, 50));
g2d.setStroke(new BasicStroke(3));
g2d.drawRoundRect(confirmButtonX, buttonY, buttonWidth, buttonHeight, 12, 12);

g2d.setFont(new Font("Arial", Font.BOLD, 20));
g2d.setColor(Color.WHITE);
String confirmText = "Confirm";
fm = g2d.getFontMetrics();
int confirmTextX = confirmButtonX + (buttonWidth - fm.stringWidth(confirmText)) / 2;
int confirmTextY = buttonY + (buttonHeight + fm.getAscent()) / 2 - 2;
g2d.drawString(confirmText, confirmTextX, confirmTextY);

if (confirmButtonHovered) {
   g2d.setColor(new Color(255, 255, 255, 80));
   g2d.setStroke(new BasicStroke(3));
   g2d.drawRoundRect(confirmButtonX - 2, buttonY - 2, buttonWidth + 4, buttonHeight + 4, 14, 14);
}

//Cancel button
if (cancelButtonHovered) {
   g2d.setColor(new Color(139, 115, 85));
} else {
   g2d.setColor(new Color(119, 95, 65));
}
g2d.fillRoundRect(cancelButtonX, buttonY, buttonWidth, buttonHeight, 12, 12);

g2d.setColor(new Color(70, 60, 50));
g2d.setStroke(new BasicStroke(3));
g2d.drawRoundRect(cancelButtonX, buttonY, buttonWidth, buttonHeight, 12, 12);

g2d.setFont(new Font("Arial", Font.BOLD, 20));
g2d.setColor(Color.WHITE);
String cancelText = "Cancel";
fm = g2d.getFontMetrics();
int cancelTextX = cancelButtonX + (buttonWidth - fm.stringWidth(cancelText)) / 2;
int cancelTextY = buttonY + (buttonHeight + fm.getAscent()) / 2 - 2;
g2d.drawString(cancelText, cancelTextX, cancelTextY);

if (cancelButtonHovered) {
   g2d.setColor(new Color(255, 255, 255, 80));
   g2d.setStroke(new BasicStroke(3));
   g2d.drawRoundRect(cancelButtonX - 2, buttonY - 2, buttonWidth + 4, buttonHeight + 4, 14, 14);
}
}
}