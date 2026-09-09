package com.strangerthings;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX application (extends Application). Prefer running {@link Launcher}
 * from IntelliJ so JavaFX is found on the classpath.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/com/strangerthings/login.fxml"));
        Parent root = loader.load();
        stage.setTitle("Stranger Things");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
