package Code_System;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.InputStream; 
/**
 * Represents a single story frame with image, text, and animation state
 */
public class StoryFrame {
    private BufferedImage image;
    private String text;
    private String imagePath;
    private int frameNumber;
    
    // Animation state
    private int currentCharIndex = 0;
    private long lastCharTime = 0;
    private boolean typingComplete = false;
    private boolean cursorVisible = true;
    private long lastCursorBlink = 0;
    
    // Timing constants
    private static final int CHAR_DELAY_MS = 50;  // 50ms per character
    private static final int CURSOR_BLINK_MS = 500;  // Blink every 500ms
    
    public StoryFrame(int frameNumber, String imagePath, String text) {
        this.frameNumber = frameNumber;
        this.imagePath = imagePath;
        this.text = text;
        loadImage();
    }
    
    private void loadImage() {
        try {
            // ✅ Load from resources (works in JAR and IDE)
            java.io.InputStream is = getClass().getResourceAsStream(imagePath);
            
            if (is != null) {
                image = ImageIO.read(is);
                System.out.println("✓ Loaded story frame " + frameNumber + ": " + imagePath);
                is.close();
            } else {
                System.err.println("✗ Story frame image not found: " + imagePath);
                System.err.println("  Make sure file is in resources" + imagePath);
                image = createPlaceholderImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading story frame " + frameNumber + ": " + e.getMessage());
            e.printStackTrace();
            image = createPlaceholderImage();
        }
    }
    
    private BufferedImage createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(new java.awt.Color(40, 40, 60));
        g2d.fillRect(0, 0, 800, 600);
        g2d.setColor(java.awt.Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        g2d.drawString("Story Frame " + frameNumber, 300, 300);
        g2d.dispose();
        return placeholder;
    }
    
    /**
     * Updates the typewriter animation
     * @return true if text is still typing, false if complete
     */
    public boolean updateAnimation() {
        if (typingComplete) {
            updateCursor();
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastCharTime >= CHAR_DELAY_MS) {
            if (currentCharIndex < text.length()) {
                currentCharIndex++;
                lastCharTime = currentTime;
            } else {
                typingComplete = true;
            }
        }
        
        updateCursor();
        return !typingComplete;
    }
    
    private void updateCursor() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCursorBlink >= CURSOR_BLINK_MS) {
            cursorVisible = !cursorVisible;
            lastCursorBlink = currentTime;
        }
    }
    
    /**
     * Instantly completes the typing animation
     */
    public void completeTyping() {
        currentCharIndex = text.length();
        typingComplete = true;
        System.out.println("✓ Typing completed - showing full text");
    }
    
    /**
     * Resets animation state for replaying
     */
    public void reset() {
        currentCharIndex = 0;
        typingComplete = false;
        cursorVisible = true;
        lastCharTime = System.currentTimeMillis();
        lastCursorBlink = System.currentTimeMillis();
    }
    
    // Getters
    public BufferedImage getImage() { return image; }
    public String getText() { return text; }
    public String getVisibleText() { return text.substring(0, currentCharIndex); }
    public boolean isTypingComplete() { return typingComplete; }
    public boolean isCursorVisible() { return cursorVisible && !typingComplete; }
    public int getFrameNumber() { return frameNumber; }
    
    
}


