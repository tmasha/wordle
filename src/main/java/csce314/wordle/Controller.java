package csce314.wordle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import javax.print.DocFlavor;
import java.util.*;
import java.io.*;

public class Controller {
    // get the board panes
    @FXML Pane a1, a2, a3, a4, a5,
         b1, b2, b3, b4, b5,
         c1, c2, c3, c4, c5,
         d1, d2, d3, d4, d5,
         e1, e2, e3, e4, e5,
         f1, f2, f3, f4, f5,
    wordPane;

    @FXML
    private Pane[][] panes = new Pane[6][5];

    @FXML Label wordLabel, loginText;

    @FXML
    Group moveThis, statsGroup;

    // keyboard
    @FXML Button a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, enter, backspace, reset, quit;

    @FXML
    Button[] buttons = new Button[28];

    @FXML
    Label[] barLabels = new Label[6];

    @FXML
    Rectangle bar1, bar2, bar3, bar4, bar5, bar6;

    @FXML Rectangle[] bars = new Rectangle[6];

    @FXML Label played, winP, currS, maxS, i11, i21, i31, i41, i51, i61;

    // any vars unrelated to the FXML:

    // word stuff
    private Scene scene;
    int[] counts = new int[26]; // number of occurrences of letter in word

    private String word; // word user needs to guess
    private Stack<Character> guess; // the guess we are building
    private HashSet<Character> seen; // seen letters to not reuse
    private boolean win = false;
    private int paneRow;
    private int paneColumn;
    private int priorOperation;
    private boolean statsIsOut = false;

    private String username = "";
    private boolean gameOver = false;

    private String[] wordList = new String[2309];
    Random random = new Random();




    @FXML void setScene(Scene s) {
        scene = s;
        scene.setOnKeyPressed(this::handleKeyPress);
    }

    @FXML void initialize() throws FileNotFoundException {
        // load in the words
        String path = Main.class.getResource("Words.txt").getPath();
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        for (int i = 0; i < 2309; ++i) {
            wordList[i] = scanner.next();
        }
        scanner.close();

        paneRow = 0;
        paneColumn = 0;
        guess = new Stack<>();
        seen = new HashSet<>();

        bars[0] = bar1;
        bars[1] = bar2;
        bars[2] = bar3;
        bars[3] = bar4;
        bars[4] = bar5;
        bars[5] = bar6;

        barLabels[0] = i11;
        barLabels[1] = i21;
        barLabels[2] = i31;
        barLabels[3] = i41;
        barLabels[4] = i51;
        barLabels[5] = i61;

        // I am sorry. JavaFX refused to let me do this any other way.
        panes[0][0] = a1;
        panes[0][1] = a2;
        panes[0][2] = a3;
        panes[0][3] = a4;
        panes[0][4] = a5;
        panes[1][0] = b1;
        panes[1][1] = b2;
        panes[1][2] = b3;
        panes[1][3] = b4;
        panes[1][4] = b5;
        panes[2][0] = c1;
        panes[2][1] = c2;
        panes[2][2] = c3;
        panes[2][3] = c4;
        panes[2][4] = c5;
        panes[3][0] = d1;
        panes[3][1] = d2;
        panes[3][2] = d3;
        panes[3][3] = d4;
        panes[3][4] = d5;
        panes[4][0] = e1;
        panes[4][1] = e2;
        panes[4][2] = e3;
        panes[4][3] = e4;
        panes[4][4] = e5;
        panes[5][0] = f1;
        panes[5][1] = f2;
        panes[5][2] = f3;
        panes[5][3] = f4;
        panes[5][4] = f5;

        // buttons
        buttons[0] = a;
        buttons[1] = b;
        buttons[2] = c;
        buttons[3] = d;
        buttons[4] = e;
        buttons[5] = f;
        buttons[6] = g;
        buttons[7] = h;
        buttons[8] = i;
        buttons[9] = j;
        buttons[10] = k;
        buttons[11] = l;
        buttons[12] = m;
        buttons[13] = n;
        buttons[14] = o;
        buttons[15] = p;
        buttons[16] = q;
        buttons[17] = r;
        buttons[18] = s;
        buttons[19] = t;
        buttons[20] = u;
        buttons[21] = v;
        buttons[22] = w;
        buttons[23] = x;
        buttons[24] = y;
        buttons[25] = z;
        buttons[26] = enter;
        buttons[27] = backspace;

    }

