package com.marufeb.note.graphics.form;

import com.marufeb.note.model.Form;
import com.marufeb.note.model.Note;
import com.marufeb.note.model.exceptions.ExceptionsHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.util.HashMap;
import java.util.Map;

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
    private final Map<String, TextField> components = new HashMap<>();

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
        form.fields.forEach(it->getItems().add(it));
    }

    /**
     * Constructs cells in order to match the Field encoding. CHECKBOX OPTION's  DISABLED
     * @param field The field you want to add
     * @return The resulting {@link BorderPane}
     */
    private BorderPane construct(Form.Field field) {
        final BorderPane temp = new BorderPane();
        final TextField f;
        temp.setMaxHeight(60);
        switch (field.getType()) {
            case TEXT_FIELD: {
                final TextField component = new TextField();
                component.setPromptText("Text");
                temp.setRight(component);
                f = component;
                break;
            }
//            case CHECKBOX: {
//                final CheckBox checkBox = new CheckBox();
//                temp.setRight(checkBox);
//                break;
//            }
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
                f = component;
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
                f = component;
                break;
            }
            case LABEL: {
                final Label label = createLabel(temp);
                label.setText("PropertyName");
                temp.setRight(label);
            }
            default:
                f = null;
        }

        final Label label = createLabel(temp);
        label.setText(field.getName());
        temp.setLeft(label);

        if (f != null)
            components.putIfAbsent(field.getName(), f);
        return temp;
    }

    /**
     * Creates a {@link Label} inside a {@link BorderPane}
     * @param temp The temp variable which represents the Label container
     * @return The created Label
     */
    private Label createLabel(BorderPane temp) {
        final Label label = new Label();
        final TextField name = new TextField();
        name.setPromptText("Property name");
        label.setOnMouseClicked(e->{
            if (e.getClickCount() == 2) {
                name.setText(label.getText());
                temp.setLeft(name);
            }
            e.consume();
        });
        label.setOnKeyTyped(e->{
            if (e.getCode() == KeyCode.ENTER) {
                label.setText(name.getText());
                name.clear();
            }
            e.consume();
        });
        return label;
    }

    /**
     * Initializes each field in order to match an existing note
     * @param note The note you want to utilize
     */
    public void init(Note note) {
        if (note.getRelatedForm().getName().equals(form.getName())) {
            note.getContent().forEach(content -> components.get(content.getName()).setText(content.getValue()));
        } else ExceptionsHandler.register(new InvalidFormException(form));
    }

    /**
     * Updates a {@link Note} based on what is typed inside each Form Field
     * @param note The note you want to update
     */
    public void register(Note note) {
        components.forEach((key, value) -> {
            try {
                note.getContent(key).ifPresentOrElse(content -> content.setValue(value.getText().isBlank() ? "?" : value.getText()), () -> note.addContent(key, value.getText()));
            } catch (NullPointerException ignore) {
                note.addContent(key, value.getText());
            }
        });
    }

    /**
     * @return A map which contains all typed components inside it
     */
    public Map<String, String> getComponents() {
        final Map<String, String> result = new HashMap<>();
        components.forEach((key, value) -> {
            final String text = value.getText();
            result.put(key, text.isBlank() ? "?" : text);

        });
        return result;
    }

    /**
     * Internal Form getter
     * @return The internal {@link Form}
     */
    public Form getForm() {
        return form;
    }

    public void clear() {
        form = null;
        getItems().clear();
        components.clear();
    }
}
