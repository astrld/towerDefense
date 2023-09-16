package com.example.battlefortress;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static Stage stage;
    public static Scene scene;
    public static double cursorX;
    public static double cursorY;

    public static Image woodCursor = new Image("file:src/main/Images/Misc/woodCursor.png");
    public static Image hammerCursor = new Image("file:src/main/Images/Misc/hammerCursor.png");

    public static void changeWindowSize(double width, double height) {
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("startController.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        this.stage = stage;
        this.scene = scene;
        scene.setOnMouseMoved(e -> {
            cursorX = e.getSceneX();
            cursorY = e.getSceneY();
        });
        scene.setCursor(new ImageCursor(woodCursor));
    }

    public static void changeScene( ){

    }

    public static void buildCursor(boolean b){
        if(b){
            scene.setCursor(new ImageCursor(hammerCursor));
        } else {
            scene.setCursor(new ImageCursor(woodCursor));
        }
    }
}