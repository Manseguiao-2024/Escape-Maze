package Code_System;

public class Cluebox {
	 // 1. Properties
	 private int x;                 
	 private int y;                
	 private String questionText;  
	 private String correctAnswer;  
	 private char letterReward;     
	 private boolean collected;     

	 // 2. Constructor
	 public Cluebox(int x, int y, String questionText, String correctAnswer, char letterReward) {
	     this.x = x;
	     this.y = y;
	     this.questionText = questionText;
	     this.correctAnswer = correctAnswer;
	     this.letterReward = letterReward;
	     this.collected = false; 
	 }

	 // 3. Check Answer Method
	 public boolean checkAnswer(String playerAnswer) {
	     if (playerAnswer == null) return false;
	     return playerAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
	 }

	 // 4. Interact Method
	 public char interact(String playerAnswer) {
	     System.out.println("=== ClueBox Interaction ===");
	     System.out.println("Location: (" + x + ", " + y + ")");
	     System.out.println();

	     if (collected) {
	         System.out.println("Status: This clue has already been collected.");
	         System.out.println("-------------------------------");
	         return ' ';
	     }

	     // Print the question
	     System.out.println("Question: " + questionText);
	     System.out.println("Player's Answer: " + (playerAnswer == null ? "null" : "\"" + playerAnswer + "\""));
	     System.out.println();

	     // Check answer
	     if (checkAnswer(playerAnswer)) {
	         collected = true;
	         System.out.println("Result:  Correct!");
	         System.out.println("You earned the letter: '" + letterReward + "'");
	        
	         return letterReward;
	     } else {
	         System.out.println("Result:  Wrong!");
	         System.out.println("Correct Answer: \"" + correctAnswer + "\"");
	         System.out.println("You received: ' ' (no letter)");
	        
	         return ' ';
	     }
	 }

	 // 5. Getter Methods
	 public int getX() { return x; }
	 public int getY() { return y; }
	 public String getQuestionText() { return questionText; }
	 public char getLetterReward() { return letterReward; }
	 public boolean isCollected() { return collected; }

	}
