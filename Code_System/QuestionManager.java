package Code_System;

import java.util.ArrayList;

import Code_System.QuestionManager.Question;

/**
 * QuestionManager.java - 
 * CRITICAL FIX: Question class is now STATIC
 */
public class QuestionManager {

    // ==================== PROPERTIES ====================
    
    private ArrayList<Question> questions;
    private ArrayList<Question> usedQuestions;
    
    // Track current question index for each level (1-5)
    private int[] questionIndexByLevel;

    
    // ==================== INNER CLASS - QUESTION (STATIC!) ====================
    
    /**
     * CRITICAL FIX: Made this class STATIC
     * This allows other classes to reference QuestionManager.Question
     */
    public static class Question {
        private String questionText;
        private String[] choices;
        private int correctAnswerIndex;
        private int difficulty;

        // Constructor
        public Question(String questionText, String[] choices, int correctAnswerIndex, int difficulty) {
            this.questionText = questionText;
            this.choices = choices;
            this.correctAnswerIndex = correctAnswerIndex;
            this.difficulty = difficulty;
        }

        // Getter Methods
        public String getQuestionText() { 
            return questionText; 
        }
        
        public String[] getChoices() { 
            return choices; 
        }
        
        public int getCorrectAnswerIndex() { 
            return correctAnswerIndex; 
        }
        
        public int getDifficulty() { 
            return difficulty; 
        }

        /**
         * CRITICAL METHOD: Maps keyboard input to answer validation
         * Converts 'A', 'B', 'C', 'D' keys to indices 0, 1, 2, 3
         */
        public boolean checkAnswer(char selectedAnswer) {
            // Convert the selected key to uppercase
            char upperCaseAnswer = Character.toUpperCase(selectedAnswer);
            int selectedIndex = -1; 
            
            // Map the key to the index
            if (upperCaseAnswer == 'A') selectedIndex = 0;
            else if (upperCaseAnswer == 'B') selectedIndex = 1;
            else if (upperCaseAnswer == 'C') selectedIndex = 2;
            else if (upperCaseAnswer == 'D') selectedIndex = 3;

            // Compare the index to the correct index
            return selectedIndex == this.correctAnswerIndex;
        }
    }

    
    // ==================== CONSTRUCTOR ====================
    
    public QuestionManager() {
        questions = new ArrayList<>();
        usedQuestions = new ArrayList<>();
        questionIndexByLevel = new int[6]; // Index 0 unused, 1-5 for levels
        loadQuestions();
    }

    
    // ==================== LOAD QUESTIONS ====================
    
