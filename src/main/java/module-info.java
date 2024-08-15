module csce314.wordle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens csce314.wordle to javafx.fxml;
    exports csce314.wordle;
}