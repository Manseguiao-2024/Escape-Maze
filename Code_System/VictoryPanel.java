package Code_System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * VictoryPanel.java - Victory Screen showing game statistics
 * Displays after completing all 5 levels and final story frame
 */
public class VictoryPanel extends JPanel {
    
    private GameWindow gameWindow;
    private GameManager gameManager;
    
    // Enhanced victory colors with richer palette
    private static final Color BG_COLOR = new Color(20, 15, 10);
    private static final Color BG_HIGHLIGHT = new Color(45, 35, 25);
    private static final Color PANEL_BG = new Color(180, 160, 140);
    private static final Color PANEL_BORDER = new Color(60, 50, 40);
    
    // Triumphant gold palette
    private static final Color GOLD_BRIGHT = new Color(255, 223, 0);
    private static final Color GOLD_RICH = new Color(218, 165, 32);
    private static final Color GOLD_DEEP = new Color(184, 134, 11);
    
    // Accent colors
    private static final Color VICTORY_GLOW = new Color(255, 215, 0, 100);
    private static final Color TEXT_PRIMARY = new Color(40, 30, 20);
    private static final Color TEXT_SECONDARY = new Color(70, 55, 40);
    private static final Color WHITE = Color.WHITE;
    
    // Button colors
    private static final Color BUTTON_COLOR = new Color(139, 90, 43);
    private static final Color BUTTON_HOVER = new Color(184, 134, 11);
    private static final Color BUTTON_BORDER = new Color(101, 67, 33);
    
    // Stats panel colors
    private static final Color STATS_BG = new Color(155, 135, 115);
    private static final Color STATS_HIGHLIGHT = new Color(240, 230, 210);
    
    private boolean returnButtonHovered = false;
    private int returnButtonX, returnButtonY, returnButtonWidth, returnButtonHeight;
    
    // Statistics from game
    private int totalTimeSeconds;
    private int resetsToLevel1;
    private int longestStreak;
    
    public VictoryPanel(GameWindow window, GameManager manager) {
        this.gameWindow = window;
        this.gameManager = manager;
        
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(BG_COLOR);
        setFocusable(true);
        
        // Get statistics from game manager
        calculateStatistics();
        
        // Setup mouse listeners for button
        setupMouseListeners();
        
        System.out.println("✓ Victory Panel initialized");
    }
    
    private void calculateStatistics() {
        // Get total time spent (you'll need to track this in GameManager)
        totalTimeSeconds = gameManager.getTotalGameTime();
        
        // Get resets to level 1 (from LevelHistoryManager)
        resetsToLevel1 = gameManager.getLevelHistoryManager().getResetCount();
        
        // Get longest streak (highest level reached without reset)
        longestStreak = gameManager.getLevelHistoryManager().getLongestStreak();
        
        System.out.println(" Victory Statistics:");
        System.out.println("   Total Time: " + formatTime(totalTimeSeconds));
        System.out.println("   Resets: " + resetsToLevel1);
        System.out.println("   Longest Streak: " + longestStreak + " levels");
    }
    
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseHover(e.getX(), e.getY());
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw enhanced background with radiant glow
        drawBackground(g2d);
        
        // Draw main victory panel
        drawVictoryPanel(g2d);
        
