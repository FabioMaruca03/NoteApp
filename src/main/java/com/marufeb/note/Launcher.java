package com.marufeb.note;

import com.marufeb.note.model.exceptions.ExceptionsHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.marufeb.note.graphics.Helper.*;

/**
 * @author fabiomaruca
 * @since January 2021
 */
public class Launcher extends Application {

    public static void main(String[] args) {
        ExceptionsHandler.onRegister(Throwable::printStackTrace);
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final Scene s = loadFXML("global", p-> System.out.println("[I] - Loaded global pane"));
        stage.setTitle("Notes - A great solution");
        stage.setScene(s);
    }

}
