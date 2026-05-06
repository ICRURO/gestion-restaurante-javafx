package com.equipok;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        //Ya no se van a manejar los botones tipo MAC por ahora
        //stage.initStyle(StageStyle.UNDECORATED); 
        stage.setResizable(false);
        stage.setTitle("Sistema Gestión Restaurante");
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/equipok/logo.png")));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo: " + e.getMessage());
        }
        scene = new Scene(loadFXML("login"));
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        
        scene.setRoot(loadFXML(fxml));
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    public static void setRoot(Parent root) {
        
        scene.setRoot(root);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}