        // Draw return to menu button
        drawReturnButton(g2d);
    }
    
    private void drawBackground(Graphics2D g2d) {
        // Deep dark background
        g2d.setColor(BG_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Multiple radial glows for triumphant effect
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Outer golden glow
        RadialGradientPaint outerGlow = new RadialGradientPaint(
            centerX, centerY, 
            getWidth() / 1.8f,
            new float[]{0f, 0.5f, 1.0f},
            new Color[]{
                new Color(80, 60, 30, 50),
                new Color(50, 40, 25, 20),
                BG_COLOR
            }
        );
        g2d.setPaint(outerGlow);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Inner warm highlight
        RadialGradientPaint innerGlow = new RadialGradientPaint(
            centerX, centerY - 50, 
            400,
            new float[]{0f, 1.0f},
            new Color[]{
                new Color(255, 215, 0, 30),
                new Color(255, 215, 0, 0)
            }
        );
        g2d.setPaint(innerGlow);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void drawVictoryPanel(Graphics2D g2d) {
        int panelWidth = 900;
        int panelHeight = 650;
        int panelX = (getWidth() - panelWidth) / 2;
        int panelY = (getHeight() - panelHeight) / 2 - 30;
        
        // === GLOWING OUTER BORDER ===
        // Outer glow effect
        for (int i = 8; i > 0; i--) {
            int alpha = 20 - (i * 2);
            g2d.setColor(new Color(255, 215, 0, alpha));
            g2d.fillRoundRect(panelX - i*2, panelY - i*2, 
                             panelWidth + i*4, panelHeight + i*4, 25, 25);
        }
        
        // Triple gold border for richness
        g2d.setColor(GOLD_DEEP);
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        g2d.setColor(GOLD_RICH);
        g2d.fillRoundRect(panelX + 5, panelY + 5, panelWidth - 10, panelHeight - 10, 18, 18);
        
        g2d.setColor(GOLD_BRIGHT);
        g2d.fillRoundRect(panelX + 8, panelY + 8, panelWidth - 16, panelHeight - 16, 16, 16);
        
        // === MAIN PANEL BACKGROUND ===
        g2d.setColor(PANEL_BG);
        g2d.fillRoundRect(panelX + 12, panelY + 12, panelWidth - 24, panelHeight - 24, 15, 15);
        
        // === TITLE BANNER WITH GRADIENT ===
        int titleBannerHeight = 110;
        int bannerX = panelX + 30;
        int bannerY = panelY + 30;
        
        // Banner gradient background
        GradientPaint bannerGradient = new GradientPaint(
            bannerX, bannerY,
            new Color(139, 90, 43),
            bannerX, bannerY + titleBannerHeight,
            new Color(101, 67, 33)
        );
        g2d.setPaint(bannerGradient);
        g2d.fillRoundRect(bannerX, bannerY, panelWidth - 60, titleBannerHeight, 15, 15);
        
        // Banner highlight
        g2d.setColor(new Color(255, 223, 0, 40));
        g2d.fillRoundRect(bannerX + 5, bannerY + 5, panelWidth - 70, 35, 10, 10);
        
        // Banner border
        g2d.setColor(GOLD_RICH);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(bannerX, bannerY, panelWidth - 60, titleBannerHeight, 15, 15);
        
        // Inner banner accent
        g2d.setColor(GOLD_DEEP);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(bannerX + 3, bannerY + 3, panelWidth - 66, titleBannerHeight - 6, 12, 12);
        
        // Title text with shadow
        g2d.setFont(new Font("Arial", Font.BOLD, 64));
        String title = "VICTORY!";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = panelX + (panelWidth - fm.stringWidth(title)) / 2;
        int titleY = panelY + 98;
        
        // Text shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(title, titleX + 3, titleY + 3);
        
        // Main text
        g2d.setColor(GOLD_BRIGHT);
        g2d.drawString(title, titleX, titleY);
        
        // Text highlight
        g2d.setColor(new Color(255, 255, 200, 150));
        g2d.drawString(title, titleX - 1, titleY - 1);
        
        // === CONGRATULATIONS MESSAGE ===
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(TEXT_PRIMARY);
        String congrats = "Congratulations! You've conquered all 5 levels!";
        fm = g2d.getFontMetrics();
        int congratsX = panelX + (panelWidth - fm.stringWidth(congrats)) / 2;
        g2d.drawString(congrats, congratsX, panelY + 175);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(TEXT_SECONDARY);
        String subtitle = "You've proven yourself worthy of escaping the maze!";
        fm = g2d.getFontMetrics();
        int subtitleX = panelX + (panelWidth - fm.stringWidth(subtitle)) / 2;
        g2d.drawString(subtitle, subtitleX, panelY + 205);
        
        // === STATISTICS SECTION ===
        int statsY = panelY + 255;
        int statsBoxWidth = panelWidth - 100;
        int statsBoxHeight = 290;
        
        // Stats box gradient background
        GradientPaint statsGradient = new GradientPaint(
            panelX + 50, statsY,
            STATS_BG,
            panelX + 50, statsY + statsBoxHeight,
            new Color(135, 115, 95)
        );
        g2d.setPaint(statsGradient);
        g2d.fillRoundRect(panelX + 50, statsY, statsBoxWidth, statsBoxHeight, 12, 12);
        
        // Stats highlight strip at top
        g2d.setColor(STATS_HIGHLIGHT);
        g2d.fillRoundRect(panelX + 55, statsY + 5, statsBoxWidth - 10, 50, 10, 10);
        
        // Stats border
        g2d.setColor(GOLD_DEEP);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(panelX + 50, statsY, statsBoxWidth, statsBoxHeight, 12, 12);
        
        // Stats title
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(TEXT_PRIMARY);
        String statsTitle = "YOUR STATISTICS";
        fm = g2d.getFontMetrics();
        int statsTitleX = panelX + (panelWidth - fm.stringWidth(statsTitle)) / 2;
        g2d.drawString(statsTitle, statsTitleX, statsY + 40);
        
        // Divider line with gradient
        GradientPaint dividerGradient = new GradientPaint(
            panelX + 80, statsY + 60,
            new Color(184, 134, 11, 50),
            panelX + panelWidth / 2, statsY + 60,
            GOLD_RICH,
            true
        );
        g2d.setPaint(dividerGradient);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(panelX + 80, statsY + 65, panelX + panelWidth - 80, statsY + 65);
        
        // === INDIVIDUAL STATS ===
        int statLineY = statsY + 110;
        int statLineSpacing = 55;
        
        // Stat 1: Total Time
        drawStatLine(g2d, "Total Time:", formatTime(totalTimeSeconds), 
                     panelX + 120, statLineY);
        
        // Stat 2: Resets
        statLineY += statLineSpacing;
        drawStatLine(g2d, "Resets to Level 1:", String.valueOf(resetsToLevel1), 
                     panelX + 120, statLineY);
        
        // Stat 3: Longest Streak
        statLineY += statLineSpacing;
        drawStatLine(g2d, "Longest Streak:", longestStreak + " levels", 
                     panelX + 120, statLineY);
        
        // Stat 4: Final Level
        statLineY += statLineSpacing;
        drawStatLine(g2d, "Levels Completed:", "5 / 5", 
                     panelX + 120, statLineY);
    }
    
    private void drawStatLine(Graphics2D g2d, String label, String value, int x, int y) {
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(TEXT_PRIMARY);
        g2d.drawString(label, x, y);
        
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        
        // Value with shadow
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.drawString(value, x + labelWidth + 22, y + 2);
        
        g2d.setColor(GOLD_BRIGHT);
        g2d.drawString(value, x + labelWidth + 20, y);
    }
    
    private void drawReturnButton(Graphics2D g2d) {
        returnButtonWidth = 350;
        returnButtonHeight = 65;
        returnButtonX = (getWidth() - returnButtonWidth) / 2;
        returnButtonY = getHeight() - 120;
        
        // Button glow when hovered
        if (returnButtonHovered) {
            for (int i = 4; i > 0; i--) {
                int alpha = 40 - (i * 8);
                g2d.setColor(new Color(184, 134, 11, alpha));
                g2d.fillRoundRect(returnButtonX - i*3, returnButtonY - i*3, 
                                 returnButtonWidth + i*6, returnButtonHeight + i*6, 18, 18);
            }
        }
        
        // Button gradient background
        GradientPaint buttonGradient;
        if (returnButtonHovered) {
            buttonGradient = new GradientPaint(
                returnButtonX, returnButtonY,
                BUTTON_HOVER,
                returnButtonX, returnButtonY + returnButtonHeight,
                new Color(218, 165, 32)
            );
        } else {
            buttonGradient = new GradientPaint(
                returnButtonX, returnButtonY,
                BUTTON_COLOR,
                returnButtonX, returnButtonY + returnButtonHeight,
                new Color(101, 67, 33)
            );
        }
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(returnButtonX, returnButtonY, returnButtonWidth, returnButtonHeight, 15, 15);
        
        // Button highlight
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.fillRoundRect(returnButtonX + 5, returnButtonY + 5, returnButtonWidth - 10, 20, 10, 10);
        
        // Button borders
        g2d.setColor(BUTTON_BORDER);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(returnButtonX, returnButtonY, returnButtonWidth, returnButtonHeight, 15, 15);
        
        g2d.setColor(returnButtonHovered ? GOLD_BRIGHT : GOLD_DEEP);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(returnButtonX + 3, returnButtonY + 3, 
                         returnButtonWidth - 6, returnButtonHeight - 6, 12, 12);
        
        // Button text with shadow
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String buttonText = "Return to Main Menu";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = returnButtonX + (returnButtonWidth - fm.stringWidth(buttonText)) / 2;
        int textY = returnButtonY + (returnButtonHeight + fm.getAscent()) / 2 - 2;
        
        // Text shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(buttonText, textX + 2, textY + 2);
        
        // Main text
        g2d.setColor(WHITE);
        g2d.drawString(buttonText, textX, textY);
    }
    
    private void handleMouseClick(int mouseX, int mouseY) {
        // Check if return button was clicked
        if (isPointInRect(mouseX, mouseY, returnButtonX, returnButtonY, 
                         returnButtonWidth, returnButtonHeight)) {
            System.out.println("✓ Return to Main Menu clicked");
            returnToMainMenu();
        }
    }
    
    private void handleMouseHover(int mouseX, int mouseY) {
        boolean wasHovered = returnButtonHovered;
        returnButtonHovered = isPointInRect(mouseX, mouseY, returnButtonX, returnButtonY, 
                                           returnButtonWidth, returnButtonHeight);
        
        // Change cursor
        if (returnButtonHovered) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
        
        // Repaint if hover changed
        if (wasHovered != returnButtonHovered) {
            repaint();
        }
    }
    
    private boolean isPointInRect(int px, int py, int rx, int ry, int rw, int rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }
    
    private void returnToMainMenu() {
        if (gameWindow != null) {
            System.out.println(" Returning to Main Menu from Victory Screen...");
            gameWindow.returnToMainMenu();
        }
    }
    
    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}
