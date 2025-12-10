package Code_System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MainMenu.java - Main Menu Screen for Escape Maze
 * Features wooden styled buttons with torch decorations and thrilling vignette effect
 */
public class MainMenu extends JPanel {
    
    private GameWindow gameWindow;
    private JButton playButton;
    private JButton overviewButton;
    private JButton mechanicsButton;
    private JButton hoveredButton = null;
    private JButton exitButton;

private boolean showingExitDialog = false;
private boolean exitConfirmHovered = false;
private boolean exitCancelHovered = false;
    
    // Colors tuned to match pixel-art reference
    private static final Color BG_COLOR = new Color(26, 18, 15);           // Dark vignette base
    private static final Color WALL_COLOR = new Color(121, 92, 70);        // Brick brown
    private static final Color WALL_SHADOW = new Color(74, 54, 43);        // Mortar / edges
    private static final Color BUTTON_COLOR = new Color(120, 79, 48);      // Button wood mid
    private static final Color BUTTON_HOVER = new Color(143, 97, 60);      // Button hover wood
    private static final Color BUTTON_BORDER = new Color(255, 204, 102);   // Bright border
    private static final Color TITLE_BG = new Color(110, 71, 43);          // Title plank
    private static final Color TITLE_BORDER = new Color(255, 214, 120);    // Title glow
    private static final Color FIRE_ORANGE = new Color(255, 140, 0);       // Torch fire
    private static final Color FIRE_YELLOW = new Color(255, 200, 50);      // Torch flame
    
    public MainMenu(GameWindow window) {
        this.gameWindow = window;
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(BG_COLOR);
        setLayout(null);
        
        setupButtons();
        setupMouseListeners();
        setupKeyListener();
        
        // ✅ CHANGE THIS LINE - Match your exact filename with space and .WAV
        AudioManager.getInstance().playBackgroundMusic("/assets/Sound/Entry_game.wav");

        System.out.println("🎵 Attempting to play main menu music");
    }

