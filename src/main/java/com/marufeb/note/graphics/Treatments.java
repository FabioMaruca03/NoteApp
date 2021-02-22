package com.marufeb.note.graphics;

import com.marufeb.note.graphics.form.CustomTreatment;
import com.marufeb.note.model.Note;
import com.marufeb.note.model.Treatment;
import com.marufeb.note.repository.NoteRepo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fabiomaruca
 * @since February 2021
 */
public class Treatments implements Initializable {

    public static NoteRepo repo;
    private Note note;

    @FXML
    private VBox container;

    @FXML
    void add(ActionEvent event) {
        if (note != null) {
            final Treatment treatment = new Treatment(note.getTreatmentsNumber());
            note.addTreatment(treatment);
            final CustomTreatment e = new CustomTreatment(treatment);
            container.getChildren().add(e);
        }
        event.consume();
    }

    public void init(Note note) {
        if (note != null) {
            this.note = note;
            final Set<CustomTreatment> collect = note.getTreatments().stream().map(CustomTreatment::new).collect(Collectors.toSet());
            if (!collect.isEmpty()) {
                container.getChildren().addAll(collect);
                collect.forEach(CustomTreatment::compose);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);
    }

    public void registerGlobally(Note note) {
        container.getChildren().stream().map(it->(CustomTreatment)it).forEach(it-> it.register(note));
    }
}
