package com.marufeb.note.graphics;

import com.marufeb.note.graphics.form.CustomForm;
import com.marufeb.note.model.Form;
import com.marufeb.note.model.FormFactory;
import com.marufeb.note.repository.FormRepo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A popup which will allow the end user to create and manage created forms.
 * It operates with form.fxml
 * @author fabiomaruca
 * @since January 2021
 */
public class FormPopup implements Initializable {

    private FormRepo repo;
    private final Consumer<Form> consumer;
    private final FormFactory formFactory = new FormFactory();
    private boolean creating = false;

    public FormPopup(Consumer<Form> formConsumer) {
        consumer = formConsumer;
    }

    @FXML
    private ChoiceBox<Form> available;

    @FXML
    private CustomForm build;

    @FXML
    private TextField formName;

    @FXML
    private TextField layerName;

    @FXML
    private ChoiceBox<Form.FieldType> components;

    @FXML
    void addLayer(ActionEvent event) {
        if (!layerName.getText().isBlank() && components.getSelectionModel().getSelectedItem() != null) {
            if (formFactory.getForm().getFields().stream().noneMatch(it->it.getName().equals(layerName.getText()))) {
                formFactory.addField(layerName.getText(), components.getSelectionModel().getSelectedItem(), build.getItems().size());
                build.getItems().add(formFactory.getLastAdded());
                build.getFields().values().forEach(it-> it.setEditable(false));
            }
        }
        event.consume();
    }

    @FXML
    void close(ActionEvent event) {
        hide(false);
        event.consume();
    }

    @FXML
    private void next(ActionEvent event) {
        final Form selectedItem = available.getSelectionModel().getSelectedItem();
        if (selectedItem!=null && !creating) {
            consumer.accept(selectedItem); // Then updates the CustomForm
            ((Stage) available.getScene().getWindow()).close();
        } else {
            hide(true);
            creating = false;
        }
        event.consume();
    }

    @FXML
    private void newForm(ActionEvent event) {
        creating = true;
        next(event);
    }

    /**
     * Change the current visible pane on the {@link javafx.scene.layout.StackPane}
     * @param hide <table>
     *             <th>HIDE</th>
     *             <tr><td>TRUE - when you want to see the Form creator</td></tr>
     *             <tr><td>FALSE - when you want to load an existing form</td></tr>
     *             </table>
     */
    private void hide(boolean hide) {
        if (hide)
            available.getParent().toBack();
        else available.getParent().toFront();
    }

    @FXML
    void save(ActionEvent event) {
        if (!formName.getText().isBlank()) {
            final Form build = formFactory.build();
            final long count = available.getItems().stream().filter(it -> it.getName().equals(formName.getText())).count()+1;
            build.setName(count==1?formName.getText():(formName.getText()+"#"+count));
            repo.getAll().forEach(it-> System.out.println(it.getName()));
            repo.add(build);
            updateAvailable();

            hide(false);
        }
        event.consume();
    }

    private synchronized void updateAvailable() {
        final List<Form> forms = repo.getAll();
        if (forms.size() != 0) {
            available.setItems(FXCollections.observableArrayList(forms));
            available.getSelectionModel().select(0);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        available.setConverter(new StringConverter<>() {
            @Override
            public String toString(Form object) {
                return object.getName();
            }

            @Override
            public Form fromString(String string) {
                return null;
            }
        });
        components.setConverter(new StringConverter<>() {
            @Override
            public String toString(Form.FieldType object) {
                return object.name();
            }

            @Override
            public Form.FieldType fromString(String string) {
                return Form.FieldType.valueOf(string);
            }
        });

        build.setOnKeyTyped(e -> {
            if (e.isShiftDown()) {
                final Form.Field selectedItem = build.getSelectionModel().getSelectedItem();
                final Form form = formFactory.getForm();
                if (selectedItem != null && form != null) {
                    form.getFields().remove(selectedItem);
                    build.getItems().remove(selectedItem);
                }
            }
        });

        new Thread(()-> {
            repo = new FormRepo();
            Platform.runLater(() -> {
                updateAvailable();
                if (available.getItems().size() > 0)
                    available.getSelectionModel().select(0);
            });
        }).start();

        components.setItems(FXCollections.observableArrayList(Arrays.stream(Form.FieldType.values()).filter(it->!it.equals(Form.FieldType.CHECKBOX)&&!it.equals(Form.FieldType.LABEL)).collect(Collectors.toList())));

        hide(false);
    }

}
