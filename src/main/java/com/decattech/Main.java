package com.decattech;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import com.functions.models.Objeto;

/**
 * JavaFX App
 */
public class Main extends Application {

    private static Scene scene;
    public static Objeto user;
    public static String version;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("view/GF_login"));
        stage.getIcons().add(new Image(Main.class.getResource("icons/logo.png").toExternalForm()));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}