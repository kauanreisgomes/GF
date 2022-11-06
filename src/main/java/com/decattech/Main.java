package com.decattech;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import com.functions.dao.Query;
import com.functions.models.Objeto;

/**
 * JavaFX App
 */
public class Main extends Application {

    private static Scene scene;
    public static Objeto user;
    public static String version;
    public static Query query;
    public static String title_prog = "GESTO FINANCEIRO - GF";
    public static String icon = Main.class.getResource("icons/logo.png").toExternalForm();

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("view/GF_login"));
        stage.getIcons().add(new Image(icon));
        stage.setTitle(title_prog);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}