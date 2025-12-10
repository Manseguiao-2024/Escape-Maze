package Code_System;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GameLoop.java
 * Manages the continuous game update loop for the timer system
 */
public class GameLoop {
    
    private GameManager gameManager;
    private Timer gameTimer;
    
    // Update every 1000ms (1 time per second) for countdown
    private static final int UPDATE_INTERVAL_MS = 1000;
    
    public GameLoop(GameManager gameManager) {
        this.gameManager = gameManager;
        
        // Create Swing Timer that fires every UPDATE_INTERVAL_MS
        this.gameTimer = new Timer(UPDATE_INTERVAL_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
            }
        });
    }
    
    /**
     * Start the game loop - call this when game starts
     */
    public void start() {
        if (!gameTimer.isRunning()) {
            gameTimer.start();
            System.out.println("[GameLoop] Timer started - countdown active!");
        }
    }
    
    /**
     * Stop the game loop - call this when game ends
     */
    public void stop() {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
            System.out.println("[GameLoop] Timer stopped");
        }
    }
    
    /**
     * Restart the game loop
     */
    public void restart() {
        stop();
        start();
    }
    
    /**
     * Called automatically every second
     */
    private void updateGame() {
        // Pass exactly 1.0 second as deltaTime
        // This ensures consistent countdown regardless of timer precision
        gameManager.update(1.0);
    }
    
    /**
     * Check if loop is running
     */
    public boolean isRunning() {
        return gameTimer.isRunning();
    }
}