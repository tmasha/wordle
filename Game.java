import java.util.*;
import java.io.*;

public class Game {
  // things I want to access over and over again
  public static Random random = new Random();
  public static String[] words = new String[2309];
  public static Scanner input = new Scanner(System.in);

  public static void main(String[] args) throws FileNotFoundException {
    
    // reading the words from the file
    File file = new File("words.txt");
    Scanner scanner = new Scanner(file);
    // there are 2309 words in the file
    for (int i = 0; i < 2309; ++i) {
      words[i] = scanner.next();
    }
    scanner.close();
    
    // accessing a random word
    String targetWord = getRandomWord();
    System.out.println("Random word: " + targetWord);
    



  }

  // accessing a random word
  public static String getRandomWord() {
    int randomIdx = random.nextInt(2309);
    return words[randomIdx];
  }
  
  // play an instance of the game
  public static void playGame() {
    // get the word
    String targetWord = getRandomWord();

    // user can guess 6 existing words
    for (int attempts = 6; attempts >= 0; --attempts) {
      String word = input.nextLine();
      
    }
  }
}
