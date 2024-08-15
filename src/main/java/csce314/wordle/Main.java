package csce314.wordle;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        System.out.println("Creating fxmlLoader + root");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("UI.fxml"));
        Parent root = fxmlLoader.load();
        System.out.println(fxmlLoader);

        System.out.println("Creating scene.");
        Scene scene = new Scene(root, 804, 904);

        System.out.println("Creating controller.");
        Controller controller = fxmlLoader.getController();
        System.out.println(controller);


        System.out.println("Setting up stage title and scene.");
        stage.setTitle("Wordle");
        stage.setScene(scene);
        controller.setScene(scene);

        System.out.println("Showing the stage.");
        stage.show();

        System.out.println("Initializing controller.");
        controller.initialize();

        controller.initializeRound();
        System.out.println("Time to play the game.");

    }

    public static void main(String[] args) {
        System.out.println("Launching...");
        launch();
        System.out.println("Terminated.");
    }
}