package com.marufeb.note.graphics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * That's the main controller. It operates with the global.fxml
 */
public class Global implements Initializable {

    @FXML
    private ListView<?> notes;

    @FXML
    private ListView<?> form;

    @FXML
    void about(ActionEvent event) {

    }

    @FXML
    void clearNotes(ActionEvent event) {

    }

    @FXML
    void close(ActionEvent event) {

    }

    @FXML
    void newNote(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