    private void loadQuestions() {
    	// ==========================================
    	// Difficulty 1 - Easy
    	// ==========================================
    	questions.add(new Question("What symbol is used to end a statement in Java?",
    	        new String[]{"Semicolon (;)", "Colon (:)", "Period (.)", "Comma (,)"}, 0, 1));
    	questions.add(new Question("What keyword is used to print output in Java?",
    	        new String[]{"print", "echo", "display", "show"}, 0, 1));
    	questions.add(new Question("Which data type is used to store whole numbers?",
    	        new String[]{"int", "string", "float", "char"}, 0, 1));
    	questions.add(new Question("What symbol is used for single-line comments in Java?",
    	        new String[]{"//", "/*", "#", "--"}, 0, 1));
    	questions.add(new Question("What is the correct way to declare a variable in Java?",
    	        new String[]{"int age = 25;", "age = 25", "var age", "declare age = 25"}, 0, 1));
        
    	// ==========================================
    			// Difficulty 2 - Medium
    			// ==========================================
    			questions.add(new Question("What does LIFO stand for in Stack?",
    			        new String[]{"Last In First Out", "Last In Final Out", "List In First Out", "Loop In First Out"}, 0, 2));
    			questions.add(new Question("Which operation adds an element to a Stack?",
    			        new String[]{"push()", "pop()", "peek()", "add()"}, 0, 2));
    			questions.add(new Question("Which operation removes the top element from a Stack?",
    			        new String[]{"remove()", "pop()", "delete()", "pull()"}, 1, 2));
    			questions.add(new Question("What does peek() do in a Stack?",
    			        new String[]{"Removes top element", "Views top element without removing", "Adds an element", "Clears the stack"}, 1, 2));
    			questions.add(new Question("What happens when you try to pop() from an empty Stack?",
    			        new String[]{"Returns null", "Stack Underflow error", "Creates new element", "Stack grows"}, 1, 2));
        
    			// ==========================================
    			// Difficulty 3 - Medium-Hard
    			// ==========================================
    			questions.add(new Question("What does FIFO stand for in Queue?",
    			        new String[]{"First In First Out", "Fast In First Out", "First Input Final Output", "Front In Front Out"}, 0, 3));
    			questions.add(new Question("Which operation adds an element to the rear of a Queue?",
    			        new String[]{"enqueue()", "push()", "insert()", "add()"}, 0, 3));
    			questions.add(new Question("Which operation removes an element from the front of a Queue?",
    			        new String[]{"dequeue()", "pop()", "remove()", "poll()"}, 0, 3));
    			questions.add(new Question("What is a Circular Queue?",
    			        new String[]{"Queue with no end", "Queue where rear connects to front", "Queue with priority", "Queue with two ends"}, 1, 3));
    			questions.add(new Question("Which data structure uses Queue for traversal?",
    			        new String[]{"DFS", "BFS", "Binary Search", "Quick Sort"}, 1, 3));
    			
    			// ==========================================
    			// Difficulty 4 - Hard
    			// ==========================================
    			questions.add(new Question("Time complexity of array access by index?",
    			        new String[]{"O(n)", "O(log n)", "O(1)", "O(n²)"}, 2, 4));
    			questions.add(new Question("Worst-case search in unsorted array?",
    			        new String[]{"O(1)", "O(log n)", "O(n)", "O(n log n)"}, 2, 4));
    			questions.add(new Question("Best search for sorted array?",
    			        new String[]{"Linear Search", "Binary Search", "Jump Search", "Sequential Search"}, 1, 4));
    			questions.add(new Question("What error occurs for invalid array index in Java?",
    			        new String[]{"NullPointer", "IndexOutOfBounds", "ArrayIndexOutOfBounds", "StackOverflow"}, 2, 4));
    			questions.add(new Question("Space complexity of n x n 2D array?",
    			        new String[]{"O(n)", "O(n²)", "O(log n)", "O(1)"}, 1, 4));
    			// ==========================================
    			// Difficulty 5 - Very Hard
    			// ==========================================
    			questions.add(new Question("What does DFS stand for in graph traversal?",
    			        new String[]{"Depth First Search", "Direct First Search", "Data First Sort", "Deep Fast Search"}, 0, 5));
    			questions.add(new Question("What does BFS stand for in graph traversal?",
    			        new String[]{"Binary First Search", "Breadth First Search", "Best First Sort", "Branch First Search"}, 1, 5));
    			questions.add(new Question("Which algorithm finds shortest path in weighted graphs?",
    			        new String[]{"DFS", "BFS", "Dijkstra's", "Binary Search"}, 2, 5));
    			questions.add(new Question("What data structure represents graph adjacency?",
    			        new String[]{"Array", "Adjacency List", "Stack", "Queue"}, 1, 5));
    			questions.add(new Question("What is a cycle in a graph?",
    			        new String[]{"Path with no edges", "Path that returns to start node", "Disconnected nodes", "Single vertex"}, 1, 5));
    			    }
    

    
    // ==================== GET NEXT QUESTION (SEQUENTIAL) ====================
    
    /**
     * Gets the next question in sequence for the specified level
     * This prevents repetition by going through questions in order
     * @param level Difficulty level (1-5)
     * @return Next question for that level, or null if none available
     */
    public Question getNextQuestion(int level) {
        ArrayList<Question> levelQuestions = new ArrayList<>();

        // Get all questions matching the difficulty level
        for (Question q : questions) {
            if (q.getDifficulty() == level) {
                levelQuestions.add(q);
            }
        }

        // Return null if no questions available for this level
        if (levelQuestions.isEmpty()) {
            System.err.println("Warning: No questions available for level " + level);
            return null;
        }

        // Get current index for this level
        int currentIndex = questionIndexByLevel[level];
        
        // Wrap around if we've gone through all questions
        if (currentIndex >= levelQuestions.size()) {
            currentIndex = 0;
            questionIndexByLevel[level] = 0;
        }

        // Get the question at current index
        Question selected = levelQuestions.get(currentIndex);
        
        // Increment index for next time
        questionIndexByLevel[level]++;

        return selected;
    }

    
    // ==================== GET RANDOM QUESTION (DEPRECATED) ====================
    
