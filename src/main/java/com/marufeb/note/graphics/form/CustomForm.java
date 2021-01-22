package com.marufeb.note.graphics.form;

import com.marufeb.note.model.Form;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents graphically the Form itself.
 * <pre>
 * This is a wrapper class, takes each field inside a {@link Form}
 * and then represents it inside a {@link BorderPane}. Each {@link com.marufeb.note.model.Form.Field}
 * is determined by its {@link com.marufeb.note.model.Form.FieldType} attribute.</pre>
 * @author fabiomaruca
 * @since January 2021
 */
public class CustomForm extends ListView<Form> {
    private Form form;

    /**
     * Creates and sets the Right node and the Left node. Then constructs the whole {@link Form}
     * @param form The form to use
     */
    public CustomForm(Form form) {
        this.form = form;
        setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Form item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    this.getChildren().clear();
                    this.getChildren().addAll(construct());
                }
            }
        });
        construct();
    }

    private List<BorderPane> construct() {
        final List<BorderPane> nodes = new ArrayList<>();

        form.fields.forEach(field -> {
            BorderPane temp = nodes.get(field.getIndex());
            if (temp == null) {
                nodes.set(field.getIndex(), new BorderPane());
                temp = nodes.get(field.getIndex());
                temp.setMaxHeight(60);
            }
            switch (field.getType()) {
                case TEXT_FIELD: {
                    final TextField component = new TextField();
                    component.setPromptText("Text");
                    temp.setRight(component);
                    break;
                }
                case CHECKBOX: {
                    final CheckBox checkBox = new CheckBox();
                    temp.setRight(checkBox);
                    break;
                }
                case EMAIL: {
                    final TextField component = new TextField();
                    component.setPromptText("Email @");
                    component.textProperty().addListener((ob, o, n)->{
                        if (!n.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
                            component.setText(o);
                    });
                    temp.setRight(component);
                    break;
                }
                case NUMERIC_FIELD: {
                    final TextField component = new TextField();
                    component.setPromptText("Numbers only [0-9]");
                    component.textProperty().addListener((ob, o, n)->{
                        if (!n.chars().allMatch(Character::isDigit))
                            component.setText(o);
                    });
                    temp.setRight(component);
                    break;
                }
                case LABEL: {
                    final Label label = new Label();
                    final TextField name = new TextField();
                    name.setPromptText("Property name");
                    BorderPane finalTemp = temp;
                    label.setOnMouseClicked(e->{
                        name.setText(label.getText());
                        finalTemp.setLeft(name);
                        e.consume();
                    });
                    label.setOnMouseExited(e->{
                        label.setText(name.getText());
                        name.clear();
                        finalTemp.setLeft(label);
                        e.consume();
                    });
                    label.setText("PropertyName");
                    temp.setLeft(label);
                }
            }
        });

        return nodes;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }
}
