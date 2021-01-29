package com.marufeb.note.graphics.form;

import com.marufeb.note.model.Form;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

/**
 * Represents graphically the Form itself.
 * <pre>
 * This is a wrapper class, takes each field inside a {@link Form}
 * and then represents it inside a {@link BorderPane}. Each {@link com.marufeb.note.model.Form.Field}
 * is determined by its {@link com.marufeb.note.model.Form.FieldType} attribute.</pre>
 * @author fabiomaruca
 * @since January 2021
 */
public class CustomForm extends ListView<Form.Field> {
    private Form form;

    /**
     * Updates the current status to the given Form. Then constructs the whole {@link Form}
     * @param form The form to use
     */
    public CustomForm(Form form) {
        this();
        update(form);
    }

    /**
     * Initializes the cell factory
     */
    public CustomForm() {
        setCellFactory(cell -> new ListCell<>() {
            @Override
            protected void updateItem(Form.Field item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setGraphic(construct(item));
                } else setGraphic(null);
            }
        });
    }

    /**
     * Updates the current Form
     * @param form The form you want to use
     */
    public void update(Form form) {
        this.form = form;
        getItems().clear();
        getItems().addAll(form.fields);
    }

    /**
     * Constructs cells in order to match the Field encoding
     * @param field The field you want to add
     * @return The resulting {@link BorderPane}
     */
    private BorderPane construct(Form.Field field) {
        BorderPane temp = new BorderPane();
        temp.setMaxHeight(60);
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
                component.setOnKeyTyped( e-> {
                    if (e.getCode().equals(KeyCode.ENTER)) {
                        if (!e.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
                            component.setText("Invalid email ");
                    }
                    e.consume();
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
                final Label label = createLabel(temp);
                label.setText("PropertyName");
                temp.setRight(label);
            }
        }

        final Label label = createLabel(temp);
        label.setText(field.getName());
        temp.setLeft(label);

        return temp;
    }

    /**
     * Creates a {@link Label} inside a {@link BorderPane}
     * @param temp
     * @return
     */
    private Label createLabel(BorderPane temp) {
        final Label label = new Label();
        final TextField name = new TextField();
        name.setPromptText("Property name");
        label.setOnMouseClicked(e->{
            name.setText(label.getText());
            temp.setLeft(name);
            e.consume();
        });
        label.setOnKeyTyped(e->{
            if (e.getCode().equals(KeyCode.ENTER)) {
                label.setText(name.getText());
                name.clear();
            }
            e.consume();
        });
        return label;
    }

    public Form getForm() {
        return form;
    }
 
}