    @FXML void initializeRound() {
        System.out.println("Choosing word:");
        moveStatsOut();
        chooseWord();
        guess.clear();
        seen.clear();
        gameOver = false;
        paneRow = 0;
        paneColumn = 0;

        wordPane.setStyle("-fx-background-color: black;");
        wordLabel.setText("");

        Arrays.fill(counts, 0);
        for (char c : word.toCharArray()) {
            ++counts[c - 'a'];
        }
        win = false;
        System.out.println("Fixing board and keyboard:");
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 5; ++j) {
                panes[i][j].setStyle("-fx-background-color: black; -fx-border-color: gray; fx-border-width: 1;");
                Label child = (Label) panes[i][j].getChildren().getFirst();
                child.setText("");
            }
        }

        // reset keyboard letters and colors
        for (int i = 0; i < 28; ++i) {
            buttons[i].defaultButtonProperty();
            Pane parent = (Pane) buttons[i].getParent();
            parent.setStyle("-fx-background-color: gray; -fx-background-radius: 10;");
        }
        System.out.println("Board initialized.");
    }

    // choose a random word and set word to it
    void chooseWord() {
        int idx = random.nextInt(2309);
        word = wordList[idx];
        System.out.println("Word: " + word);
    }



    // all input stuff

    @FXML void handleInput(String i) {
        wordLabel.setText("");

        System.out.println("Handling the input: " + i);

        // if the game is complete, do not accept input
        if (gameOver) {
            System.out.println("Can't input when game complete");
            wordLabel.setStyle("-fx-background-color: black; -fx-alignment: center");
            wordLabel.setText("PRESS \"RESET\" TO START A NEW GAME");
        }

        // handle input = backspace
        else if (i.equals("backspace")) {

            // go to next input if guess is empty, otherwise pop stack
            if (guess.empty()) {
                System.out.println("Invalid backspace input.");
            } else {
                if (paneColumn > 0) --paneColumn;

                Pane currPane = panes[paneRow][paneColumn];
                Label label = (Label) currPane.getChildren().getFirst();

                System.out.println("Valid backspace input. Popping the last letter.");
                System.out.println("Removing to label: " + paneRow + " " + paneColumn);
                label.setText("");
                guess.pop();
            }

        }

        // handle input = enter
        else if (i.equals("enter")) {
            // break if guess is full word, otherwise next input
            if (guess.size() == 5) {
                System.out.println("Valid guess locked in.");
                // build the g string
                String g = "";
                while (!guess.empty()) {
                    g = guess.peek() + g;
                    guess.pop();
                }
                System.out.println("Checking guess: " + g);


                // check if word is a valid word
                boolean wordIsPresent = false;
                for (String w : wordList) {
                    if (w.equals(g)) {
                        wordIsPresent = true;
                        break;
                    }
                }

                if (wordIsPresent) {
                    System.out.println("Word is valid");
                    handleGuess(g);
                    paneColumn = 0;
                } else {
                    System.out.println("Word is not valid");
                    wordLabel.setStyle("-fx-background-color: black; -fx-alignment: center");
                    wordLabel.setText(g.toUpperCase() + " IS NOT A VALID WORD");
                    System.out.println("Clearing labels.");
                    for (int j = 0; j < 5; ++j) {
                        Pane currPane = panes[paneRow][j];
                        Label label = (Label) currPane.getChildren().getFirst();
                        label.setText("");
                    }
                    paneColumn = 0;
                }

            } else {
                System.out.println("Invalid enter input.");
            }
        }

        // handle input = letter
        else if (Character.isLetter(i.charAt(0))) {

            char c = i.charAt(0);
            // if letter good and stack not full add it, otherwise continue
            if (seen.contains(c)) {
                System.out.println("Detected a wrong, already guessed letter " + c);
                wordLabel.setStyle("-fx-background-color: transparent; -fx-alignment: center");
                wordLabel.setText("YOU ALREADY GUESSED " + Character.toUpperCase(c));
            }
            else if (guess.size() < 5 && !seen.contains(c)) {
                System.out.println("Letter input " + c + " pushed to guess.");
                System.out.println("Adding to label: " + paneRow + " " + paneColumn);

                Pane currPane = panes[paneRow][paneColumn];
                Label label = (Label) currPane.getChildren().getFirst();

                label.setText(String.valueOf(c).toUpperCase());
                guess.push(c);
                if (paneColumn < 5) ++paneColumn;
            } else {
                System.out.println("Letter input " + c + " is invalid.");
            }

        }

        // do nothing otherwise
        System.out.println("After operation, paneRow and paneColumn are: " + paneRow + ", " + paneColumn);
    }



    // save the game as is
    @FXML void saveGame() throws IOException {
        // if game over don't save
        if (gameOver) {
            wordLabel.setStyle("-fx-background-color: black; -fx-alignment: center");
            wordLabel.setText("PRESS \"RESET\" TO START A NEW GAME");
            return;
        }
        // set up the outfile (save prev game if necessary)
        System.out.println("Saving.");
        String path = "src/main/resources/csce314/wordle/PrevGame.txt";
        System.out.println(path);
        File file = new File(path);
        FileWriter writer = new FileWriter(file);
        writer.write(word + "\n" + paneRow + "\n");

        for (int i = 0; i < paneRow; ++i) {
            String line = "";
            for (int j = 0; j < 5; ++j) {
                Label label = (Label) panes[i][j].getChildren().getFirst();
                line += label.getText();
                String style = panes[i][j].getStyle();
                if (style.contains("-fx-background-color: green;")) {
                    line += "g"; // green code
                } else if (style.contains("-fx-background-color: #aaaa3e")) {
                    line += "y"; // yellow code
                } else if (style.contains("-fx-background-color: #494949")) {
                    line += "r"; // gray code
                }
            }
            System.out.println("writing line to last save: " + line);
            writer.write(line + "\n");
        }
        writer.close();
    }

    // load the previous game
    @FXML void loadGame() throws FileNotFoundException {
        // if game over dont load
        if (gameOver) {
            wordLabel.setStyle("-fx-background-color: black; -fx-alignment: center");
            wordLabel.setText("PRESS \"RESET\" TO START A NEW GAME");
            return;
        }
        System.out.println("Loading.");
        initializeRound();
        String path = "src/main/resources/csce314/wordle/PrevGame.txt";
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        word = scanner.nextLine(); // get the word
        System.out.println("word set to " + word);
        paneRow = scanner.nextInt(); // get the row
        scanner.nextLine();
        System.out.println("paneRow set to " + paneRow);

        // outer loop loops row times
        for (int i = 0; i < paneRow; ++i) {

            String line = scanner.nextLine(); // get the next line
            Pane currPane = null;
            // loop through the chars in the line
            for (int j = 0; j < 10; ++j) {

                if (j % 2 == 0) { // even indices change the label
                    currPane = panes[i][j / 2];
                    Label label = (Label) currPane.getChildren().getFirst();
                    String letter = String.valueOf(line.charAt(j)).toUpperCase();
                    System.out.print("label: " + letter);
                    label.setText(letter);
                } else { // odd indices change the color
                    System.out.println(", color: " + g);
                    char color = line.charAt(j);
                    if (color == 'g') {
                        currPane.setStyle("-fx-background-color: green; -fx-border-color: gray; fx-border-width: 1;");
                    } else if (color == 'y') {
                        currPane.setStyle("-fx-background-color: #aaaa3e; -fx-border-color: gray; fx-border-width: 1;");
                    } else if (color == 'r') {
                        currPane.setStyle("-fx-background-color: #494949; -fx-border-color: gray; fx-border-width: 1;");
                    }

                }
            }
        }
        scanner.close();
    }

    @FXML void quitGame() {
        System.exit(0); // quit the game
    }

    // use this function in SceneBuilder
    @FXML void handleButtonClicked(ActionEvent e) {
        Button b = (Button) e.getSource();
        String i = b.getText().toLowerCase();

        if (i.equals("enter") || i.equals("backspace") || Character.isLetter(i.charAt(0))) {
            System.out.println("Handling input from button");
            handleInput(i);
        }

    }

    @FXML void handleKeyPress(KeyEvent e) {

        if (e.getCode() == KeyCode.ENTER) {
            handleInput("enter");
        } else if (e.getCode() == KeyCode.BACK_SPACE) {
            handleInput("backspace");
        } else {
            handleInput(e.getText().toLowerCase());
        }

    }

    @FXML
    void handleGuess(String guess) {
        // make a temp array that copies counts, so we can use it without affecting counts
        int[] temp = new int[26];
        System.arraycopy(counts, 0, temp, 0, 26);

        int corrects = 0; // keep track of correctly placed letters
        // loop through the current row
        for (int i = 0; i < 5; ++i) {
            char curr = guess.charAt(i);
            System.out.println(temp[curr - 'a']);
            // right letter in right place
            if (curr == word.charAt(i)) {
                System.out.println(i + ": Found correct letter " + curr + " in correct place");
                panes[paneRow][i].setStyle("-fx-background-color: green; -fx-border-color: gray;"); // tile turns green
                if(temp[curr - 'a'] > 0) --temp[curr - 'a']; // subtract 1 from tempcount
                ++corrects; // add 1 to corrects
            }

            // right letter in not right place
            else if (temp[curr - 'a'] > 0) { // and not green
                System.out.println(i + ": Found correct letter " + curr + " in wrong place");
                panes[paneRow][i].setStyle("-fx-background-color: #aaaa3e; -fx-border-color: gray;"); // tile turns yellow
                if(temp[curr - 'a'] > 0) --temp[curr - 'a'];
            }

            // wrong letter
            else if (word.indexOf(curr) == -1) { // and not green and not yellow
                System.out.println(i + ": Found wrong letter " + curr);
                panes[paneRow][i].setStyle("-fx-background-color: #494949; -fx-border-color: gray;");
                seen.add(curr);
                // disable the button

                buttons[curr - 'a'].getParent().setStyle("-fx-background-color: #494949; -fx-background-radius: 10;");
                buttons[curr - 'a'].cancelButtonProperty();
                // turn it a darker color
            }

            // the user entered too many of a correct letter, oh noes! :(
            else {
                System.out.println(i + ": Entered too many of a right letter " + curr);
                panes[paneRow][i].setStyle("-fx-background-color: #494949; -fx-border-color: gray;");
            }
        }

        // increment the Row and set the Column to 0 for the next guess.
        ++paneRow;
        paneColumn = 0;

        // handle when the game ends
        if (corrects == 5 || paneRow == 6) {
            gameOver = true;
            if (corrects == 5) win = true;
            wordPane.setStyle("-fx-background-color: gray; -fx-background-radius: 10;");
            wordLabel.setText(word.toUpperCase());

            // handle exception and stats page
            try {
                stats();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }

    }


    @FXML void stats() throws IOException {
        String path = "src/main/resources/csce314/wordle/Stats.txt";
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        // read in every line
        ArrayList<String> allLines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            allLines.add(scanner.nextLine().trim());
        }

        scanner.close();

        int gamesPlayed = Integer.parseInt(allLines.get(0)) + 1;
        int wins = Integer.parseInt(allLines.get(1));
        int currStreak = Integer.parseInt(allLines.get(2));
        int maxStreak = Integer.parseInt(allLines.get(3));
        int idx = 4;
        for (int i = 4; i < allLines.size(); i++) {
            if (allLines.get(i).contains(word)) {
                idx = i;
                break;
            }
        }
        System.out.println(allLines.get(idx));

        allLines.set(0, String.valueOf(gamesPlayed));
        if (win) {
            // if user won, wins and currStreak both increment by 1
            allLines.set(1, String.valueOf(++wins));
            allLines.set(2, String.valueOf(++currStreak));
            // if currStreak more than maxStreak, set maxStreak to currStreak
            if (currStreak > maxStreak) {
                allLines.set(3, String.valueOf(++maxStreak));
            }
            // increment the win to the number of guesses (paneRow)
            int targetRow = idx + paneRow;
            System.out.println(allLines.get(targetRow));
            int num = Integer.parseInt(allLines.get(targetRow));
            allLines.set(targetRow, String.valueOf(++num));
        } else {
            allLines.set(2, "0"); // set current streak to 0 if loss
        }

        // now we write the whole modified list back into Stats.txt
        FileWriter writer = new FileWriter(file);
        for (String line : allLines) {
            writer.write(line + "\n");
        }

        // show the stats bars
        if (win) {

            int[] values = new int[6];
            int locMax = 1;
            for (int i = 0; i < 6; ++i) {
                String target = allLines.get(i + idx + 1);
                values[i] = Integer.parseInt(target);
                if (locMax < values[i]) {
                    locMax = values[i];
                }
            }

            double[] props = new double[6];
            for (int i = 0; i < 6; ++i) {
                props[i] = (double) values[i] / locMax * 270.0;
                if (values[i] != 0) bars[i].setWidth(props[i]);
                else bars[i].setWidth(0.0);
                barLabels[i].setText(String.valueOf(values[i]));
            }

            played.setText(String.valueOf(gamesPlayed));
            winP.setText(String.valueOf((int)Math.round((double) wins / gamesPlayed * 100.0)));
            maxS.setText(String.valueOf(maxStreak));
            currS.setText(String.valueOf(currStreak));

            moveStatsIn();

        }

    }

    @FXML void moveStatsIn() {
        if (statsIsOut) {
            moveThis.setLayoutX(moveThis.getLayoutX() - 200);
            statsGroup.setLayoutX(statsGroup.getLayoutX() - 5000);
            statsIsOut = false;
        }
    }

    @FXML void moveStatsOut() {
        if (!statsIsOut) {
            moveThis.setLayoutX(moveThis.getLayoutX() + 200);
            statsGroup.setLayoutX(statsGroup.getLayoutX() + 5000);
            statsIsOut = true;
        }
    }

}