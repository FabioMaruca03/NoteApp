package com.marufeb.note.graphics;

import com.marufeb.note.graphics.form.CustomForm;
import com.marufeb.note.graphics.note.CustomNote;
import com.marufeb.note.model.Form;
import com.marufeb.note.model.Note;
import com.marufeb.note.repository.NoteRepo;
import com.marufeb.note.repository.ResourceLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * That's the main controller. It operates with the global.fxml
 * @author fabiomaruca
 * @since January 2021
 */
public class Global implements Initializable {

    private final About about = new About();
    private final NoteRepo noteRepo = new NoteRepo();
    private boolean updating = false;
    private final Comparator<Note> comparator = (o1, o2) -> {
        if (o1.isModified() && o2.isModified())
            return o1.getModDate().after(o2.getModDate())?0:1;
        else if (o1.isModified())
            return o1.getModDate().after(o2.getCreation())?0:1;
        else if (o2.isModified())
            return o1.getCreation().after(o2.getModDate())?0:1;
        else return o1.getCreation().after(o2.getCreation())?0:1;
    };

    @FXML
    private ListView<Note> notes;

    @FXML
    private CustomForm form;

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
        if (updating) {
            final Note selectedItem = notes.getSelectionModel().getSelectedItem();
            form.register(selectedItem);
            noteRepo.update(selectedItem);
        } else {
            final long new_note = notes.getItems().stream().filter(it -> it.getTitle().startsWith("new note")).count()+1;
            final Note note = form.getForm().toNote("new note" + (new_note > 1 ? (" # " + new_note) : ""));
            form.register(note);

            noteRepo.add(note);
            notes.getItems().add(note);
            form.clear();
        }
        notes.getItems().sort(comparator);
        event.consume();
    }

    @FXML
    void newNote(ActionEvent event) throws IOException {
        final Form form = Helper.selectForm();
        this.form.update(form);
        event.consume();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        notes.setCellFactory(v -> new CustomNote(noteRepo::update));
        notes.setOnMouseClicked(e -> {
            final Note selectedItem = notes.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getRelatedForm() != null) {
                form.update(selectedItem.getRelatedForm());
                form.init(selectedItem);
            }
            e.consume();
        });
        notes.setOnKeyTyped(e -> {
            if (e.isShiftDown()) {
                final Note item = notes.getSelectionModel().getSelectedItem();
                if (item != null) {
                    notes.getItems().remove(item);
                    noteRepo.remove(item);
                }
            }
        });
        noteRepo.getAll().forEach(it->notes.getItems().add(it));
    }
}