    /**
     * @deprecated Use getNextQuestion() instead for sequential questions
     */
    @Deprecated
    public Question getRandomQuestion(int level) {
        ArrayList<Question> availableQuestions = new ArrayList<>();

        for (Question q : questions) {
            if (q.getDifficulty() == level) {
                availableQuestions.add(q);
            }
        }

        if (availableQuestions.isEmpty()) {
            System.err.println("Warning: No questions available for level " + level);
            return null;
        }

        int randomIndex = (int) (Math.random() * availableQuestions.size());
        Question selected = availableQuestions.get(randomIndex);

        return selected;
    }

    
    // ==================== GET FINAL QUESTION ====================
    
    public Question getFinalQuestion() {
        return new Question(
                "What is the time complexity of the best comparison-based sorting algorithm?",
                new String[]{"O(n)", "O(n log n)", "O(log n)", "O(n²)"},
                1, // Correct: O(n log n)
                5
        );
    }

    
    // ==================== CHECK ANSWER ====================
    
    public boolean checkAnswer(Question q, int selectedIndex) {
        return selectedIndex == q.getCorrectAnswerIndex();
    }

    
    // ==================== RESET QUESTIONS ====================
    
    /**
     * Resets question indices back to 0 for all levels
     * Call this when restarting the game or loading a new level
     */
    public void resetQuestions() {
        // Reset all level indices to 0
        for (int i = 0; i < questionIndexByLevel.length; i++) {
            questionIndexByLevel[i] = 0;
        }
        
        usedQuestions.clear();
        
        System.out.println("✓ Question indices reset");
    }

    
    // ==================== RESET SPECIFIC LEVEL ====================
    
    /**
     * Resets question index for a specific level only
     * @param level Level to reset (1-5)
     */
    public void resetLevel(int level) {
        if (level >= 1 && level <= 5) {
            questionIndexByLevel[level] = 0;
            System.out.println("✓ Question index reset for level " + level);
        }
    }

    
    // ==================== UTILITY METHODS ====================
    
    public int getRemainingQuestionsCount() {
        return questions.size();
    }

    
    public ArrayList<Question> getQuestionsByLevel(int level) {
        ArrayList<Question> levelQuestions = new ArrayList<>();
        for (Question q : questions) {
            if (q.getDifficulty() == level) {
                levelQuestions.add(q);
            }
        }
        return levelQuestions;
    }

    
    /**
     * Gets the number of questions available for a specific level
     */
    public int getQuestionCountByLevel(int level) {
        int count = 0;
        for (Question q : questions) {
            if (q.getDifficulty() == level) {
                count++;
            }
        }
        return count;
    }

    
    /**
     * Gets the current question index for a level
     */
    public int getCurrentQuestionIndex(int level) {
        if (level >= 1 && level <= 5) {
            return questionIndexByLevel[level];
        }
        return 0;
    }

    
    /**
     * Gets a progress string showing current question number
     */
    public String getProgressString(int level) {
        int current = getCurrentQuestionIndex(level);
        int total = getQuestionCountByLevel(level);
        
        int displayCurrent = current + 1;
        if (displayCurrent > total) {
            displayCurrent = 1;
        }
        
        return "Question " + displayCurrent + "/" + total;
    }

    
    /**
     * Print all questions (for testing/debugging)
     */
    public void printAllQuestions() {
        System.out.println("=== ALL QUESTIONS ===");
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println((i + 1) + ". " + q.getQuestionText());
            String[] choices = q.getChoices();
            for (int j = 0; j < choices.length; j++) {
                System.out.println("   " + (char) ('A' + j) + ". " + choices[j]);
            }
            System.out.println("   Correct Answer: " + (char) ('A' + q.getCorrectAnswerIndex()));
            System.out.println("   Difficulty: " + q.getDifficulty());
            System.out.println("-------------------------");
        }
    }
}