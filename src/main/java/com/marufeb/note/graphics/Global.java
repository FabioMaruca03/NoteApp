package com.marufeb.note.graphics;

import com.marufeb.note.graphics.charts.ReferredByChart;
import com.marufeb.note.graphics.form.CustomForm;
import com.marufeb.note.graphics.note.CustomNote;
import com.marufeb.note.model.Form;
import com.marufeb.note.model.Note;
import com.marufeb.note.model.exceptions.ExceptionsHandler;
import com.marufeb.note.repository.NoteRepo;
import com.marufeb.note.repository.ResourceLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    public ReferredByChart referred;

    @FXML
    private ListView<Note> notes;

    @FXML
    private CustomForm form;

    @FXML
    private TextField searchBox;

    @FXML
    private Text treatments;

    @FXML
    void about(ActionEvent event) {
        about.showAndWait();
        event.consume();
    }

    @FXML
    void close(ActionEvent event) {
        ((Stage) notes.getScene().getWindow()).close();
        event.consume();
    }

    @FXML
    void toExcel(ActionEvent event) {
        final Optional<File> selection = Helper.selection(form.getScene().getWindow(), new FileChooser.ExtensionFilter(".xls only", ".xlsx"), "unnamed.xls");
        selection.ifPresent(file -> {
            final Note note = notes.getSelectionModel().getSelectedItem();
            if (note != null) {
                Helper.exportToExcel(note, file);
            }
        });
        event.consume();
    }

    @FXML
    void toWord(ActionEvent event) {
        final Optional<File> selection = Helper.selection(form.getScene().getWindow(), new FileChooser.ExtensionFilter(".docx only", ".docx"), "unnamed.docx");
        selection.ifPresent(file -> {
            final Note note = notes.getSelectionModel().getSelectedItem();
            if (note != null) {
                Helper.exportToWord(note, file);
            }
        });
        event.consume();
    }

    @FXML
    void save(ActionEvent event) {
        if (updating) {
            final Note selectedItem = notes.getSelectionModel().getSelectedItem();
            selectedItem.setModDate(Calendar.getInstance().getTime());
            form.register(selectedItem);
            noteRepo.update(selectedItem);
        } else {
            try {
                final long new_note = notes.getItems().stream().filter(it -> it.getTitle().startsWith("new note")).count()+1;
                final Note note = form.getForm().toNote("new note" + (new_note > 1 ? (" # " + new_note) : ""));
                form.register(note);

                noteRepo.add(note);
                notes.getItems().add(note);
                form.clear();
            } catch (Exception e) {
                ExceptionsHandler.register(e);
            }
        }
        notes.getItems().sort(comparator);
        event.consume();
    }

    @FXML
    void newNote(ActionEvent event) throws IOException {
        final Form form = Helper.selectForm();
        this.form.update(form);
        notes.getSelectionModel().clearSelection();
        updating = false;
        event.consume();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        notes.setCellFactory(v -> new CustomNote(noteRepo::update));

        final AtomicReference<Note> selectedCache = new AtomicReference<>(null);
        final AtomicBoolean first = new AtomicBoolean(true);
        final Semaphore noteUpdater = new Semaphore(1);
        notes.setOnMouseClicked(e -> {
            try { // Async call
                noteUpdater.acquire(1);
            } catch (InterruptedException ignore) { }

            final Note selectedItem = notes.getSelectionModel().getSelectedItem();
            if (selectedCache.get() == null)
                selectedCache.set(selectedItem);
            else first.set(false);

            if (selectedItem != null && selectedItem.getRelatedForm() != null) {
                treatments.setText(String.valueOf(selectedItem.getTreatments()));
                if (selectedCache.get() != null) {
                    new Thread(() -> { // Async Note update
                        try {
                            noteUpdater.acquire(1);
                            final Note note = selectedCache.get();
                            if (note != null && !first.get()) {
                                final int tempHash = note.hashCode();
                                form.register(note);
                                if (note.hashCode() != tempHash) { // Detect changes
                                    note.setModDate(Calendar.getInstance().getTime());
                                    noteRepo.update(note);
                                }
                            }
                            noteUpdater.release(1);
                        } catch (InterruptedException ignore) { } finally {
                            selectedCache.set(selectedItem);
                            Platform.runLater(() -> form.init(selectedItem));
                        }
                    }).start();
                } else form.init(selectedItem);
                updating = true;
            } else selectedCache.set(null);

            noteUpdater.release(1);
            e.consume();
        });

        notes.setOnKeyTyped(e -> {
            if (e.isShiftDown()) {
                final Note item = selectedCache.get();
                if (item != null) {
                    notes.getItems().remove(item);
                    noteRepo.remove(item);
                    form.clear();
                }
            }
        });

        final AtomicReference<List<Note>> cache = new AtomicReference<>(noteRepo.getAll());
        searchBox.textProperty().addListener((ob, o, n) -> {
            if (n.isBlank()) {
                notes.getItems().clear();
                cache.get().forEach(it->notes.getItems().add(it));
            } else {
                notes.getItems().clear();
                cache.get().stream().filter(it -> {
                    final List<Note.Content> contents = it.getContent();
                    final Optional<Note.Content> name = contents.stream().filter(content -> content.getName().toLowerCase().equals("name")).findFirst();
                    final Optional<Note.Content> work = contents.stream().filter(content -> content.getName().toLowerCase().equals("work")).findFirst();
                    if (name.isPresent()) {
                        if (!name.get().getValue().isBlank()) {
                            return name.get().getValue().contains(searchBox.getText());
                        }
                    }
                    if (work.isPresent()) {
                        if (!work.get().getValue().isBlank()) {
                            return work.get().getValue().contains(searchBox.getText());
                        }
                    }
                    return false;
                }).forEach(note -> notes.getItems().add(note));
            }
        });
        searchBox.setOnAction(e -> {
            cache.set(noteRepo.getAll());
            e.consume();
        }) ;
        cache.get().forEach(it->notes.getItems().add(it));

        ReferredByChart.repo = noteRepo;
        referred.setData(ReferredByChart.update());
    }
}
