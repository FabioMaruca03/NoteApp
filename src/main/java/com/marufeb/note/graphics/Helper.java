package com.marufeb.note.graphics;

import com.marufeb.note.model.Form;
import com.marufeb.note.model.Note;
import com.marufeb.note.repository.ResourceLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A basic helper class for my GUI
 * @author fabiomaruca
 * @since January 2021
 */
public class Helper {

    private final static ResourceLoader loader = new ResourceLoader();

    /**
     * Loads a new Scene based on a given fxml file.
     * @param fileName The fxml filename ("/fxml/$FILENAME$.fxml) without extension
     * @param mid The operation to execute on the {@link Parent}
     * @return The requested {@link Scene}. NULL if no file has been found.
     */
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

    /**
     * Creates a new directory chooser in order to select a valid data folder
     * @param window The window owner
     * @return The desired list of notes
     */
    public static List<Note> open(Window window) {
        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Notes opener");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        final File file = chooser.showDialog(window);
        return loader.loadNotes(file);
    }

    public static Form selectForm() throws IOException {
        final AtomicReference<Form> reference = new AtomicReference<>();
        final FormPopup popup = new FormPopup(reference::set);

        final FXMLLoader loader = new FXMLLoader(Form.class.getResource("/fxml/form.fxml"));
        loader.setController(popup);
        final Parent load = loader.load();
        final Stage stage = new Stage();
        stage.setScene(new Scene(load));
        stage.setTitle("Creator");

        stage.showAndWait();
        return reference.get();
    }

}
