package Code_System;

import javax.swing.JFrame;

import Code_System.GameLoop;
import Code_System.GamePanel;
import Code_System.GameWindow;
import Code_System.MainMenu;
import Code_System.VictoryPanel;

/**
 * GameWindow.java - Modified to start with MainMenu
 * Now supports panel switching between menu, game, and victory screen
 */
public class GameWindow extends JFrame {
    
    private GamePanel gamePanel;
    private GameLoop gameLoop;
    private MainMenu mainMenu;
    private VictoryPanel victoryPanel;  // ✅ NEW: Victory screen support
    
    public GameWindow() {
        // ===== WINDOW PROPERTIES =====
        setTitle("Escape Maze");
        setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // ===== START WITH MAIN MENU =====
        showMainMenu();
        
        // ===== MAKE WINDOW VISIBLE =====
        setVisible(true);
        
        System.out.println("✓ Game window created successfully!");
        System.out.println("  Size: " + Constants.SCREEN_WIDTH + "x" + Constants.SCREEN_HEIGHT);
        System.out.println("  Showing Main Menu...");
    }
    
    /**
     * Shows the main menu screen
     */
    public void showMainMenu() {
        getContentPane().removeAll();
        mainMenu = new MainMenu(this);
        add(mainMenu);
        revalidate();
        repaint();
        mainMenu.requestFocusInWindow();
        System.out.println("🏠 Main Menu displayed");
    }
    
    /**
     * Switches from menu to game (called when PLAY button clicked)
     */
 // Replace the switchToGame method (around line 46)
    public void switchToGame() {
        getContentPane().removeAll();
        
        gamePanel = new GamePanel(this);
        add(gamePanel);
        
        gameLoop = new GameLoop(gamePanel.getGameManager());
        
        // ✅ FIXED: Use resource path without "src/" prefix
        AudioManager.getInstance().switchBackgroundMusic("/assets/Sound/In_game(Mercy active).WAV");
        
        revalidate();
        repaint();
        gamePanel.requestFocusInWindow();
        
        System.out.println("🎮 Switched to game view");
        
        startNewGame();
    }
    
    /**
     * ✅ NEW METHOD: Shows the victory screen after completing all levels
     */
    public void showVictoryScreen() {
        System.out.println("🎉 Switching to Victory Screen...");
        
        // Stop the game loop and timers
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        if (gamePanel != null) {
            gamePanel.stopTimers();
        }
        
        // Clear the current content
        getContentPane().removeAll();
        
        // Create and display victory panel
        victoryPanel = new VictoryPanel(this, gamePanel.getGameManager());
        add(victoryPanel);
        
        // Refresh the display
        revalidate();
        repaint();
        victoryPanel.requestFocusInWindow();
        
        System.out.println("✅ Victory Screen displayed!");
    }
    
    /**
     * Starts a new game session
     */
    public void startNewGame() {
        gamePanel.getGameManager().startNewGame();
        gameLoop.start();  // START TIMER COUNTDOWN
        System.out.println("▶️ Game started - timer counting down!");
        System.out.println("👀 Watch the TIME display at the top center!");
    }
    
    /**
     * Stops the game
     */
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
            System.out.println("⏸️ Game stopped - timer paused!");
        }
    }
    
    /**
     * Returns to main menu (can be called from game over screen, victory screen, etc.)
     */
    public void returnToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        // ✅ STOP GAME PANEL TIMERS to prevent memory leaks
        if (gamePanel != null) {
            gamePanel.stopTimers();
        }
        
        showMainMenu();
        System.out.println("🏠 Returned to Main Menu");
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameWindow();
            }
        });
}
}