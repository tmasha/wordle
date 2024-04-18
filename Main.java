package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
  public static void main(String[] args) { launch(args); }
  
  @Override
  public void start(Stage primaryStage) {
    Label label = new Label("Wordoli");
    Scene scene = new Scene(label, 300, 200);
    primaryStage.setScene(scene);
    primaryStage.setTitle("wordle");
    primaryStage.show();
  }
    
}
