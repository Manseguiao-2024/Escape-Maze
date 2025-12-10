package Code_System;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GameManager {
    // Game States
    public static final String STATE_MAZE_EXPLORATION = "MAZE";
    public static final String STATE_FINAL_CHALLENGE = "FINAL";
    private String currentGameState = STATE_MAZE_EXPLORATION;
    public static final String STATE_VICTORY = "VICTORY";
    public boolean isShowingVictory() {
        return currentGameState.equals(STATE_VICTORY);
    }
    // *** ADD THESE NEW STORY VARIABLES HERE ***
    public static final String STATE_STORY = "STORY";
    private StoryFrame[] storyFrames;
    private int currentStoryFrame = 0;
    private boolean showingStory = false;
    private long storyStartTime = 0;
    private long storyAutoAdvanceTime = 8000;
    private boolean storySkipRequested = false;
    private boolean storyTypingCompleted = false;

    // Story sound paths
  
    // *** END OF NEW STORY VARIABLES ***
    
    // Final Challenge Variables (your existing code continues here)
    private String finalAnswerInput = "";
    public static final int MAX_FINAL_ANSWER_LENGTH = 5;

    private static final String SOUND_BOX_OPEN = "/assets/Sound/OpeningClueBox.wav";
    private static final String SOUND_CORRECT = "/assets/Sound/CorrectAnswer.wav";
    private static final String SOUND_WRONG = "/assets/Sound/Wrong answer_1.wav";
    private static final String SOUND_LETTER = "/assets/Sound/letter_collect.wav";
    private static final String SOUND_GAME_OVER = "/assets/Sound/game_over.wav";
    private static final String SOUND_LEVEL_COMPLETE = "/assets/Sound/level_complete.wav";
    private static final String SOUND_HAZARD = "/assets/Sound/hazard.wav";
    private static final String SOUND_FINAL_WRONG = "/assets/Sound/Wrong answer_2.wav";


    private AudioManager audioManager;
    
    // Existing variables
    private boolean isGameRunning;
    private boolean isPaused;
    private int currentLevel;
    private int timeRemaining;
    private double timeAccumulator;
    private boolean vanishingTriggered;
    private long lastUpdateTime;
    private int playerHearts;
    private boolean mercyActive;
    private ArrayList<Character> collectedLetters;
    private ArrayList<Character> shuffledLetters;
    private ArrayList<String> openedBoxPositions;
    private int totalCluesInLevel;
    private boolean allCluesCollected;
    private String targetWord;
    private Maze maze;
    private Player player;
    private QuestionManager questionManager;
    private PathManager pathManager;
    private LevelHistoryManager levelHistoryManager;
    private boolean waitingForAnswer;
    private boolean isFinalQuestion;
    private QuestionManager.Question currentQuestion;
    private boolean settingsMenuOpen = false;
    
    private static final int MAX_LEVEL = 5;
    private static final int STARTING_HEARTS = 3;
    private static final int LEVEL_TIME_SECONDS = 180;
    private static final int VANISHING_TRIGGER_TIME = 120;
    private static final int MAZE_WIDTH = 29;
    private static final int MAZE_HEIGHT = 17;

    private boolean warningShown = false;           // Track if warning was already shown
    private long warningStartTime = 0;              // When the warning started showing
    private boolean isWarningVisible = false;       // Whether warning is currently visible
    private static final int WARNING_DISPLAY_DURATION_MS = 5000;  // 5 seconds
    
 // Answer feedback popup properties
    private boolean showingAnswerFeedback = false;
    private boolean lastAnswerCorrect = false;
    
    private char earnedLetter = ' ';
    private long feedbackStartTime = 0;
    private static final int FEEDBACK_DISPLAY_DURATION_MS = 2000; // 2 seconds
    
 // Final Challenge feedback popup properties
    private boolean showingFinalFeedback = false;
    private boolean finalAnswerCorrect = false;
    private long finalFeedbackStartTime = 0;
    private static final int FINAL_FEEDBACK_DISPLAY_DURATION_MS = 3000; // 3 seconds
    
 // Game Over popup properties
    private boolean showingGameOver = false;
    private String gameOverReason = "";  // "HEART", "TIME", or "FALL"
    
    private boolean gameOverSoundPlayed = false;
    
    // Mercy deactivation popup properties
    private boolean showingMercyDeactivated = false;
    private long mercyDeactivatedStartTime = 0;
    private static final int MERCY_POPUP_DURATION_MS = 5000; // 5 seconds
    
 // Victory tracking
    private int totalGameTimeSeconds = 0;
    private long gameStartTime = 0;
    
    // Constructor and existing methods remain the same...
    public GameManager() {
        this.maze = new Maze(MAZE_WIDTH, MAZE_HEIGHT);
        this.player = new Player(1, 1);
        this.questionManager = new QuestionManager();
        this.pathManager = new PathManager();
        this.collectedLetters = new ArrayList<>();
        this.shuffledLetters = new ArrayList<>();
        this.openedBoxPositions = new ArrayList<>();
        this.levelHistoryManager = new LevelHistoryManager();
        this.isGameRunning = false;
        this.isPaused = false;
        this.currentLevel = 0;
        this.playerHearts = STARTING_HEARTS;
        this.mercyActive = true;
        this.allCluesCollected = false;
        this.vanishingTriggered = false;
        this.waitingForAnswer = false;
        this.isFinalQuestion = false;
        this.timeRemaining = LEVEL_TIME_SECONDS;
        this.lastUpdateTime = System.currentTimeMillis();
        this.totalCluesInLevel = 0;
        this.targetWord = null;
        this.audioManager = AudioManager.getInstance();
        this.storyFrames = initializeStoryFrames();
        System.out.println("✓ Story system initialized");
    }


    public void startNewGame() {
        System.out.println("\n=================================");
        System.out.println("    STARTING NEW GAME...");
        System.out.println("=================================");
        
        this.currentLevel = 0;
        this.playerHearts = STARTING_HEARTS;
        this.mercyActive = true;
        this.timeRemaining = LEVEL_TIME_SECONDS;
        this.timeAccumulator = 0.0;
        this.vanishingTriggered = false;
        this.lastUpdateTime = System.currentTimeMillis();
        this.allCluesCollected = false;
        this.waitingForAnswer = false;
        this.isFinalQuestion = false;
        this.collectedLetters.clear();
        this.shuffledLetters.clear();
        this.openedBoxPositions.clear();
        this.pathManager.clearHistory();
        this.questionManager.resetQuestions();
        
        // *** NEW: Track game start time ***
        this.gameStartTime = System.currentTimeMillis();
        this.totalGameTimeSeconds = 0;
        
        startStoryFrame(0);  // Show intro story before Level 1
        
        this.isGameRunning = true;
        this.isPaused = false;
        this.lastUpdateTime = System.currentTimeMillis();
        this.warningShown = false;
        this.isWarningVisible = false;
        this.warningStartTime = 0;
        this.gameOverSoundPlayed = false;
        
        System.out.println("✓ New game started!");
        System.out.println("✓ Level 1 loaded");
        System.out.println("✓ Hearts: " + playerHearts);
        System.out.println("✓ Mercy: Active");
        System.out.println("=================================\n");
    }
    public void loadLevel(int levelNumber) {
        // Save previous level state before loading new one
        if (currentLevel > 0) {
            levelHistoryManager.pushLevelState(
                currentLevel, 
                playerHearts, 
                timeRemaining, 
                collectedLetters, 
                false
            );
        }
        
        if (levelNumber < 1 || levelNumber > MAX_LEVEL) {
            System.err.println("Error: Invalid level number " + levelNumber);
            System.err.println("Valid levels are 1-" + MAX_LEVEL);
            return;
        }
        
        System.out.println("\n--- Loading Level " + levelNumber + " ---");
        
        this.currentLevel = levelNumber;
        this.timeRemaining = LEVEL_TIME_SECONDS;
        this.vanishingTriggered = false;
        this.lastUpdateTime = System.currentTimeMillis();
        
        System.out.println("Timer reset to " + LEVEL_TIME_SECONDS + " seconds");
        
        this.collectedLetters.clear();
        this.openedBoxPositions.clear();
        this.allCluesCollected = false;
        this.showingMercyDeactivated = false;
        this.gameOverSoundPlayed = false;
        
        // *** ADD THIS LINE TO RESET MERCY ***
        this.mercyActive = true;  // ⭐ Reset mercy for new level
        System.out.println("✓ Mercy system reactivated for Level " + levelNumber);
        
        System.out.println("✓ Clue collection cleared");
        
        this.targetWord = getTargetWord(levelNumber); 
        
        //Create shuffled letter order
        this.shuffledLetters.clear();
        for (char c : targetWord.toCharArray()) {
            shuffledLetters.add(c);
        }
        Collections.shuffle(shuffledLetters);  // Randomize the order!
        
        this.waitingForAnswer = false;
        this.isFinalQuestion = false;
        System.out.println("Target word for this level: " + targetWord);
        System.out.println("Letters will appear in RANDOM order");
        
        maze.generateMaze(levelNumber);
        
        System.out.println("Maze generated for level " + levelNumber);
        
        this.totalCluesInLevel = targetWord.length(); // Dynamic based on word length
        System.out.println("Total clues in level: " + totalCluesInLevel);
        
        int startX = maze.getStartX();
        int startY = maze.getStartY();
        this.player = new Player(startX, startY);
        
        System.out.println("Player positioned at START (" + startX + ", " + startY + ")");
        
        pathManager.clearHistory();
        pathManager.recordStep(startX, startY);
        
        System.out.println("Path history cleared and initialized");
        System.out.println("--- Level " + levelNumber + " Ready! ---\n");
    }
    
    public void resetToLevel1() {
    	System.out.println("\n=================================");
        System.out.println("   RESETTING TO LEVEL 1...");
        System.out.println("=================================");
        
        // *** NEW: Track reset ***
        levelHistoryManager.incrementResetCount();
        
        this.playerHearts = STARTING_HEARTS;
        System.out.println("Hearts reset to " + STARTING_HEARTS);
        this.mercyActive = true;
        System.out.println("Mercy system restored");
        this.collectedLetters.clear();
        this.shuffledLetters.clear();  // FIX: Clear shuffled letters
        this.openedBoxPositions.clear();  // FIX: Clear opened boxes
        this.allCluesCollected = false;
        this.vanishingTriggered = false;
        this.waitingForAnswer = false;
        this.isFinalQuestion = false;
        this.warningShown = false;
        this.isWarningVisible = false;
        this.warningStartTime = 0;
        System.out.println("All progress cleared");
        this.pathManager.clearHistory();
        System.out.println("Path history cleared");
        this.questionManager.resetQuestions();
        System.out.println("Questions reset");
         loadLevel(1);
        this.isGameRunning = true;
        this.isPaused = false;
        this.gameOverSoundPlayed = false;  
        System.out.println("Reset complete!");
        System.out.println("=================================\n");
    }
    
    private String getTargetWord(int level) {
        switch(level) {
            // insert later on what to collect
             case 1: return "CODER"; 
        case 2: return "STACK";
        case 3: return "QUEUE";
        case 4: return "ARRAY";
        case 5: return "GRAPH";
        default: return "ERROR";
        }
    }
    
	// part 3: Game loop timer
    public void update(double deltaTime) {
    	    // ✅ PRIORITY 0: Story frame display
    	    if (showingStory) {
    	        updateStory();
    	        return;
    	    }
    	    
    	    // ✅ PRIORITY 1: Final Challenge Feedback
    	    if (showingFinalFeedback) {
    	        updateFinalFeedback(); // This line should execute every frame
    	        return;
    	    }
        
        // PRIORITY 2: Game over check
        if (showingGameOver) {
            return;
        }
        
        // PRIORITY 3: Settings Menu (blocks everything)
        if (settingsMenuOpen) {
            return;
        }
        
        // PRIORITY 4: Answer feedback timer
        if (showingAnswerFeedback) {
            long elapsed = System.currentTimeMillis() - feedbackStartTime;
            if (elapsed >= FEEDBACK_DISPLAY_DURATION_MS) {
                showingAnswerFeedback = false;
                earnedLetter = ' ';
                lastUpdateTime = System.currentTimeMillis();
                System.out.println("✅ Feedback closed");
            }
        }
        
        // PRIORITY 5: Mercy deactivation popup timer
        if (showingMercyDeactivated) {
            long elapsed = System.currentTimeMillis() - mercyDeactivatedStartTime;
            
            if (elapsed % 1000 < 20) {
                System.out.println("⏱ Mercy popup showing... " + (elapsed / 1000) + " seconds elapsed");
            }
            
            if (elapsed >= MERCY_POPUP_DURATION_MS) {
                showingMercyDeactivated = false;
                System.out.println("✅ MERCY POPUP CLOSED after " + (elapsed / 1000) + " seconds");
            }
        }
        
        // PRIORITY 6: Check pause/running state
        if (!isGameRunning || currentGameState.equals(STATE_FINAL_CHALLENGE)) {
            return;
        }
        
        if (isPaused && !showingAnswerFeedback) {
            return;
        }
        
        // PRIORITY 7: Update game logic
        updateTimer(deltaTime);
        checkTimerThresholds();
        
        if (vanishingTriggered) {
            updateVanishingTiles();
            
            Tile playerTile = maze.getTile(player.getX(), player.getY());
            if (playerTile != null && playerTile.isVanished()) {
                handlePlayerFall();
                return;
            }
        }
        
        if (playerHearts <= 0) {
            gameOver();
        }
    }

    // STEP 2: UPDATE GameManager.java - updateFinalFeedback() method (around line 1009)
    // Replace the existing updateFinalFeedback() with this FIXED version:

    /**
     * Updates the final feedback timer and executes consequences when done
     * Call this from update() method
     */
    
    private void updateTimer(double deltaTime) {
        // Since GameLoop now passes exactly 1.0 second each time,
        // we can directly decrement without accumulator
        timeRemaining -= (int) deltaTime;
        
        // Ensure time doesn't go negative
        if (timeRemaining < 0) {
            timeRemaining = 0;
        }
    }
    private void checkTimerThresholds() {
        // Check if reached 2:10 (130 seconds) - show warning popup
        if (timeRemaining <= 130 && !warningShown) {
            warningShown = true;
            isWarningVisible = true;
            warningStartTime = System.currentTimeMillis();
            System.out.println("\n⚠ WARNING POPUP TRIGGERED AT 2:10!");
            System.out.println("⚠ TILES VANISHING IN 2 MINUTES!");
        }
        
        // Check if warning should be hidden (after 5 seconds)
        if (isWarningVisible && (System.currentTimeMillis() - warningStartTime >= WARNING_DISPLAY_DURATION_MS)) {
            isWarningVisible = false;
            System.out.println("⚠ Warning popup hidden after 5 seconds");
        }
        
        // Check if reached 2 minutes (120 seconds) - trigger vanishing
        if (timeRemaining <= VANISHING_TRIGGER_TIME && !vanishingTriggered) {
            vanishingTriggered = true;
            System.out.println("\n⏰ WARNING: 2 MINUTES REMAINING!");
            System.out.println("⏰ TILES ARE STARTING TO VANISH!");
        }
        
        // Check if time has run out - game over
        if (timeRemaining <= 0 && isGameRunning) {
            System.out.println("\n⏱ TIME'S UP!");
            
            // *** FIX: Set ALL flags in correct order ***
            gameOverReason = "TIME";
            
            // Clear any active popups/states FIRST
            showingAnswerFeedback = false;
            waitingForAnswer = false;
            currentQuestion = null;
            isFinalQuestion = false;
            showingFinalFeedback = false;
            showingMercyDeactivated = false;
            
            // THEN stop game
            isGameRunning = false;
            isPaused = false;
            
            // FINALLY set game over popup
            showingGameOver = true;
            
            System.out.println("🚨 Game Over popup triggered - reason: TIME");
        }
    }
    
    private void updateVanishingTiles() {
        // Get tiles that should vanish (older than threshold)
        long vanishThreshold = 30000; // 30 seconds in milliseconds
        ArrayList<PathManager.Position> dangerTiles = pathManager.getTilesInDangerZone(vanishThreshold);
        
        if (!dangerTiles.isEmpty()) {
            System.out.println("⚠️ Vanishing " + dangerTiles.size() + " tiles...");
        }
        
        // For each danger tile, mark it as vanished in the maze
        for (PathManager.Position pos : dangerTiles) {
            Tile tile = maze.getTile(pos.getX(), pos.getY());
            if (tile != null && !tile.isVanished()) {
                tile.setVanished(true);
                tile.setWalkable(false);
                System.out.println("🕳️ Tile vanished at (" + pos.getX() + ", " + pos.getY() + ")");
            }
        }
    }

	// part 4: player movement & interaction
    public boolean movePlayer(int newX, int newY) {
        // ADD THIS CHECK AT THE START
        if (currentGameState.equals(STATE_FINAL_CHALLENGE)) {
            return false; // No movement during final challenge
        }
        
        // Check if game is paused or waiting for answer
        if (isPaused || waitingForAnswer) {
            System.out.println("Cannot move: Game paused or waiting for answer");
            return false;
        }
        
        // Check if position is valid and walkable
        if (!maze.isValidPosition(newX, newY)) {
            System.out.println("Move blocked: Out of bounds");
            return false;
        }
        
        if (!maze.isWalkable(newX, newY)) {
            System.out.println("Move blocked: Tile not walkable");
            return false;
        }
        
        // Update player position
        player.setPosition(newX, newY);
        
        // *** NEW: Check if player stepped on vanished tile IMMEDIATELY ***
        Tile currentTile = maze.getTile(newX, newY);
        if (currentTile != null && currentTile.isVanished()) {
            System.out.println("🚨 CRITICAL: Player stepped on VANISHED tile at (" + newX + ", " + newY + ")");
            handlePlayerFall();  // Instant game over
            return false;  // Don't continue processing
        }
        
        // Record step in PathManager
        pathManager.recordStep(newX, newY);
        
        // Check what type of tile the player stepped on
        checkTileType(newX, newY);
        
        return true;
    }
    
    private void checkTileType(int x, int y) {
        // Get the tile from maze
        Tile tile = maze.getTile(x, y);
        
        if (tile == null) {
            System.err.println("Error: Tile at (" + x + ", " + y + ") is null");
            return;
        }
        
        String tileType = tile.getType();
        
        tile.onStep(player);

        // Handle different tile types
        switch (tileType) {
            case "PATH":
                // Normal path tile - do nothing special
                break;
                
            case "WALL":
                // Should never happen (isWalkable should block this)
                System.out.println("Warning: Player somehow walked on WALL tile");
                break;
                
            case "CLUE":
                // Player stepped on a clue box
                handleClueBox(x, y);
                break;
                
            case "HAZARD":
                // Player stepped on a hazard
                handleHazard();
                break;
                
            case "EXIT":
                // Player reached the exit
                handleExit();
                break;
                
            case "START":
                // Player is on start tile - do nothing
                break;
                
            default:
                System.out.println("Unknown tile type: " + tileType);
                break;
        }
    }
    /**
     * Processes the player's selected answer for the current question.
     * This is called from GamePanel's KeyListener.
     * @param selectedAnswer The character key pressed ('A', 'B', 'C', or 'D').
     */
    public void submitAnswer1(char selectedAnswer) {
        if (!waitingForAnswer || currentQuestion == null) {
            return; 
        }

        // Call the implemented checkAnswer method in the Question object
        boolean isCorrect = currentQuestion.checkAnswer(selectedAnswer);
        
        // Apply consequences
        if (isCorrect) {
            System.out.println("Correct Answer! Clue collected.");
            
            // ADD YOUR CLUE COLLECTION LOGIC HERE:
            // 1. Mark the clue box as opened/collected.
            // 2. Add the corresponding letter to the collectedLetters list.
            
        } else {
            System.out.println("❌ Wrong Answer! Lost a heart.");
            playerHearts--; 
            
            if (playerHearts <= 0) {
                gameOver();
            }
        }
        
        // Clear the question state to unpause the game and remove the visual pop-up
        this.waitingForAnswer = false;
        this.currentQuestion = null; 
        this.isFinalQuestion = false;
        this.isPaused = false; 
    }
    
    private void handleClueBox(int x, int y) {
        // FIX: Create unique identifier for this box position
        String boxPosition = x + "," + y;
        
        // FIX: Check if this box has already been answered correctly
        if (openedBoxPositions.contains(boxPosition)) {
            System.out.println("\n This clue box has already been completed!");
            System.out.println("Find other clue boxes.\n");
            return;  // Don't open the box again
        }
        
        // *** ADD BOX OPEN SOUND ***
        audioManager.playSoundEffect(SOUND_BOX_OPEN);
        
        System.out.println("\n=================================");
        System.out.println("    CLUE BOX DISCOVERED!");
        System.out.println("=================================");
        
        // Pause the game timer
        isPaused = true;
        
        // Get a sequential question for current level
        QuestionManager.Question question = questionManager.getNextQuestion(currentLevel);
        
        if (question == null) {
            System.err.println("Error: No questions available for level " + currentLevel);
            isPaused = false;
            return;
        }
        
        // Display the question
        System.out.println("\nQuestion (Difficulty " + question.getDifficulty() + "):");
        System.out.println(question.getQuestionText());
        System.out.println();
        
        // Display choices
        String[] choices = question.getChoices();
        for (int i = 0; i < choices.length; i++) {
            System.out.println((char)('A' + i) + ". " + choices[i]);
        }
        System.out.println();
        
        // Set waiting state
        waitingForAnswer = true;
        
        // Store current question for answer checking
        currentQuestion = question;
        
        // Store the box position temporarily
        player.setCurrentBoxPosition(boxPosition);
        
        System.out.println("Enter your answer (A, B, C, or D): ");
        System.out.println("=================================\n");
    }
    
    public void submitAnswer(String playerAnswer) {
        // Check if we're actually waiting for an answer
        if (!waitingForAnswer || currentQuestion == null) {
            System.out.println("Error: Not waiting for an answer");
            return;
        }
        
        // Convert answer letter to index (A=0, B=1, C=2, D=3)
        playerAnswer = playerAnswer.trim().toUpperCase();
        
        if (playerAnswer.length() != 1) {
            System.out.println("Invalid answer format. Please enter A, B, C, or D");
            return;
        }
        
        char answerChar = playerAnswer.charAt(0);
        int answerIndex = answerChar - 'A';  // Convert 'A' to 0, 'B' to 1, etc.
        
        // Validate answer index
        if (answerIndex < 0 || answerIndex >= currentQuestion.getChoices().length) {
            System.out.println("Invalid answer. Please enter A, B, C, or D");
            return;
        }
        
        // Check if answer is correct
        boolean isCorrect = questionManager.checkAnswer(currentQuestion, answerIndex);
        
        System.out.println("\n=================================");
        if (isCorrect) {
            System.out.println("     CORRECT!");
            System.out.println("=================================");
            
            // FIX: Get the letter reward from SHUFFLED list (not sequential)
            char letterReward = getLetterReward();
            
            // Add letter to collection
            collectLetter(letterReward);
            
            System.out.println("You earned the letter: '" + letterReward + "'");
            System.out.println("Collected letters: " + collectedLetters);
            
            // FIX: Mark the box as completed ONLY on correct answer
            String boxPosition = player.getCurrentBoxPosition();
            if (boxPosition != null && !openedBoxPositions.contains(boxPosition)) {
                openedBoxPositions.add(boxPosition);
            }
            player.setCurrentBoxPosition(null);  // Clear temporary box position
            
        } else {
            System.out.println("     WRONG!");
            System.out.println("=================================");
            System.out.println("Correct answer was: " + (char)('A' + currentQuestion.getCorrectAnswerIndex()));
            System.out.println("\n You can retry this clue box by stepping on it again!");
            
            // Player takes damage (only if mercy is inactive)
            takeDamage(1);
            
            // FIX: DON'T mark the box as opened - allow retry
            player.setCurrentBoxPosition(null);  // Clear temporary box position
        }
        System.out.println("=================================\n");
        
        // Clear waiting state
        waitingForAnswer = false;
        currentQuestion = null;
        
        // Resume game
        isPaused = false;
        lastUpdateTime = System.currentTimeMillis(); // Reset timer
    }

    private void handleHazard() {
        System.out.println("\n⚠ HAZARD! You stepped on a dangerous tile!");
        
        // Play hazard sound ALWAYS when stepping on hazard
        audioManager.playSoundEffect(SOUND_HAZARD);
        System.out.println("🔊 Played hazard sound");
        
        // Check if fatal before applying damage
        boolean willDie = (playerHearts - 1 <= 0) && !mercyActive;
        
        if (willDie) {
            gameOverReason = "HEART";
        }
        
        // Apply damage (this calls gameOver() if fatal)
        takeDamage(1);
    }

    
    private void handleExit() {
        System.out.println("\n=================================");
        System.out.println("    EXIT REACHED!");
        System.out.println("=================================");
        
        if (allCluesCollected) {
            System.out.println("All clues collected! Proceeding to final challenge...");
            startFinalChallenge(); // ✅ CORRECT - Visual input system
            // NOT handleFinalChallenge() ❌ - Console input system
        } else {
            int remaining = totalCluesInLevel - collectedLetters.size();
            System.out.println(" You need to collect all clues first!");
            System.out.println("Clues collected: " + collectedLetters.size() + "/" + totalCluesInLevel);
            System.out.println("Clues remaining: " + remaining);
            System.out.println("Keep exploring the maze!");
            System.out.println("=================================\n");
        }
    }
    
	// part 5: clue collection system
    private char getLetterReward() {
        // Get the next letter from SHUFFLED list (not target word directly)
        int index = collectedLetters.size();
        if (index < shuffledLetters.size()) {
            return shuffledLetters.get(index);
        }
        // Fallback (shouldn't happen)
        return '?';
    }
    
    private void collectLetter(char letter) {
        collectedLetters.add(letter);
        player.collectLetter(letter);
        
        // Check if all clues collected
        if (collectedLetters.size() >= totalCluesInLevel) {
            allCluesCollected = true;
            
            // Disable mercy system once all clues collected
            if (mercyActive) {
                mercyActive = false;
                System.out.println("\n MERCY SYSTEM DEACTIVATED!");
                System.out.println(" Wrong answers will now damage your hearts!");
                System.out.println(" Reach the EXIT to complete the level!\n");
            }
        }
    }
    
    private void collectLetter(String letter) {
        if (letter == null || letter.isEmpty()) {
            System.err.println("Error: Invalid letter");
            return;
        }
        
        char letterChar = letter.charAt(0);
        collectedLetters.add(letterChar);
        player.collectLetter(letterChar);
        
        System.out.println("✓ Letter collected: '" + letterChar + "'");
        System.out.println("Current letters: " + collectedLetters);

    }
    


    private void triggerFinalQuestion() {
        System.out.println("\n=================================");
        System.out.println("     FINAL CHALLENGE! ");
        System.out.println("=================================");
        
        // Pause the game
        isPaused = true;
        
        // Set final question flag
        isFinalQuestion = true;
        waitingForAnswer = true;
        
        // Get the final question from QuestionManager
        QuestionManager.Question finalQ = questionManager.getFinalQuestion();
        currentQuestion = finalQ;
        
        System.out.println("\nFinal Question:");
        System.out.println(finalQ.getQuestionText());
        System.out.println();
        
        // Display choices
        String[] choices = finalQ.getChoices();
        for (int i = 0; i < choices.length; i++) {
            System.out.println((char)('A' + i) + ". " + choices[i]);
        }
        
        System.out.println();
        System.out.println(" WARNING: Wrong answer = Restart from Level 1!");
        System.out.println("Enter your answer (A, B, C, or D): ");
        System.out.println("=================================\n");
    }
    
    public void submitAnswer(char selectedAnswer) {
        if (!waitingForAnswer || currentQuestion == null) {
            return; 
        }

        boolean isCorrect = currentQuestion.checkAnswer(selectedAnswer);
        
        if (isCorrect) {
        	audioManager.playSoundEffect(SOUND_CORRECT);
            
            System.out.println("✓ Correct Answer! Clue collected.");
            
            if (collectedLetters.size() < shuffledLetters.size()) {
                char nextLetter = shuffledLetters.get(collectedLetters.size());
                collectedLetters.add(nextLetter);
                player.collectLetter(nextLetter);
                
                audioManager.playSoundEffect(SOUND_LETTER);
                
                System.out.println("✓ Collected letter: " + nextLetter);
                System.out.println("✓ Total collected: " + collectedLetters.size() + "/" + totalCluesInLevel);

                String boxPosition = player.getCurrentBoxPosition();
                if (boxPosition != null && !openedBoxPositions.contains(boxPosition)) {
                    openedBoxPositions.add(boxPosition);
                    System.out.println("✓ Box marked as opened: " + boxPosition);
                }
                player.setCurrentBoxPosition(null);
                
                earnedLetter = nextLetter;
                
                if (collectedLetters.size() >= totalCluesInLevel) {
                    allCluesCollected = true;
                    
                    if (mercyActive) {
                        mercyActive = false;
                        System.out.println("\n⚠ MERCY SYSTEM DEACTIVATED!");
                        System.out.println("⚠ Wrong answers will now damage your hearts!");
                        
                        // *** NEW: Trigger mercy deactivation popup ***
                        showingMercyDeactivated = true;
                        mercyDeactivatedStartTime = System.currentTimeMillis();
                        System.out.println("✓ Showing mercy deactivation popup for 5 seconds");
                    }
                    
                    System.out.println("\n✓ All clues for this level collected! ");
                    System.out.println("✓ Proceed to the EXIT (E) to complete the level!");
                }
            }
            
            lastAnswerCorrect = true;
            
        } else {
        	  audioManager.playSoundEffect(SOUND_WRONG);
            
            System.out.println("✗ Wrong Answer!");
            playerHearts--; 
            
            if (playerHearts <= 0) {
                System.out.println("💀 OUT OF HEARTS - GAME OVER!");
                
                this.waitingForAnswer = false;
                this.currentQuestion = null; 
                this.isFinalQuestion = false;
                this.isPaused = false;
                player.setCurrentBoxPosition(null);
                
                gameOverReason = "HEART";
                gameOver();
                return;
            }
            
            System.out.println("❤ Lost a heart. Hearts remaining: " + playerHearts);
            
            player.setCurrentBoxPosition(null);
            
            lastAnswerCorrect = false;
        }
        
        // *** CRITICAL FIX: Clear ALL states immediately ***
        this.waitingForAnswer = false;
        this.currentQuestion = null; 
        this.isFinalQuestion = false;
        this.isPaused = false;  // *** DON'T pause - feedback handles it ***
        
        // Show feedback popup
        showingAnswerFeedback = true;
        feedbackStartTime = System.currentTimeMillis();
        
        System.out.println("✓ Answer processed - showing feedback (isPaused = " + isPaused + ")");
    }
   
    /**
     * Closes the game over popup and returns to main menu
     */
    public void closeGameOver() {
        showingGameOver = false;
        gameOverReason = "";
        System.out.println("Game Over popup closed - returning to main menu");
    }
    
    /**
     * Check if settings menu is open
     */
    public boolean isSettingsMenuOpen() {
        return settingsMenuOpen;
    }

    /**
     * Set settings menu state
     */
    public void setSettingsMenuOpen(boolean open) {
        this.settingsMenuOpen = open;
        System.out.println("⚙️ Settings menu " + (open ? "opened" : "closed"));
    }
    
    /**
     * Get total game time in seconds
     */
    public int getTotalGameTime() {
        if (gameStartTime > 0) {
            long elapsed = System.currentTimeMillis() - gameStartTime;
            return (int)(elapsed / 1000);
        }
        return totalGameTimeSeconds;
    }
    /**
     * Processes the player's selected answer for the current question.
     * @param selectedAnswer The character key pressed ('A', 'B', 'C', or 'D').
     */
    
 // FIX 1: In the submitAnswer() method, replace the section that shows feedback:

 // REPLACE the submitAnswer(char selectedAnswer) method in GameManager.java (around line 460)
 // with this FIXED version:

    
    
	// part 6: health and mercy system
    public void takeDamage() {
        // Check if mercy is active
        if (mercyActive) {
            System.out.println("\n MERCY ACTIVE: No damage taken!");
            System.out.println(" You are protected until all clues are collected.");
            System.out.println(" Clues: " + collectedLetters.size() + "/" + totalCluesInLevel);
            return;
        }
        
        // Apply 1 heart damage
        playerHearts -= 1;
        player.takeDamage(1);
        
        System.out.println("\n You took 1 damage!");
        System.out.println(" Hearts: " + playerHearts + "/" + STARTING_HEARTS);
        
        // Check if player died
        if (playerHearts <= 0) {
            System.out.println("OUT OF HEARTS!");
            gameOver();
        }
    }
    
    private void takeDamage(int damage) {
        // Check mercy system
        if (mercyActive) {
            System.out.println("MERCY ACTIVE: No damage taken!");
            System.out.println("(Collect all clues to deactivate mercy)");
            return;
        }
        
        // Apply damage
        playerHearts -= damage;
        player.takeDamage(damage);
        
        System.out.println("❤ Hearts: " + playerHearts + "/" + STARTING_HEARTS);
        
        // *** UPDATED: Set reason and check if player died ***
        if (playerHearts <= 0) {
            System.out.println("\n OUT OF HEARTS!");
            gameOverReason = "HEART";  // Set reason before game over
            gameOver();
        }
    }
    
    private void removeMercy() {
        mercyActive = false;
        
        System.out.println("\n  ╔═══════════════════════════════╗");
        System.out.println("   MERCY SYSTEM DEACTIVATED!");
        System.out.println("   ╚═══════════════════════════════╝");
        System.out.println("   Wrong answers will now cost hearts!");
        System.out.println("  Hazards will damage you!");
        System.out.println("   Be careful!\n");
    }
    
    public boolean isPlayerAlive() {
        return playerHearts > 0;
    }
    
    private void restoreMercy() {
        mercyActive = true;
        System.out.println("  Mercy system restored!");
        System.out.println("  You are protected until all clues are collected.");
    }

    private void resetHearts() {
        playerHearts = STARTING_HEARTS;
        System.out.println("   Hearts reset to " + STARTING_HEARTS);
    }
    
    public boolean checkMercyStatus() {
        return mercyActive;
    }
    
    public int getCurrentHearts() {
        return playerHearts;
    }

    public int getMaxHearts() {
        return STARTING_HEARTS;
    }

    
    public void displayHealthStatus() {
        System.out.println("\n┌──────────────────────┐");
        System.out.println("  HEALTH STATUS");
        System.out.println("└──────────────────────┘");
        
        // Display hearts visually
        System.out.print("  Hearts: ");
        for (int i = 0; i < playerHearts; i++) {
            System.out.print(" ");
        }
        for (int i = playerHearts; i < STARTING_HEARTS; i++) {
            System.out.print(" ");
        }
        System.out.println(" (" + playerHearts + "/" + STARTING_HEARTS + ")");
        
        // Display mercy status
        if (mercyActive) {
            System.out.println("  Mercy:  ACTIVE (Protected)");
        } else {
            System.out.println("  Mercy:  INACTIVE (Vulnerable)");
        }
        
        System.out.println("└──────────────────────┘\n");
    }
    
	 //part 7: level progression
    public void nextLevel() {
    	audioManager.playSoundEffect(SOUND_LEVEL_COMPLETE);
        
        System.out.println("Advancing to next level...");
        System.out.println("Level " + currentLevel + " COMPLETE!");
        
        // ✅ FIX: Reset hearts before advancing
        this.playerHearts = STARTING_HEARTS;
        System.out.println("❤️ Hearts reset to " + STARTING_HEARTS);
        
        // ✅ FIX: Restore mercy for next level
        this.mercyActive = true;
        System.out.println("🛡️ Mercy system reactivated");
        
        // Update streak
        levelHistoryManager.updateStreak(currentLevel);
        
        // Save completion state
        levelHistoryManager.pushLevelState(
            currentLevel, 
            playerHearts, 
            timeRemaining, 
            collectedLetters, 
            true
        );
        
        // ✅ CRITICAL FIX: Check if game is complete BEFORE showing story
        if (currentLevel >= MAX_LEVEL) {
            System.out.println("🎉 ALL 5 LEVELS COMPLETED!");
            System.out.println("📖 Showing victory story frame (Frame 6)...");
            startStoryFrame(5);  // Show victory story (Frame 6)
            return;  // Don't proceed to next level
        }
        
        // Show post-level story (which will load next level after)
        triggerPostLevelStory(currentLevel);
    }


    
    private void winGame() {
        System.out.println("=================================");
        System.out.println("   GAME COMPLETED!    ");
        System.out.println("=================================");
        System.out.println("Levels completed: " + currentLevel);
        System.out.println("Hearts remaining: " + playerHearts);
        
        // Calculate total time
        totalGameTimeSeconds = getTotalGameTime();
        
        // Stop game
        this.isGameRunning = false;
        this.isPaused = false;
        
        // Victory screen will be shown after story frame 5
        System.out.println("✓ Ready for victory screen");
    }
        
	// part 8: Game over condition
  
    
    private void instantTileCollapse() {
        System.out.println("\n================================");
        System.out.println("  WRONG FINAL ANSWER!");
        System.out.println("  ALL TILES COLLAPSING...");
        System.out.println("================================");
        
        // Simulate collapse animation with 2 second delay
        try {
            Thread.sleep(2000);  // Wait 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\nRestarting from Level 1...\n");
        // Reset to Level 1
        resetToLevel1();
    }

	// part 9: Pause/ resume
    public void pauseGame() {
        // Check if game is running
        if (!isGameRunning) {
            System.out.println("Cannot pause: Game is not running");
            return;
        }
        
        // Check if already paused
        if (isPaused) {
            System.out.println("Game is already paused");
            return;
        }
        
        // Check if waiting for answer (cannot pause during questions)
        if (waitingForAnswer) {
            System.out.println("Cannot pause: Waiting for answer");
            return;
        }
        
        // Pause the game
        isPaused = true;
        System.out.println("\n================================");
        System.out.println("       GAME PAUSED");
        System.out.println("================================");
        System.out.println("Press resume to continue...\n");
    }
    
    public void resumeGame() {
        // Check if game is running
        if (!isGameRunning) {
            System.out.println("Cannot resume: Game is not running");
            return;
        }
        
        // Check if not paused
        if (!isPaused) {
            System.out.println("Game is not paused");
            return;
        }
        
        // Resume the game
        isPaused = false;
        
        // Reset the last update time to prevent time skip
        lastUpdateTime = System.currentTimeMillis();
        
        System.out.println("\n================================");
        System.out.println("      GAME RESUMED");
        System.out.println("================================");
        System.out.println("Continuing...\n");
    }

    public void togglePause() {
        if (isPaused) {
            // Game is paused, so resume it
            resumeGame();
        } else {
            // Game is not paused, so pause it
            pauseGame();
        }
    }
    
	// part 10: vanishing tile coordination
    private void triggerVanishingTiles() {
        // Set the vanishing flag
        vanishingTriggered = true;
        
        System.out.println("\n================================");
        System.out.println("  WARNING: TILES VANISHING!");
        System.out.println("================================");
        System.out.println("Tiles you walked on will start to disappear!");
        System.out.println("Move quickly to avoid falling!\n");
        
        // TODO: Phase 4 - Connect to TileCollapseManager
        // Example: tileCollapseManager.startVanishing();
    }
    
    /**
     * Task 10.2: Checks if a tile at given position has vanished
     * Placeholder for Phase 4 integration with TileCollapseManager
     * @param x X coordinate of tile
     * @param y Y coordinate of tile
     * @return true if tile has vanished, false otherwise
     */
    public boolean isTileVanished(int x, int y) {
        // TODO: Phase 4 - Check with TileCollapseManager
        // Example: return tileCollapseManager.isTileVanished(x, y);
        
        // For now, check the tile's vanished status directly
        Tile tile = maze.getTile(x, y);
        if (tile != null) {
            return tile.isVanished();
        }
        
        // Default: return false (tile hasn't vanished)
        return false;
    }

    private void handlePlayerFall() {
        System.out.println("\n================================");
        System.out.println("🕳️ PLAYER FELL THROUGH TILE!");
        System.out.println("================================");
        System.out.println("Position: (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("You stepped on a vanished tile and fell through!");
        System.out.println("================================\n");
        
        // *** FIX: Set ALL flags immediately and in correct order ***
        gameOverReason = "FALL";
        
        // Clear ANY active popups/states FIRST
        showingAnswerFeedback = false;
        waitingForAnswer = false;
        currentQuestion = null;
        isFinalQuestion = false;
        showingFinalFeedback = false;
        
        // THEN set game over state
        isGameRunning = false;
        isPaused = false;
        showingGameOver = true;
        
        // Play game over sound
        if (audioManager != null) {
            audioManager.playSoundEffect(SOUND_GAME_OVER);
        }
        
        // Save failed attempt
        if (levelHistoryManager != null) {
            levelHistoryManager.pushLevelState(
                currentLevel, 
                playerHearts, 
                timeRemaining, 
                collectedLetters, 
                false
            );
        }
        
        System.out.println("✓ showingGameOver = " + showingGameOver);
        System.out.println("✓ gameOverReason = " + gameOverReason);
        System.out.println("✓ isGameRunning = " + isGameRunning);
        System.out.println("✓ isPaused = " + isPaused);
        System.out.println("================================\n");
    }

    
    
    // FIX 4: Update gameOver() to match the same pattern:

    public void gameOver() {
        System.out.println("\n================================");
        System.out.println("         GAME OVER");
        System.out.println("================================");
        System.out.println("Reason: " + gameOverReason);
        
        // Ensure reason is set
        if (gameOverReason.isEmpty()) {
            if (playerHearts <= 0) {
                gameOverReason = "HEART";
            } else if (timeRemaining <= 0) {
                gameOverReason = "TIME";
            } else {
                gameOverReason = "FALL";
            }
        }
        
        // ✅ STOP BACKGROUND MUSIC FIRST
        System.out.println("🔇 STOPPING BACKGROUND MUSIC...");
        if (audioManager != null) {
            audioManager.stopBackgroundMusic();
            System.out.println("   Background music stopped");
        }
        
        // ✅ PLAY GAME OVER SOUND ONCE
        if (!gameOverSoundPlayed && audioManager != null) {
            audioManager.playSoundEffect(SOUND_GAME_OVER);
            gameOverSoundPlayed = true;
            System.out.println("🔊 Game Over sound played");
        }
        
        // Clear ALL active states
        showingAnswerFeedback = false;
        showingFinalFeedback = false;
        waitingForAnswer = false;
        currentQuestion = null;
        isFinalQuestion = false;
        showingMercyDeactivated = false;
        
        // Set game over state
        isGameRunning = false;
        isPaused = false;
        showingGameOver = true;

        // Save failed attempt
        levelHistoryManager.pushLevelState(
            currentLevel, 
            playerHearts, 
            timeRemaining, 
            collectedLetters, 
            false
        );
        
        System.out.println("✅ showingGameOver = " + showingGameOver);
        System.out.println("✅ gameOverReason = " + gameOverReason);
        System.out.println("================================\n");
    }
	// part 11: getters
    
   
    //Check if final challenge feedback popup should be displayed
   
    public boolean isShowingFinalFeedback() {
        return showingFinalFeedback;
    }

    /**
     * Was the final challenge answer correct?
     */
    

    /**
     * Updates the final feedback timer and executes consequences when done
     * Call this from update() method
     */
    public void updateFinalFeedback() {
        if (!showingFinalFeedback) {
            return;
        }
        
        long elapsed = System.currentTimeMillis() - finalFeedbackStartTime;
        
        // Check if display duration has passed (3 seconds)
        if (elapsed >= FINAL_FEEDBACK_DISPLAY_DURATION_MS) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║  FINAL FEEDBACK COMPLETE           ║");
            System.out.println("╚════════════════════════════════════╝");
            
            showingFinalFeedback = false;
            
            // Execute the consequence after popup closes
            if (finalAnswerCorrect) {
                System.out.println("✅ Answer was CORRECT");
                
                if (currentLevel >= MAX_LEVEL) {
                    // ✅ ALL 5 LEVELS COMPLETE - SHOW FRAME 6
                    System.out.println("╔════════════════════════════════════╗");
                    System.out.println("║   🎉 ALL 5 LEVELS COMPLETE! 🎉   ║");
                    System.out.println("╚════════════════════════════════════╝");
                    System.out.println("📖 Starting victory story (Frame 6)...");
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    
                    startStoryFrame(5);  // Show Frame 6
                    
                } else {
                    // More levels to play
                    System.out.println("➡️ Advancing to Level " + (currentLevel + 1));
                    nextLevel();
                }
                
            } else {
                // Wrong answer
                System.out.println("❌ Answer was WRONG - Resetting to Level 1");
                instantTileCollapse();
            }
        }
    }



   /**
    * Was the final challenge answer correct?
    */
   public boolean wasFinalAnswerCorrect() {
       return finalAnswerCorrect;
   }
    //game state
    public boolean isGameRunning() { return isGameRunning; }
    public boolean isPaused() { return isPaused; }
    public int getCurrentLevel() { return currentLevel; }
    
    // timer
    public int getTimeRemaining() { return timeRemaining; }
    public boolean isVanishingActive() {
        return vanishingTriggered;
    }
    
    // player info
    public int getPlayerHearts() { return playerHearts; }
    public boolean isMercyActive() { return mercyActive; }
    
    // clue progress
    public int getCollectedClueCount() { return collectedLetters.size(); }
    public int getTotalClueCount() { return totalCluesInLevel; }
    public ArrayList<Character> getCollectedLetters() { 
        return new ArrayList<>(collectedLetters); }
    public boolean areAllCluesCollected() {return allCluesCollected;}
    public String getTargetWord() { return targetWord; }
    
    // question state
    public QuestionManager.Question getCurrentQuestion() {
        return currentQuestion;
    }
    
    public boolean isWaitingForAnswer() { return waitingForAnswer; }
    public boolean isFinalQuestion() { return isFinalQuestion; }
    
    public boolean isWarningVisible() {
        return isWarningVisible;
    }

    /**
    * Check if mercy deactivation popup should be displayed
    */
   public boolean isShowingMercyDeactivated() {
       return showingMercyDeactivated;
   }

    /**
     * Get the warning message for display
     */
    public String getWarningMessage() {
        return "WARNING: TILES VANISHING IN 2 MINUTES!";
    }

    /**
     * Get the warning submessage
     */
    public String getWarningSubMessage() {
        return "collect all the clue as soon as possible!";
    }
    
    // Object References
    public Maze getMaze() {
        return maze;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public PathManager getPathManager() {
        return pathManager;
    }
    
    public QuestionManager getQuestionManager() {
        return questionManager;
    }
    
    public int getPlayerX() {
        return player.getX();
    }
    
    public int getPlayerY() {
        return player.getY();
    }
    
    // Constant
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
    
    public int getStartingHearts() {
        return STARTING_HEARTS;
    }

    public LevelHistoryManager getLevelHistoryManager() {
    return levelHistoryManager;
}

    // FIX: Add getter for opened boxes (useful for debugging/UI)
    public ArrayList<String> getOpenedBoxPositions() {
        return new ArrayList<>(openedBoxPositions);
    }
    
	// part 12: helper/ utility methods
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    public String getFormattedTime() {
        return formatTime(timeRemaining);
    }
    
    public int calculateScore() {
        int score = 0;
        
        // Points for levels completed
        score += (currentLevel - 1) * 1000;  // currentLevel-1 because we count completed levels
        
        // Points for hearts remaining
        score += playerHearts * 500;
        
        // Points for time remaining
        score += timeRemaining;
        
        // Points for clues collected
        score += collectedLetters.size() * 200;
        
        // Bonus: If all clues collected, add 1000 bonus
        if (allCluesCollected) {
            score += 1000;
        }
        
        return score;
    }
    
 // Game Over popup getters
    public boolean isShowingGameOver() {
        return showingGameOver;
    }

    public String getGameOverReason() {
        return gameOverReason;
    }
    
 // Answer feedback getters
    public boolean isShowingAnswerFeedback() {
        if (!showingAnswerFeedback) {
            return false;
        }
        
        // Auto-hide after duration
        if (System.currentTimeMillis() - feedbackStartTime >= FEEDBACK_DISPLAY_DURATION_MS) {
            showingAnswerFeedback = false;
            return false;
        }
        
        return true;
    }

    /**
     * Was the last answer correct?
     */
    public boolean wasLastAnswerCorrect() {
        return lastAnswerCorrect;
    }

    /**
     * Get the letter earned from last correct answer
     */
    public char getEarnedLetter() {
        return earnedLetter;
    }
    
    public void printGameState() {
        System.out.println("\n========================================");
        System.out.println("         GAME STATE DEBUG");
        System.out.println("========================================");
        
        // Game Status
        System.out.println("--- Game Status ---");
        System.out.println("Running: " + isGameRunning);
        System.out.println("Paused: " + isPaused);
        System.out.println("Waiting for Answer: " + waitingForAnswer);
        System.out.println("Final Question: " + isFinalQuestion);
        
        // Level Info
        System.out.println("\n--- Level Info ---");
        System.out.println("Current Level: " + currentLevel + "/" + MAX_LEVEL);
        System.out.println("Target Word: " + targetWord);
        System.out.println("Shuffled Letters: " + shuffledLetters);  // FIX: Show shuffled order
        
        // Timer Info
        System.out.println("\n--- Timer ---");
        System.out.println("Time Remaining: " + timeRemaining + "s (" + getFormattedTime() + ")");
        System.out.println("Vanishing Active: " + vanishingTriggered);
        
        // Player Info
        System.out.println("\n--- Player ---");
        System.out.println("Position: (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("Hearts: " + playerHearts + "/" + STARTING_HEARTS);
        System.out.println("Mercy Active: " + mercyActive);
        
        // Clue Progress
        System.out.println("\n--- Clues ---");
        System.out.println("Collected: " + collectedLetters.size() + "/" + totalCluesInLevel);
        System.out.println("Letters: " + collectedLetters);
        System.out.println("All Collected: " + allCluesCollected);
        System.out.println("Opened Boxes: " + openedBoxPositions.size());  // FIX: Show opened box count
        
        // Score
        System.out.println("\n--- Score ---");
        System.out.println("Current Score: " + calculateScore());
        
        System.out.println("========================================\n");
    }
    
    public void printCompactStatus() {
        System.out.println("┌────────────────────────────────┐");
        System.out.println("Level: " + currentLevel + "/" + MAX_LEVEL + 
                           " | Hearts: " + playerHearts + "/" + STARTING_HEARTS +
                           " | Time: " + getFormattedTime() +
                           " | Clues: " + collectedLetters.size() + "/" + totalCluesInLevel);
        System.out.println("└────────────────────────────────┘");
    }
    
    public boolean isPlayerInDanger() {
        return (playerHearts <= 1 || timeRemaining <= 30);
    }
    
    public void debugMercyPopup() {
        System.out.println("=== MERCY POPUP DEBUG ===");
        System.out.println("showingMercyDeactivated: " + showingMercyDeactivated);
        System.out.println("mercyActive: " + mercyActive);
        System.out.println("allCluesCollected: " + allCluesCollected);
        if (showingMercyDeactivated) {
            long elapsed = System.currentTimeMillis() - mercyDeactivatedStartTime;
            System.out.println("Elapsed time: " + (elapsed / 1000) + " seconds");
            System.out.println("Time remaining: " + ((MERCY_POPUP_DURATION_MS - elapsed) / 1000) + " seconds");
        }
        System.out.println("========================");
    }

    /**
     * Gets a progress report string
     * @return Formatted progress string
     */
    public String getProgressReport() {
        return "Level " + currentLevel + "/" + MAX_LEVEL + 
               " - " + collectedLetters.size() + "/" + totalCluesInLevel + " clues";
    }
    public boolean validateGameState() {
        // Check for invalid values
        if (playerHearts < 0 || playerHearts > STARTING_HEARTS) return false;
        if (currentLevel < 0 || currentLevel > MAX_LEVEL) return false;
        if (timeRemaining < 0) return false;
        if (collectedLetters.size() > totalCluesInLevel) return false;
        
        return true;
    }
 // ============================================
 // PART 13: FINAL CHALLENGE SYSTEM
 // ============================================

 /**
  * Gets the current game state (MAZE or FINAL)
  */
 public String getCurrentGameState() {
     return currentGameState;
 }

 /**
  * Gets the current final answer input string
  */
 public String getFinalAnswerInput() {
     return finalAnswerInput;
 }

 /**
  * Called when the player reaches the Exit (E) tile with all clues.
  * Triggers the visual Final Challenge mode.
  */
 public void startFinalChallenge() {
     if (allCluesCollected && currentGameState.equals(STATE_MAZE_EXPLORATION)) {
         currentGameState = STATE_FINAL_CHALLENGE;
         isPaused = true; // Pause game during final challenge
         finalAnswerInput = ""; // Clear any previous input
         
         System.out.println("\n=================================");
         System.out.println("★ ENTERING FINAL CHALLENGE MODE");
         System.out.println("=================================");
         System.out.println("Arrange your collected letters to spell the word!");
         System.out.println("Type your answer using the keyboard.");
         System.out.println("=================================\n");
     }
 }

 /**
  * Handles character input from GamePanel during Final Challenge.
  * @param c The character typed by the player.
  */
 public void appendFinalAnswer(char c) {
     if (!currentGameState.equals(STATE_FINAL_CHALLENGE)) {
         return; // Only process input during Final Challenge
     }
     
     // Handle Backspace/Delete
     if (c == KeyEvent.VK_BACK_SPACE || c == (char) 8) {
         if (!finalAnswerInput.isEmpty()) {
             finalAnswerInput = finalAnswerInput.substring(0, finalAnswerInput.length() - 1);
             System.out.println("Backspace - Current input: " + finalAnswerInput);
         }
     }
     // Handle Submission (Enter/Return)
     else if (c == KeyEvent.VK_ENTER || c == '\n') {
         System.out.println("Enter pressed - Submitting answer: " + finalAnswerInput);
         submitFinalAnswer();
     }
     // Handle typing letters (limit length)
     else if (Character.isLetter(c) && finalAnswerInput.length() < MAX_FINAL_ANSWER_LENGTH) {
         finalAnswerInput += Character.toUpperCase(c);
         System.out.println("Letter added - Current input: " + finalAnswerInput);
     }
 }

 /**
  * Checks the final answer against the target word.
  * Called when player presses ENTER.
  */
 /**
  * Checks the final answer against the target word.
  * Called when player presses ENTER.
  */
 public void submitFinalAnswer() {
	    if (!currentGameState.equals(STATE_FINAL_CHALLENGE)) {
	        System.out.println("⚠️ submitFinalAnswer called but not in FINAL_CHALLENGE state!");
	        return;
	    }
	    
	    System.out.println("\n╔════════════════════════════════════╗");
	    System.out.println("║   SUBMITTING FINAL ANSWER          ║");
	    System.out.println("╚════════════════════════════════════╝");
	    System.out.println("Your answer: " + finalAnswerInput);
	    System.out.println("Correct answer: " + targetWord);
	    System.out.println("Current Level: " + currentLevel);
	    System.out.println("Max Level: " + MAX_LEVEL);
	    
	    // Check if answer is correct
	    boolean isCorrect = finalAnswerInput.equalsIgnoreCase(targetWord);
	    System.out.println("Is Correct: " + isCorrect);
	    
	    // === SET FINAL FEEDBACK VARIABLES ===
	    showingFinalFeedback = true;
	    finalAnswerCorrect = isCorrect;
	    finalFeedbackStartTime = System.currentTimeMillis();
	    
	    System.out.println("✅ Feedback flags set:");
	    System.out.println("   showingFinalFeedback = " + showingFinalFeedback);
	    System.out.println("   finalAnswerCorrect = " + finalAnswerCorrect);
	    System.out.println("   Feedback will show for 3 seconds");
	    
	    if (isCorrect) {
	        audioManager.playSoundEffect(SOUND_LEVEL_COMPLETE);
	        System.out.println("🔊 Playing level complete sound");
	        
	        if (currentLevel >= MAX_LEVEL) {
	            System.out.println("🎉 THIS IS LEVEL 5 - VICTORY SHOULD TRIGGER!");
	        } else {
	            System.out.println("➡️ More levels to go - will advance to Level " + (currentLevel + 1));
	        }
	    } else {
	        audioManager.playSoundEffect(SOUND_FINAL_WRONG);
	        System.out.println("🔊 Playing wrong answer sound");
	        System.out.println("❌ Will reset to Level 1");
	    }
	    
	    // Reset state
	    currentGameState = STATE_MAZE_EXPLORATION;
	    isPaused = false;
	    finalAnswerInput = "";
	    
	    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
	}
 // STEP 9:  ADD CLEANUP METHOD AT END OF CLASS
/**
* Call this when the game closes to cleanup audio resources
*/
public void cleanup() {
  if (audioManager != null) {
      audioManager.cleanup();
  }
}

private void advanceStory() {
    showingStory = false;
    currentGameState = STATE_MAZE_EXPLORATION;
    
    // Play transition sound (remove)
    if (audioManager != null) {
    }
    
    System.out.println("✅ Story frame " + (currentStoryFrame + 1) + " complete");
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    
    // Determine what happens next
    if (currentStoryFrame == 0) {
        // ╔══ INTRO STORY → Level 1 ══╗
        System.out.println("📖 Intro story complete");
        System.out.println("🎮 Loading Level 1...");
        loadLevel(1);
        
    } else if (currentStoryFrame >= 1 && currentStoryFrame <= 4) {
        // ╔══ POST-LEVEL STORY → Next Level ══╗
        int completedLevel = currentStoryFrame;
        int nextLevel = currentStoryFrame + 1;
        
        System.out.println("📖 Level " + completedLevel + " story complete");
        
        if (nextLevel <= MAX_LEVEL) {
            System.out.println("🎮 Preparing Level " + nextLevel + "...");
            
            // Reset hearts before loading next level
            this.playerHearts = STARTING_HEARTS;
            System.out.println("   ❤️  Hearts reset: " + STARTING_HEARTS + "/" + STARTING_HEARTS);
            
            // Restore mercy for new level
            this.mercyActive = true;
            System.out.println("   🛡️  Mercy: ACTIVE");
            
            // Reset game over flag
            this.showingGameOver = false;
            this.gameOverReason = "";
            this.gameOverSoundPlayed = false;
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // Load next level
            loadLevel(nextLevel);
        }
        
    } else if (currentStoryFrame == 5) {
        // ╔═══ VICTORY STORY (Frame 6) → Show Victory Panel ═══╗
        System.out.println("╔═══════════════════════════════════╗");
        System.out.println("║  VICTORY STORY COMPLETE!          ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println("📖 Victory story (Frame 6) complete");
        System.out.println("🏆 Setting state to VICTORY...");
        
        // ✅ CRITICAL FIX: Stop showing story BEFORE setting victory state
        showingStory = false;  // ← ADD THIS LINE FIRST
        
        // Stop the game
        isGameRunning = false;
        isPaused = false;
        
        // Calculate final statistics
        totalGameTimeSeconds = getTotalGameTime();
        
        // ✅ SET VICTORY STATE - This is what GamePanel checks!
        currentGameState = STATE_VICTORY;
        
        System.out.println("✅ currentGameState = " + currentGameState);
        System.out.println("✅ isShowingVictory() = " + isShowingVictory());
        System.out.println("✅ showingStory = " + showingStory);  
        System.out.println("✅ isGameRunning = " + isGameRunning);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Final Statistics:");
        System.out.println("   ⏱️  Total Time: " + formatTime(totalGameTimeSeconds));
        System.out.println("   ❤️  Hearts: " + playerHearts + "/" + STARTING_HEARTS);
        System.out.println("   🔄  Resets: " + levelHistoryManager.getResetCount());
        System.out.println("   🔥  Streak: " + levelHistoryManager.getLongestStreak());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎮 GamePanel should now detect victory state!");
    }
}
//============================================
//PART 14: STORY FRAME SYSTEM
//============================================

/**
* Initializes all story frames with images and text
*/
private StoryFrame[] initializeStoryFrames() {
    System.out.println("🎬 Initializing story frames...");
    
    StoryFrame[] frames = new StoryFrame[6];
    
    try {
        // Frame 0: Game Intro (before Level 1)
        frames[0] = new StoryFrame(1, 
            "/assets/StoryLoad/frame 1.jpg",  // ✅ NEW PATH
            "Welcome brave adventurer! The ancient maze awaits those who dare to challenge it."
            + "\nCollect clues, answer riddles, and prove your worth to escape!");
        
        // Frame 1: After Level 1
        frames[1] = new StoryFrame(2,
            "/assets/StoryLoad/frame 2.jpg",  // ✅ NEW PATH
            "Well done! You've conquered the first challenge. But the maze grows more treacherous "
            + "\n with each level. Steel yourself for what lies ahead!");
        
        // Frame 2: After Level 2
        frames[2] = new StoryFrame(3,
            "/assets/StoryLoad/frame 3.jpg",  // ✅ NEW PATH
            "Impressive! Your wit and courage shine through. The maze respects your determination, "
            + "\nbut it will not yield easily. Press onward, seeker of truth!");
        
        // Frame 3: After Level 3
        frames[3] = new StoryFrame(4,
            "/assets/StoryLoad/frame 4.jpg",  // ✅ NEW PATH
            "Remarkable! Few have made it this far. The maze's secrets are within your grasp. "
            + "\nTwo trials stand between you and ultimate victory!");
        
        // Frame 4: After Level 4
        frames[4] = new StoryFrame(5,
            "/assets/StoryLoad/frame 5.jpg",  // ✅ NEW PATH
            "Extraordinary! You stand at the threshold of the final challenge. "
            + "\nOne last trial remains. Will you claim your destiny?");
        
        // Frame 5: Victory (after Level 5)
        frames[5] = new StoryFrame(6,
            "/assets/StoryLoad/frame 6.jpg",  // ✅ NEW PATH
            "VICTORY! You have conquered the maze and proven yourself worthy! "
            + "\nYour name will echo through the halls of legends. Congratulations, champion!");
        
        System.out.println("✓ All " + frames.length + " story frames initialized");
        
    } catch (Exception e) {
        System.err.println("❌ Error initializing story frames: " + e.getMessage());
        e.printStackTrace();
    }
    
    return frames;
}

/**
* Starts displaying a specific story frame
*/
public void startStoryFrame(int frameIndex) {
 if (frameIndex < 0 || frameIndex >= storyFrames.length) {
     System.err.println("❌ Invalid story frame index: " + frameIndex);
     return;
 }
 
 currentStoryFrame = frameIndex;
 showingStory = true;
 storyStartTime = System.currentTimeMillis();
 storySkipRequested = false;
 storyTypingCompleted = false;
 
 // Reset animation
 storyFrames[frameIndex].reset();
 
 // Play ambient sound if available (remove)
 if (audioManager != null) {
   
 }
 
 System.out.println("📖 Showing story frame " + (frameIndex + 1));
}

/**
* Updates story animation and checks for auto-advance
* MUST be called from update() method
*/
public void updateStory() {
    if (!showingStory) {
        System.out.println("⚠️ updateStory called but showingStory is false");
        return;
    }
    
    if (currentStoryFrame < 0 || currentStoryFrame >= storyFrames.length) {
        System.err.println("❌ Invalid story frame index: " + currentStoryFrame);
        return;
    }
    
    StoryFrame frame = storyFrames[currentStoryFrame];
    
    if (frame == null) {
        System.err.println("❌ Story frame " + currentStoryFrame + " is null!");
        return;
    }
    
    // Update typewriter animation
    boolean stillTyping = frame.updateAnimation();
    
    // Check if typing just completed
    if (!stillTyping && !storyTypingCompleted) {
        storyTypingCompleted = true;
        storyStartTime = System.currentTimeMillis();
        System.out.println("✓ Story typing completed - auto-advance in 8 seconds");
    }
    
    // Auto-advance after 8 seconds (only after typing completes)
    if (storyTypingCompleted) {
        long elapsed = System.currentTimeMillis() - storyStartTime;
        
        // Debug output every 2 seconds
        if (elapsed % 2000 < 20) {
            System.out.println("⏰ Auto-advance timer: " + (elapsed / 1000) + "s / 8s");
        }
        
        if (elapsed >= storyAutoAdvanceTime) {
            System.out.println("⏩ Auto-advancing story (timer expired)");
            advanceStory();
        }
    }
}


/**
* Handles SPACE or ENTER key during story
*/
	public void handleStorySkip() {
	    if (!showingStory) {
	        System.out.println("⚠️ handleStorySkip called but not showing story");
	        return;
	    }
	    
	    StoryFrame frame = storyFrames[currentStoryFrame];
	    
	    if (frame == null) {
	        System.err.println("❌ Story frame is null!");
	        return;
	    }
	    
	    // Check typing status
	    if (!frame.isTypingComplete()) {
	        // First press: Complete typing instantly
	        frame.completeTyping();
	        storyTypingCompleted = true;
	        storyStartTime = System.currentTimeMillis();
	        System.out.println("⏩ Story typing completed instantly");
	    } else {
	        // Second press: Advance to next frame
	        System.out.println("⏩ Advancing story frame...");
	        advanceStory();
	    }
	}

/**
* Skips all remaining story frames (ESC key)
*/
public void skipAllStory() {
 if (!showingStory) return;
 
 System.out.println("⏭️ Skipping all story frames");
 showingStory = false;
 currentGameState = STATE_MAZE_EXPLORATION;
 
 // Stop story sounds
 if (audioManager != null) {
     audioManager.stopBackgroundMusic();
 }
}

/**
* Determines if a story should play after completing a level
*/
private boolean shouldShowStoryAfterLevel(int completedLevel) {
 // Story appears after levels 1-4 (not after level 5, that's handled by victory)
 return completedLevel >= 1 && completedLevel <= 4;
}

/**
* Triggers the appropriate story frame after level completion
*/
private void triggerPostLevelStory(int completedLevel) {
 if (shouldShowStoryAfterLevel(completedLevel)) {
     // Story frame indices: 1=after L1, 2=after L2, 3=after L3, 4=after L4
     startStoryFrame(completedLevel);
 } else {
     // No story, just load next level
     int nextLevel = completedLevel + 1;
     if (nextLevel <= MAX_LEVEL) {
         loadLevel(nextLevel);
     }
 }
}

//Story getters
public boolean isShowingStory() { 
 return showingStory; 
}

public StoryFrame getCurrentStoryFrame() {
 if (currentStoryFrame >= 0 && currentStoryFrame < storyFrames.length) {
     return storyFrames[currentStoryFrame];
 }
 return null;
}

public int getCurrentStoryFrameIndex() {
 return currentStoryFrame;
}


public void debugVictoryFlow() {
    System.out.println("\n╔════════════════════════════════════╗");
    System.out.println("║     VICTORY FLOW DEBUG INFO        ║");
    System.out.println("╚════════════════════════════════════╝");
    System.out.println("showingFinalFeedback: " + showingFinalFeedback);
    System.out.println("finalAnswerCorrect: " + finalAnswerCorrect);
    System.out.println("currentLevel: " + currentLevel);
    System.out.println("MAX_LEVEL: " + MAX_LEVEL);
    System.out.println("currentGameState: " + currentGameState);
    System.out.println("showingStory: " + showingStory);
    System.out.println("isGameRunning: " + isGameRunning);
    
    if (showingFinalFeedback) {
        long elapsed = System.currentTimeMillis() - finalFeedbackStartTime;
        System.out.println("Feedback elapsed: " + elapsed + "ms / " + FINAL_FEEDBACK_DISPLAY_DURATION_MS + "ms");
    }
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
}
}