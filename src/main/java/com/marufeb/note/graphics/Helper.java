package com.marufeb.note.graphics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * A basic helper class for my GUI
 * @author fabiomaruca
 * @since January 2021
 */
public class Helper {
    public static Scene loadFXML(String fileName, Consumer<Parent> mid) {
        final FXMLLoader loader = new FXMLLoader(Helper.class.getResource("/fxml/"+fileName+".fxml"));
        try {
            final Parent load = loader.load();
            mid.accept(load);
            return new Scene(load);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
