package com.marufeb.note.graphics.form;

import com.marufeb.note.model.Form;
import com.marufeb.note.model.Note;
import com.marufeb.note.model.exceptions.ExceptionsHandler;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

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
    private boolean clear = true;
    private Semaphore semaphore;
    private int permits;

    /**
     * Updates the current status to the given Form. Then constructs the whole {@link Form}
     * @param form The form to use
     */
    @SuppressWarnings("unused")
    public CustomForm(Form form) {
        this();
        update(form);
    }

    /**
     * Initializes the cell factory
     */
    public CustomForm() {
        setCellFactory(cell -> new ListCell<Form.Field>() {
            @Override
            protected void updateItem(Form.Field item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    setGraphic(construct(item));
                } else setGraphic(null);
            }
        });
    }

    /**
     * Updates the current Form
     * @param form The form you want to use
     */
    public synchronized void update(Form form) {
//        if (semaphore == null || permits != form.fields.size()-1) {
            permits = form.fields.size() - 1;
            this.semaphore = new Semaphore(permits);
//        }
        if (clear) {
            clear();
            this.form = form;
        } else getItems().clear();
        clear = true;
        try {
            semaphore.acquire(permits);
            form.fields.forEach(it->getItems().add(it));
        } catch (InterruptedException e) {
            ExceptionsHandler.register(e);
        }
    }

    /**
     * Constructs cells in order to match the Field encoding. CHECKBOX OPTION's  DISABLED
     * @param field The field you want to add
     * @return The resulting {@link BorderPane}
     */
    private synchronized BorderPane construct(Form.Field field) {
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
            case EMAIL: {
                final TextField component = new TextField();
                component.setPromptText("Email @");
                component.setOnKeyTyped( e-> {
                    if (e.getCode() == KeyCode.ENTER) {
                        if (!e.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                            component.setText("Invalid email ");
                        }
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
                    if (!n.chars().allMatch(Character::isDigit)) {
                        component.setText(o);
                    }
                });
                temp.setRight(component);
                f = component;
                break;
            }
//            case CHECKBOX: {
//                final CheckBox checkBox = new CheckBox();
//                temp.setRight(checkBox);
//                break;
//            }

//            case LABEL: {
//                final Label label = createLabel(temp);
//                label.setText("PropertyName");
//                temp.setRight(label);
//            }
            default:
                f = null;
        }

        final Label label = createLabel(temp);
        label.setText(field.getName());
        temp.setLeft(label);
        if (f != null)
            components.put(field.getName(), f);

        if (semaphore != null && semaphore.availablePermits() != permits) {
            semaphore.release(1);
        }
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
//        label.setOnMouseClicked(e->{       // DISABLED
//            if (e.getClickCount() == 2) {
//                name.setText(label.getText());
//                temp.setLeft(name);
//            }
//            e.consume();
//        });
//        label.setOnKeyTyped(e->{
//            if (e.getCode() == KeyCode.ENTER) {
//                label.setText(name.getText());
//                name.clear();
//            }
//            e.consume();
//        });
        return label;
    }

    /**
     * Initializes each field in order to match an existing note
     * @param note The note you want to utilize
     */
    public synchronized void init(Note note) {
        clear = false;
        update(note.getRelatedForm());

        new Thread(() -> {
            try {
                this.semaphore.acquire(permits);
                Thread.sleep(30);
                note.getContent().forEach(content -> {
                    Platform.runLater(() -> {
                        try {
                            components.get(content.getName()).setText(content.getValue());
                        } catch (Exception e) {
                            ExceptionsHandler.register(e);
                        } finally {
                            this.semaphore.release(permits);
                        }
                    });
                });
            } catch (InterruptedException e) {
                ExceptionsHandler.register(e);
            }
        }).start();

    }

    /**
     * Updates a {@link Note} based on what is typed inside each Form Field
     * @param note The note you want to update
     */
    public void register(Note note) {
        components.forEach((key, value) -> {
            try {
                note.getContent(key).ifPresentOrElse(content -> content.setValue(value.getText().isBlank() ? "[-]" : value.getText()), () -> note.addContent(key, value.getText()));
            } catch (NullPointerException ignore) {
                note.addContent(key, value.getText());
            }
        });
    }

    /**
     * @return A map which contains all typed components inside it
     */
    @SuppressWarnings("unused")
    public Map<String, String> getComponents() {
        final Map<String, String> result = new HashMap<>();
        components.forEach((key, value) -> {
            final String text = value.getText();
            result.put(key, text.isBlank() ? "[-]" : text);
        });
        return result;
    }

    public Map<String, TextField> getFields() {
        return components;
    }

    /**
     * Internal Form getter
     * @return The internal {@link Form}
     */
    public Form getForm() {
        return form;
    }

    public synchronized void clear() {
        form = null;
        getItems().clear();
        components.clear();
    }
}
