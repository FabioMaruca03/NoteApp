package com.marufeb.note.graphics;

import com.marufeb.note.graphics.form.CustomForm;
import com.marufeb.note.graphics.note.CustomNote;
import com.marufeb.note.model.Form;
import com.marufeb.note.model.Note;
import com.marufeb.note.repository.ResourceLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * That's the main controller. It operates with the global.fxml
 * @author fabiomaruca
 * @since January 2021
 */
public class Global implements Initializable {

    private final About about = new About();

    @FXML
    private ListView<Note> notes;

    @FXML
    private ListView<Form.Field> form;

    @FXML
    void about(ActionEvent event) {
        about.showAndWait();
        event.consume();
    }

    @FXML
    void clearNotes(ActionEvent event) {
        notes.getItems().clear();
        event.consume();
    }

    @FXML
    void open(ActionEvent event) {
        Helper.open(notes.getScene().getWindow());
        event.consume();
    }


    @FXML
    void close(ActionEvent event) {
        ((Stage) notes.getScene().getWindow()).close();
        event.consume();
    }

    @FXML
    void toExcel(ActionEvent event) {
        event.consume();
    }

    @FXML
    void toWord(ActionEvent event) {
        event.consume();
    }

    @FXML
    void save(ActionEvent event) {

        event.consume();
    }

    @FXML
    void newNote(ActionEvent event) throws IOException {
        ((CustomForm) form).update(Helper.selectForm());
        event.consume();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        form = new CustomForm(new Form());
        notes.setCellFactory(v -> new CustomNote());
    }
}
