package com.marufeb.note;

import com.marufeb.note.model.exceptions.ExceptionsHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.NoResultException;

import static com.marufeb.note.graphics.Helper.*;

/**
 * Launches the application itself.
 * Launches Graphics APi and Repository API
 * @author fabiomaruca
 * @since January 2021
 */
public class Launcher extends Application {

    /**
     * Initializes the {@link ExceptionsHandler} and launches the Graphics API
     * @param args You know
     */
    public static void main(String[] args) {
        ExceptionsHandler.onRegister(Throwable::printStackTrace);
        ExceptionsHandler.onSpecificRegister(e->{ /* todo: define */ }, NoResultException.class);
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final Scene s = loadFXML("global", p-> System.out.println("[I] - Loaded global pane"));
        stage.setTitle("Notes - A great solution");
        stage.setScene(s);
        stage.show();
    }

}