    // ✅ Add this new method
    private void setupKeyListener() {
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (showingExitDialog && e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    System.out.println("❌ ESC pressed - closing exit dialog");
                    showingExitDialog = false;
                    exitConfirmHovered = false;
                    exitCancelHovered = false;
                    
                    // Restore buttons
                    playButton.setVisible(true);
                    overviewButton.setVisible(true);
                    mechanicsButton.setVisible(true);
                    exitButton.setVisible(true);
                    
                    repaint();
                }
            }
        });
    }
  

    private void setupButtons() {
        // Create custom styled buttons (centered)
        int buttonWidth = 300;
        int centerX = (Constants.SCREEN_WIDTH - buttonWidth) / 2;
        
        playButton = createMenuButton("PLAY", centerX, 350);
        overviewButton = createMenuButton("Overview", centerX, 420);
        mechanicsButton = createMenuButton("Mechanics", centerX, 490);
        exitButton = createMenuButton("EXIT", centerX, 560);
        
        // Add action listeners
        playButton.addActionListener(e -> {
            AudioManager.getInstance().stopBackgroundMusic();  
            startGame();
        });
        overviewButton.addActionListener(e -> showOverview());
        mechanicsButton.addActionListener(e -> showMechanics());
        exitButton.addActionListener(e -> exitGame());
        
        // Add buttons to panel
        add(playButton);
        add(overviewButton);
        add(mechanicsButton);
        add(exitButton);
    }

    // NEW method for exit button
    private void exitGame() {
        showingExitDialog = true;
        
        // ✅ Hide all buttons when dialog opens
        playButton.setVisible(false);
        overviewButton.setVisible(false);
        mechanicsButton.setVisible(false);
        exitButton.setVisible(false);
        
        repaint();
    }

    /**
     * Draws the custom exit confirmation dialog
     */
    /**
     * Draws the custom exit confirmation dialog
     */
    private void drawExitConfirmDialog(Graphics2D g2d) {
        if (!showingExitDialog) return;
        
        int dialogW = 600;
        int dialogH = 250;
        int dialogX = (Constants.SCREEN_WIDTH - dialogW) / 2;
        int dialogY = (Constants.SCREEN_HEIGHT - dialogH) / 2;
        
        // Dark overlay
        g2d.setColor(new Color(0, 0, 0, 240));
        g2d.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        
        // Main dialog background (cream - matching OverviewPanel)
        g2d.setColor(new Color(245, 235, 220));
        g2d.fillRoundRect(dialogX, dialogY, dialogW, dialogH, 20, 20);
        
        // Outer border (brown)
        g2d.setColor(new Color(90, 70, 50));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(dialogX, dialogY, dialogW, dialogH, 20, 20);
        
        // Inner border (yellow)
        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(dialogX + 2, dialogY + 2, dialogW - 4, dialogH - 4, 19, 19);
        
        // Title bar (dark brown)
        g2d.setColor(new Color(80, 60, 45));
        g2d.fillRoundRect(dialogX + 15, dialogY + 15, dialogW - 30, 60, 15, 15);
        
        // Title bar outer border (brown)
        g2d.setColor(new Color(90, 70, 50));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(dialogX + 15, dialogY + 15, dialogW - 30, 60, 15, 15);
        
        // Title bar inner border (yellow)
        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(dialogX + 18, dialogY + 18, dialogW - 36, 54, 13, 13);
        
        // Title text (Verdana for consistency)
        g2d.setFont(new Font("Verdana", Font.BOLD, 24));
        g2d.setColor(new Color(255, 240, 200));
        String title = "Exit Game?";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = dialogX + (dialogW - fm.stringWidth(title)) / 2;
        
        // Title shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(title, titleX + 2, dialogY + 57);
        
        // Title main
        g2d.setColor(new Color(255, 240, 200));
        g2d.drawString(title, titleX, dialogY + 55);
        
        // Message
        g2d.setFont(new Font("Verdana", Font.BOLD, 18));
        g2d.setColor(new Color(60, 45, 35));
        String message = "Are you sure you want to quit?";
        fm = g2d.getFontMetrics();
        int messageX = dialogX + (dialogW - fm.stringWidth(message)) / 2;
        g2d.drawString(message, messageX, dialogY + 130);
        
        // Buttons
        int buttonWidth = 200;
        int buttonHeight = 50;
        int buttonY = dialogY + dialogH - 80;
        int confirmButtonX = dialogX + 80;
        int cancelButtonX = dialogX + dialogW - 280;
        
        // Confirm button (brown theme)
        if (exitConfirmHovered) {
            g2d.setColor(new Color(120, 100, 80)); // Brown hover
        } else {
            g2d.setColor(new Color(100, 80, 60)); // Brown normal
        }
        g2d.fillRoundRect(confirmButtonX, buttonY, buttonWidth, buttonHeight, 12, 12);
        
        g2d.setColor(new Color(90, 70, 50));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(confirmButtonX, buttonY, buttonWidth, buttonHeight, 12, 12);
        
        g2d.setFont(new Font("Verdana", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        String confirmText = "Exit";
        fm = g2d.getFontMetrics();
        int confirmTextX = confirmButtonX + (buttonWidth - fm.stringWidth(confirmText)) / 2;
        int confirmTextY = buttonY + (buttonHeight + fm.getAscent()) / 2 - 2;
        g2d.drawString(confirmText, confirmTextX, confirmTextY);
        
        if (exitConfirmHovered) {
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(confirmButtonX - 2, buttonY - 2, buttonWidth + 4, buttonHeight + 4, 14, 14);
        }
        
        // Cancel button (lighter brown)
        if (exitCancelHovered) {
            g2d.setColor(new Color(150, 120, 90)); // Lighter brown hover
        } else {
            g2d.setColor(new Color(120, 90, 70)); // Medium brown normal
        }
        g2d.fillRoundRect(cancelButtonX, buttonY, buttonWidth, buttonHeight, 12, 12);
        
        g2d.setColor(new Color(90, 70, 50));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(cancelButtonX, buttonY, buttonWidth, buttonHeight, 12, 12);
        
        g2d.setFont(new Font("Verdana", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        String cancelText = "Cancel";
        fm = g2d.getFontMetrics();
        int cancelTextX = cancelButtonX + (buttonWidth - fm.stringWidth(cancelText)) / 2;
        int cancelTextY = buttonY + (buttonHeight + fm.getAscent()) / 2 - 2;
        g2d.drawString(cancelText, cancelTextX, cancelTextY);
        
        if (exitCancelHovered) {
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(cancelButtonX - 2, buttonY - 2, buttonWidth + 4, buttonHeight + 4, 14, 14);
        }
    }

    /**
     * Checks if point is inside rectangle (helper method)
     */
    private boolean isPointInRect(int px, int py, int rx, int ry, int rw, int rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    /**
     * Handles clicks on exit confirmation dialog
     */
    private void handleExitDialogClick(int mouseX, int mouseY) {
        int dialogW = 600;
        int dialogH = 250;
        int dialogX = (Constants.SCREEN_WIDTH - dialogW) / 2;
        int dialogY = (Constants.SCREEN_HEIGHT - dialogH) / 2;
        
        int buttonWidth = 200;
        int buttonHeight = 50;
        int buttonY = dialogY + dialogH - 80;
        int confirmButtonX = dialogX + 80;
        int cancelButtonX = dialogX + dialogW - 280;
        
        // Check Confirm (Exit) button
        if (isPointInRect(mouseX, mouseY, confirmButtonX, buttonY, buttonWidth, buttonHeight)) {
            System.out.println("✅ Exit confirmed");
            System.exit(0); // Exit the application
            return;
        }
        
        // Check Cancel button
        if (isPointInRect(mouseX, mouseY, cancelButtonX, buttonY, buttonWidth, buttonHeight)) {
            System.out.println("❌ Exit canceled");
            showingExitDialog = false;
            exitConfirmHovered = false;
            exitCancelHovered = false;
            
            // ✅ Restore buttons when dialog closes
            playButton.setVisible(true);
            overviewButton.setVisible(true);
            mechanicsButton.setVisible(true);
            exitButton.setVisible(true);
            
            repaint();
            return;
        }
        
        // Click outside dialog = cancel
        if (!isPointInRect(mouseX, mouseY, dialogX, dialogY, dialogW, dialogH)) {
            System.out.println("❌ Clicked outside - canceling exit");
            showingExitDialog = false;
            exitConfirmHovered = false;
            exitCancelHovered = false;
            
            // ✅ Restore buttons when dialog closes
            playButton.setVisible(true);
            overviewButton.setVisible(true);
            mechanicsButton.setVisible(true);
            exitButton.setVisible(true);
            
            repaint();
        }
    }

    /**
     * Checks hover state for exit dialog buttons
     */
    private void checkExitDialogHover(int mouseX, int mouseY) {
        int dialogW = 600;
        int dialogH = 250;
        int dialogX = (Constants.SCREEN_WIDTH - dialogW) / 2;
        int dialogY = (Constants.SCREEN_HEIGHT - dialogH) / 2;
        
        int buttonWidth = 200;
        int buttonHeight = 50;
        int buttonY = dialogY + dialogH - 80;
        int confirmButtonX = dialogX + 80;
        int cancelButtonX = dialogX + dialogW - 280;
        
        boolean wasConfirmHovered = exitConfirmHovered;
        boolean wasCancelHovered = exitCancelHovered;
        
        exitConfirmHovered = isPointInRect(mouseX, mouseY, confirmButtonX, buttonY, buttonWidth, buttonHeight);
        exitCancelHovered = isPointInRect(mouseX, mouseY, cancelButtonX, buttonY, buttonWidth, buttonHeight);
        
        // Change cursor when hovering
        if (exitConfirmHovered || exitCancelHovered) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
        
        // Repaint if hover changed
        if (wasConfirmHovered != exitConfirmHovered || wasCancelHovered != exitCancelHovered) {
            repaint();
        }
    }

    
    private JButton createMenuButton(String text, int x, int y) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();

                // Drop shadow
                g2d.setColor(new Color(0, 0, 0, 130));
                g2d.fillRoundRect(6, 10, w - 12, h - 6, 16, 16);

                // Button wooden background (pixel board)
                Color base = (this == hoveredButton) ? BUTTON_HOVER : BUTTON_COLOR;
                GradientPaint gp = new GradientPaint(
                        0, 4, base.brighter(),
                        0, h, base.darker()
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(4, 2, w - 8, h - 10, 14, 14);

                // Inner wood slats
                g2d.setColor(new Color(95, 60, 41, 170));
                for (int i = 12; i < h - 16; i += 2) {
                    g2d.drawLine(10, i, w - 10, i);
                }

                // Golden border
                g2d.setColor(BUTTON_BORDER);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(4, 2, w - 8, h - 10, 14, 14);

                // Small studs
                g2d.setColor(new Color(255, 248, 220));
                g2d.fillOval(10, 8, 6, 6);
                g2d.fillOval(w - 16, 8, 6, 6);
                g2d.fillOval(10, h - 18, 6, 6);
                g2d.fillOval(w - 16, h - 18, 6, 6);
                
                // Text
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (w - fm.stringWidth(getText())) / 2;
                int textY = (h + fm.getAscent()) / 2 - 2;
                
                // Text shadow
                g2d.setColor(new Color(0, 0, 0, 160));
                g2d.drawString(getText(), textX + 2, textY + 3);

                // Text main (warm cream)
                g2d.setColor(new Color(255, 239, 207));
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setBounds(x, y, 320, 60);
        button.setFont(new Font("Arial", Font.BOLD, 26));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoveredButton = button;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredButton = null;
                repaint();
            }
        });
        
        return button;
    }
    
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (showingExitDialog) {
                    handleExitDialogClick(e.getX(), e.getY());
                }
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (showingExitDialog) {
                    checkExitDialogHover(e.getX(), e.getY());
                }
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw stone wall background pattern + vignette
        drawStoneWalls(g2d);
        drawEdgeVignette(g2d);
        
        // Draw tagline
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String tagline = "FIND THE CLUE. ESCAPE THE MAZE.";
        FontMetrics fm = g2d.getFontMetrics();
        int taglineX = (getWidth() - fm.stringWidth(tagline)) / 2;
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.drawString(tagline, taglineX, 82);
        g2d.setColor(new Color(220, 196, 171));
        g2d.drawString(tagline, taglineX, 78);
        
        // Draw title sign
        drawTitleSign(g2d);
        
        // Draw torches
        drawTorch(g2d, 150, 120);
        drawTorch(g2d, getWidth() - 200, 120);
        
        // ✅ Draw exit confirmation dialog on top of everything
        drawExitConfirmDialog(g2d);
    }
    
    private void drawStoneWalls(Graphics2D g2d) {
        int brickW = 80;
        int brickH = 40;

        for (int y = 0; y < getHeight(); y += brickH) {
            for (int x = 0; x < getWidth(); x += brickW) {
                int offsetX = (y / brickH % 2 == 0) ? 0 : brickW / 2;

                int shade = (int) (Math.random() * 20) - 10;
                g2d.setColor(new Color(
                        Math.max(0, WALL_COLOR.getRed() + shade),
                        Math.max(0, WALL_COLOR.getGreen() + shade),
                        Math.max(0, WALL_COLOR.getBlue() + shade)
                ));

                g2d.fillRect(x + offsetX, y, brickW - 2, brickH - 2);

                g2d.setColor(BG_COLOR);
                g2d.drawRect(x + offsetX, y, brickW - 2, brickH - 2);
            }
        }
    }
    
    private void drawEdgeVignette(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        // Vignette intensity
        int vignetteDepth = 200;
        int maxAlpha = 180;
        
        // Top vignette
        GradientPaint topGradient = new GradientPaint(
            0, 0, new Color(0, 0, 0, maxAlpha),
            0, vignetteDepth, new Color(0, 0, 0, 0)
        );
        g2d.setPaint(topGradient);
        g2d.fillRect(0, 0, width, vignetteDepth);
        
        // Bottom vignette
        GradientPaint bottomGradient = new GradientPaint(
            0, height - vignetteDepth, new Color(0, 0, 0, 0),
            0, height, new Color(0, 0, 0, maxAlpha)
        );
        g2d.setPaint(bottomGradient);
        g2d.fillRect(0, height - vignetteDepth, width, vignetteDepth);
        
        // Left vignette
        GradientPaint leftGradient = new GradientPaint(
            0, 0, new Color(0, 0, 0, maxAlpha),
            vignetteDepth, 0, new Color(0, 0, 0, 0)
        );
        g2d.setPaint(leftGradient);
        g2d.fillRect(0, 0, vignetteDepth, height);
        
        // Right vignette
        GradientPaint rightGradient = new GradientPaint(
            width - vignetteDepth, 0, new Color(0, 0, 0, 0),
            width, 0, new Color(0, 0, 0, maxAlpha)
        );
        g2d.setPaint(rightGradient);
        g2d.fillRect(width - vignetteDepth, 0, vignetteDepth, height);
        
        // Corner shadows for extra drama
        RadialGradientPaint topLeftCorner = new RadialGradientPaint(
            0, 0, vignetteDepth * 1.5f,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(0, 0, 0, maxAlpha + 50), new Color(0, 0, 0, 0)}
        );
        g2d.setPaint(topLeftCorner);
        g2d.fillRect(0, 0, vignetteDepth * 2, vignetteDepth * 2);
        
        RadialGradientPaint topRightCorner = new RadialGradientPaint(
            width, 0, vignetteDepth * 1.5f,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(0, 0, 0, maxAlpha + 50), new Color(0, 0, 0, 0)}
        );
        g2d.setPaint(topRightCorner);
        g2d.fillRect(width - vignetteDepth * 2, 0, vignetteDepth * 2, vignetteDepth * 2);
        
        RadialGradientPaint bottomLeftCorner = new RadialGradientPaint(
            0, height, vignetteDepth * 1.5f,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(0, 0, 0, maxAlpha + 50), new Color(0, 0, 0, 0)}
        );
        g2d.setPaint(bottomLeftCorner);
        g2d.fillRect(0, height - vignetteDepth * 2, vignetteDepth * 2, vignetteDepth * 2);
        
        RadialGradientPaint bottomRightCorner = new RadialGradientPaint(
            width, height, vignetteDepth * 1.5f,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(0, 0, 0, maxAlpha + 50), new Color(0, 0, 0, 0)}
        );
        g2d.setPaint(bottomRightCorner);
        g2d.fillRect(width - vignetteDepth * 2, height - vignetteDepth * 2, vignetteDepth * 2, vignetteDepth * 2);
    }
    
    private void drawTitleSign(Graphics2D g2d) {
        int signW = 560;
        int signH = 140;
        int signX = (getWidth() - signW) / 2;
        int signY = 130;

        // Shadow backdrop
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(signX - 20, signY - 12, signW + 40, signH + 24, 30, 30);

        // Side braces (kept perfectly symmetrical)
        Color braceColor = new Color(88, 56, 33);
        g2d.setColor(braceColor);
        int braceWidth = 36;
        int braceInset = 12;
        int braceOverlap = 8;
        int braceHeight = signH - braceInset * 2;
        int leftBraceX = signX - (braceWidth - braceOverlap);
        int rightBraceX = signX + signW - braceOverlap;
        g2d.fillRoundRect(leftBraceX, signY + braceInset, braceWidth, braceHeight, 16, 16);
        g2d.fillRoundRect(rightBraceX, signY + braceInset, braceWidth, braceHeight, 16, 16);

        int capSize = 64;
        int capOverlap = 12;
        drawCornerCap(g2d, signX - (capSize - capOverlap), signY + 8);
        drawCornerCap(g2d, signX + signW - capOverlap, signY + 8);

        // Main plank gradient
        GradientPaint plankPaint = new GradientPaint(
                signX, signY,
                TITLE_BG.brighter(),
                signX, signY + signH,
                TITLE_BG.darker()
        );
        g2d.setPaint(plankPaint);
        g2d.fillRoundRect(signX, signY, signW, signH, 20, 20);

        // Thin inner highlight line
        g2d.setColor(new Color(255, 229, 175));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(signX + 6, signY + 6, signW - 12, signH - 12, 16, 16);

        // Golden outer border
        g2d.setColor(TITLE_BORDER);
        g2d.setStroke(new BasicStroke(7f));
        g2d.drawRoundRect(signX + 1, signY + 1, signW - 2, signH - 2, 22, 22);

        // Wood lines
        g2d.setColor(new Color(89, 55, 33, 160));
        for (int i = 18; i < signH - 14; i += 10) {
            g2d.drawLine(signX + 18, signY + i, signX + signW - 18, signY + i);
        }

        // Title text
        g2d.setFont(new Font("Arial", Font.BOLD, 74));
        String title = "Escape Maze";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = signX + (signW - fm.stringWidth(title)) / 2;
        int titleY = signY + (signH + fm.getAscent()) / 2 - 5;
        
        // Title shadow (embossed effect)
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.drawString(title, titleX + 3, titleY + 3);
        
        // Title highlight
        g2d.setColor(new Color(255, 240, 200));
        g2d.drawString(title, titleX - 1, titleY - 1);
        
        // Title main
        g2d.setColor(new Color(240, 220, 180));
        g2d.drawString(title, titleX, titleY);
    }
    
    private void drawCornerCap(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(92, 59, 36));
        g2d.fillRoundRect(x, y, 64, 64, 18, 18);
        g2d.setColor(new Color(64, 38, 24));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(x, y, 64, 64, 18, 18);
        g2d.setColor(new Color(170, 112, 71));
        g2d.fillOval(x + 22, y + 22, 20, 20);
    }
    
    private void drawTorch(Graphics2D g2d, int x, int y) {
        int centerX = x + 22;
        int centerY = y + 22;
        int glowSize = 80;

        // Big circular glow centered on flame
        g2d.setColor(new Color(255, 180, 80, 95));
        g2d.fillOval(centerX - glowSize / 2, centerY - glowSize / 2, glowSize, glowSize);

        // Dark mount plate
        g2d.setColor(new Color(32, 27, 25));
        g2d.fillRoundRect(centerX - 12, y + 44, 24, 20, 6, 6);

        // Torch handle
        g2d.setColor(new Color(118, 74, 47));
        g2d.fillRoundRect(centerX - 4, y + 12, 8, 60, 4, 4);
        g2d.setColor(new Color(79, 45, 30));
        g2d.fillRect(centerX - 4, y + 38, 8, 8);

        // Flame outer polygon (pixel diamond)
        int baseY = y + 20;
        int[] outerX = {centerX, centerX - 12, centerX - 4, centerX - 10, centerX,
                centerX + 10, centerX + 4, centerX + 12};
        int[] outerY = {baseY - 32, baseY - 8, baseY, baseY + 18, baseY + 28,
                baseY + 18, baseY, baseY - 8};
        g2d.setColor(new Color(255, 142, 62));
        g2d.fillPolygon(outerX, outerY, outerX.length);

        // Mid flame
        int[] midX = {centerX, centerX - 8, centerX - 3, centerX - 6, centerX,
                centerX + 6, centerX + 3, centerX + 8};
        int[] midY = {baseY - 22, baseY - 4, baseY + 4, baseY + 12, baseY + 18,
                baseY + 12, baseY + 4, baseY - 4};
        g2d.setColor(new Color(255, 187, 92));
        g2d.fillPolygon(midX, midY, midX.length);

        // Inner flame
        int[] innerX = {centerX, centerX - 4, centerX - 1, centerX + 1, centerX + 4};
        int[] innerY = {baseY - 10, baseY + 2, baseY + 10, baseY + 2, baseY - 10};
        g2d.setColor(new Color(255, 235, 160));
        g2d.fillPolygon(innerX, innerY, innerX.length);

        // Floating sparks
        g2d.setColor(new Color(255, 220, 150, 200));
        g2d.fillOval(centerX - 3, baseY - 40, 6, 6);
        g2d.fillOval(centerX + 10, baseY - 30, 4, 4);
        g2d.fillOval(centerX - 14, baseY - 28, 4, 4);
    }

    
 // Button actions
    private void startGame() {
        System.out.println("Starting game...");
        if (gameWindow != null) {
            gameWindow.switchToGame(); 
        }
    }

    private void showOverview() {
        System.out.println("Showing overview...");
        if (gameWindow != null) {
            // --- ADD THIS LOGIC ---
            gameWindow.getContentPane().removeAll();
            OverviewPanel overviewPanel = new OverviewPanel(gameWindow);
            gameWindow.add(overviewPanel);
            gameWindow.revalidate();
            gameWindow.repaint();
            overviewPanel.requestFocusInWindow();
            System.out.println(" Overview Panel displayed");
            // --- END ADDED LOGIC ---
        }
    }

    private void showMechanics() {
        System.out.println("Showing mechanics...");
        if (gameWindow != null) {
            // --- ADD THIS LOGIC ---
            gameWindow.getContentPane().removeAll();
            MechanicsPanel mechanicsPanel = new MechanicsPanel(gameWindow);
            gameWindow.add(mechanicsPanel);
            gameWindow.revalidate();
            gameWindow.repaint();
            mechanicsPanel.requestFocusInWindow();
            System.out.println(" Mechanics Panel displayed");
            // --- END ADDED LOGIC ---
        }
    }
    
    
 
}