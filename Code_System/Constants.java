package Code_System;

import java.awt.Color;
import java.awt.Font;

/**
 * Constants.java - DESKTOP SIZE VERSION
 * Larger screen optimized for desktop play
 */
public class Constants {
    
    // ==================== SCREEN SETTINGS ====================
    // Desktop-friendly size: wider screen with comfortable viewing
    public static final int SCREEN_WIDTH = 1551;   // Wide desktop size
    public static final int SCREEN_HEIGHT = 830;   // Comfortable height
    public static final int FPS = 60;
    
    
    // ==================== TILE SETTINGS ====================
    public static final int TILE_SIZE = 41;      //
    public static final int MAZE_WIDTH = 30;     // 
    public static final int MAZE_HEIGHT = 30;    // 
    
    
    
    // ==================== COLORS - TILES ====================
    // Brown dungeon theme matching reference image
    
    // PATH = Walkable floor (brown stone)
    public static final Color COLOR_PATH = new Color(120, 85, 60);
    
    // WALL = Blocked tiles (brick red-brown)
    public static final Color COLOR_WALL = new Color(100, 50, 40);
    
    // CLUE = Question tiles (golden treasure box)
    public static final Color COLOR_CLUE = new Color(200, 160, 80);
    
    // HAZARD = Damage tiles (red danger)
    public static final Color COLOR_HAZARD = new Color(75, 54, 33);
    
    // EXIT = Level exit (green/salmon color)
    public static final Color COLOR_EXIT = new Color(200, 120, 100);
    
    // START = Player spawn (green/salmon color)
    public static final Color COLOR_START = new Color(200, 120, 100);
    
    // VANISHED = Disappeared tiles
    public static final Color COLOR_VANISHED = new Color(30, 40, 40);
    
    
    // ==================== COLORS - UI ====================
    
    // Player color (will be overridden by sprite)
    public static final Color COLOR_PLAYER = new Color(220, 180, 120);
    
    // UI panel colors (tan/beige from reference)
    public static final Color COLOR_UI_BG = new Color(165, 145, 125);      // Panel background
    public static final Color COLOR_UI_BORDER = new Color(80, 70, 60);     // Panel border
    public static final Color COLOR_UI_TEXT = new Color(40, 35, 30);       // Dark text
    
    // Hearts
    public static final Color COLOR_HEALTH = new Color(220, 40, 40);
    
    // Mercy
    public static final Color COLOR_MERCY = new Color(80, 220, 150);
    
    // Letters (gold)
    public static final Color COLOR_LETTERS = new Color(255, 215, 0);
    
    
    // ==================== FOG OF WAR SETTINGS ====================
    
    // How many tiles away from player are visible
    // 1 = only adjacent tiles visible
    // 2 = player can see 2 tiles away (5x5 area)
    // 3 = player can see 3 tiles away (7x7 area)
    public static final int VISIBILITY_RANGE = 2;
    
    // Fog darkness (0-255, where 0=transparent, 255=solid black)
    // Recommended values:
    //   100 = Light fog (subtle)
    //   140 = Medium fog (balanced) ← CURRENT
    //   180 = Heavy fog (dark/mysterious)
    public static final int FOG_ALPHA = 190;
    
    // Fog color (black overlay)
    public static final Color COLOR_FOG = new Color(0, 0, 0, FOG_ALPHA);
    
    
    // ==================== FONTS ====================
    
    public static final Font FONT_UI = new Font("Arial", Font.PLAIN, 16);
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 24);
    public static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_LETTER = new Font("Arial", Font.BOLD, 28);
    public static final Font FONT_TIMER = new Font("Arial", Font.BOLD, 28);
    
    
    // ==================== ANIMATION SETTINGS ====================
    
    // Movement animation duration
    public static final int MOVE_ANIMATION_MS = 170;
    
    // Sprite animation frame delay
    public static final int SPRITE_FRAME_DELAY = 200;
    
    
    // ==================== UI LAYOUT ====================
    
    // Top area for panels
    public static final int UI_TOP_HEIGHT = 80;
    
    // Bottom area for panels  
    public static final int UI_BOTTOM_HEIGHT = 150;
    
    // Maze offset (to center it)
    public static final int MAZE_OFFSET_X = (SCREEN_WIDTH - (MAZE_WIDTH * TILE_SIZE)) / 2;  // Centers maze horizontally
    public static final int MAZE_OFFSET_Y = UI_TOP_HEIGHT;   // Below top panels
    
    // Panel spacing
    public static final int UI_PADDING = 15;
    
    
    // ==================== MOVEMENT SETTINGS ====================
    
    // Delay between moves (prevents key spam/holding)
    public static final int MOVE_DELAY_MS = 250;
    
    
    // ==================== VISUAL EFFECTS ====================
    
    // Draw shadow under player
    public static final boolean DRAW_PLAYER_SHADOW = true;
    
    // Panel corner radius
    public static final int PANEL_CORNER_RADIUS = 10;
    
    // Border thickness
    public static final int BORDER_THICKNESS = 3;
    
    
   
}
