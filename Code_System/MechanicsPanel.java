package Code_System;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * MechanicsPanel.java - Game Mechanics and Controls Screen
 * Shows detailed gameplay instructions and keyboard controls
 */
public class MechanicsPanel extends JPanel {
    
    private GameWindow gameWindow;
    private JButton backButton;
    private boolean backHovered = false;
    
    // Updated color scheme - brown and cream (same as Overview)
    private static final Color BG_COLOR = new Color(70, 55, 45);
    private static final Color PANEL_COLOR = new Color(245, 235, 220);
    private static final Color BORDER_COLOR = new Color(90, 70, 50);
    private static final Color TEXT_COLOR = new Color(60, 45, 35);
    private static final Color TITLE_COLOR = new Color(120, 90, 70);
    private static final Color BUTTON_COLOR = new Color(100, 80, 60);
    private static final Color BUTTON_HOVER = new Color(120, 100, 80);
    private static final Color KEY_BG = new Color(200, 180, 160);
    private static final Color HIGHLIGHT_COLOR = new Color(150, 120, 90);
    
    public MechanicsPanel(GameWindow window) {
        this.gameWindow = window;
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(BG_COLOR);
        setLayout(null);
        
        setupBackButton();
        setupMouseListeners();
    }
    
    private void setupBackButton() {
        backButton = createStyledButton("← BACK TO MENU", 50, Constants.SCREEN_HEIGHT - 125);
        backButton.addActionListener(e -> returnToMenu());
        add(backButton);
    }
    
    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (backHovered && this == backButton) {
                    g2d.setColor(BUTTON_HOVER);
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), textX + 2, textY + 2);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setBounds(x, y, 250, 50);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backHovered = false;
                repaint();
            }
        });
        
        return button;
    }
    
    private void setupMouseListeners() {
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        drawStoneWalls(g2d);
        drawEdgeVignette(g2d);
        drawContentPanel(g2d);
    }
    
    private void drawStoneWalls(Graphics2D g2d) {
        int brickW = 80;
        int brickH = 40;
        
        for (int y = 0; y < getHeight(); y += brickH) {
            for (int x = 0; x < getWidth(); x += brickW) {
                int offsetX = (y / brickH % 2 == 0) ? 0 : brickW / 2;
                int shade = (int)(Math.random() * 20) - 10;
                
                g2d.setColor(new Color(
                    Math.max(0, Math.min(255, 100 + shade)),
                    Math.max(0, Math.min(255, 75 + shade)),
                    Math.max(0, Math.min(255, 60 + shade))
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
        int vignetteDepth = 200;
        int maxAlpha = 180;
        
        GradientPaint topGradient = new GradientPaint(
            0, 0, new Color(0, 0, 0, maxAlpha),
            0, vignetteDepth, new Color(0, 0, 0, 0)
        );
        g2d.setPaint(topGradient);
        g2d.fillRect(0, 0, width, vignetteDepth);
        
        GradientPaint bottomGradient = new GradientPaint(
            0, height - vignetteDepth, new Color(0, 0, 0, 0),
            0, height, new Color(0, 0, 0, maxAlpha)
        );
        g2d.setPaint(bottomGradient);
        g2d.fillRect(0, height - vignetteDepth, width, vignetteDepth);
        
        GradientPaint leftGradient = new GradientPaint(
            0, 0, new Color(0, 0, 0, maxAlpha),
            vignetteDepth, 0, new Color(0, 0, 0, 0)
        );
        g2d.setPaint(leftGradient);
        g2d.fillRect(0, 0, vignetteDepth, height);
        
        GradientPaint rightGradient = new GradientPaint(
            width - vignetteDepth, 0, new Color(0, 0, 0, 0),
            width, 0, new Color(0, 0, 0, maxAlpha)
        );
        g2d.setPaint(rightGradient);
        g2d.fillRect(width - vignetteDepth, 0, vignetteDepth, height);
        
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
    
    private void drawContentPanel(Graphics2D g2d) {
        int panelW = 950;
        int panelH = 630;
        int panelX = (getWidth() - panelW) / 2;
        int panelY = (getHeight() - panelH) / 2 - 20;
        
        // Main panel background (cream)
        g2d.setColor(PANEL_COLOR);
        g2d.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        
        // Inner shadow
        g2d.setColor(new Color(220, 210, 195));
        g2d.fillRoundRect(panelX + 2, panelY + 2, panelW - 4, panelH - 4, 18, 18);
        g2d.setColor(PANEL_COLOR);
        g2d.fillRoundRect(panelX + 2, panelY + 2, panelW - 4, panelH - 4, 18, 18);
        
        // Panel border (brown)
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        
        // Inner border highlight (YELLOW)
        g2d.setColor(new Color(255, 215, 0)); // Golden yellow
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(panelX + 2, panelY + 2, panelW - 4, panelH - 4, 19, 19);
        
        // Title
        drawTitle(g2d, panelX, panelY, panelW);
        
        // Content
        drawContent(g2d, panelX, panelY, panelW);
    }
    
    private void drawCornerDecoration(Graphics2D g2d, int x, int y) {
        
    }
    
    private void drawTitle(Graphics2D g2d, int x, int y, int width) {
        // Title background (dark brown)
        g2d.setColor(new Color(80, 60, 45));
        g2d.fillRoundRect(x + 30, y + 20, width - 60, 70, 15, 15);
        
        // Outer title border (brown)
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x + 30, y + 20, width - 60, 70, 15, 15);
        
        // Inner title border (YELLOW)
        g2d.setColor(new Color(255, 215, 0)); // golden yellow
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x + 33, y + 23, width - 66, 64, 13, 13);
        
        // Title text (Verdana)
        g2d.setFont(new Font("Verdana", Font.BOLD, 34));
        String title = "GAME MECHANICS";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = x + (width - fm.stringWidth(title)) / 2;
        
        // Title shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(title, titleX + 2, y + 67);
        
        // Title main (cream)
        g2d.setColor(new Color(255, 240, 200));
        g2d.drawString(title, titleX, y + 65);
    }
    
    private void drawContent(Graphics2D g2d, int x, int y, int width) {
        int leftColX = x + 50;
        int rightColX = x + width / 2 + 20;
        int colWidth = width / 2 - 70;
        int contentY = y + 120;
        
        // ========== LEFT COLUMN: KEYBOARD CONTROLS ==========
        drawControlsSection(g2d, leftColX, contentY, colWidth);
        
        // ========== RIGHT COLUMN: GAMEPLAY MECHANICS ==========
        drawMechanicsSection(g2d, rightColX, contentY, colWidth);
    }
    
    private void drawControlsSection(Graphics2D g2d, int x, int y, int width) {
        // Section title (Verdana)
        g2d.setFont(new Font("Verdana", Font.BOLD, 20));
        g2d.setColor(TITLE_COLOR);
        g2d.drawString("KEYBOARD CONTROLS", x, y);
        
        y += 30;
        
        // Movement section
        g2d.setFont(new Font("Verdana", Font.BOLD, 15));
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.drawString("Movement:", x, y);
        
        y += 30;
        
        // Draw keyboard controls
        String[][] controls = {
            {"W / ↑", "Move Up"},
            {"S / ↓", "Move Down"},
            {"A / ←", "Move Left"},
            {"D / →", "Move Right"}
        };
        
        for (String[] control : controls) {
            drawKeyBinding(g2d, x, y, control[0], control[1]);
            y += 40;
        }
        
        y += 20;
        
        // Other controls section
        g2d.setFont(new Font("Verdana", Font.BOLD, 15));
        g2d.setColor(HIGHLIGHT_COLOR);
        g2d.drawString("Other Controls:", x, y);
        
        y += 30;
        
        String[][] otherControls = {
            {"A/B/C/D", "Answer Questions"},
            {"P / ESC", "Pause Game"},
            {"ENTER", "Submit Final Answer"}
        };
        
        for (String[] control : otherControls) {
            drawKeyBinding(g2d, x, y, control[0], control[1]);
            y += 40;
        }
    }
    
    private void drawKeyBinding(Graphics2D g2d, int x, int y, String key, String action) {
        // Key background (cream)
        g2d.setColor(KEY_BG);
        g2d.fillRoundRect(x, y - 20, 80, 30, 8, 8);
        
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y - 20, 80, 30, 8, 8);
        
        // Key text (Verdana)
        g2d.setFont(new Font("Verdana", Font.BOLD, 13));
        g2d.setColor(TEXT_COLOR);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(key, x + (80 - fm.stringWidth(key)) / 2, y - 2);
        
        // Action text
        g2d.setFont(new Font("Verdana", Font.PLAIN, 13));
        g2d.setColor(TEXT_COLOR);
        g2d.drawString(action, x + 90, y - 2);
    }
    
    private void drawMechanicsSection(Graphics2D g2d, int x, int y, int width) {
        // Section title (Verdana)
        g2d.setFont(new Font("Verdana", Font.BOLD, 20));
        g2d.setColor(TITLE_COLOR);
        g2d.drawString("GAMEPLAY MECHANICS", x, y);
        
        y += 35;
        
        // Mechanics list
        String[][] mechanics = {
            {"Objective", "Collect all letters in each level by solving questions, then reach the EXIT to arrange the collected letters to escape."},
            {"Fog of War", "Limited visibility around your character. Explore carefully!"},
            {"Clue Boxes", "Answer questions correctly to earn letters. Wrong answers cost hearts."},
            {"Time Pressure", "3 minutes per level. After 2 minutes, tiles start vanishing!"},
            {"Vanishing Tiles", "Paths you've walked on disappear over time. Plan your route!"},
            {"Mercy System", "Start with 3 hearts. Protected until all clues collected, then hazards activate."},
            {"Hazard Tiles", "Red danger tiles that damage you after mercy ends. Avoid them!"},
            {"Final Challenge", "Arrange collected letters to spell the correct word. Wrong answer = restart from Level 1!"}
        };
        
        g2d.setFont(new Font("Verdana", Font.PLAIN, 13));
        int lineHeight = 40;
        
        for (String[] mechanic : mechanics) {
            // Title
            g2d.setFont(new Font("Verdana", Font.BOLD, 13));
            g2d.setColor(HIGHLIGHT_COLOR);
            g2d.drawString("• " + mechanic[0], x, y);
            
            y += 18;
            
            // Description
            g2d.setFont(new Font("Verdana", Font.PLAIN, 13));
            g2d.setColor(TEXT_COLOR);
            drawWrappedText(g2d, mechanic[1], x + 15, y, width - 10);
            
            y += lineHeight;
        }
    }
    
    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        String line = "";
        int lineY = y;
        
        for (String word : words) {
            String test = line + word + " ";
            if (fm.stringWidth(test) > maxWidth && !line.isEmpty()) {
                g.drawString(line, x, lineY);
                lineY += fm.getHeight() + 2;
                line = word + " ";
            } else {
                line = test;
            }
        }
        
        if (!line.isEmpty()) {
            g.drawString(line, x, lineY);
        }
    }
    
    private void returnToMenu() {
        if (gameWindow != null) {
            gameWindow.showMainMenu();
        }
    }
}