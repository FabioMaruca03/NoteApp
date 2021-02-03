package com.marufeb.note.graphics.note;

import com.marufeb.note.model.Note;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.text.SimpleDateFormat;
import java.util.function.Consumer;


/**
 * Represents a custom note list cell
 * @author fabiomaruca
 * @since January 2021
 */
public class CustomNote extends ListCell<Note> {

    private final BorderPane pane = new BorderPane();
    private final Label center = new Label();
    private final TextField field = new TextField();
//    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm dd-MM-yyyy");
    private Note note;

    public CustomNote(Consumer<Note> onChange) {
        pane.setCenter(center);
        pane.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                final String text = center.getText();
                field.setText(text.length()>0?text.split(" at ")[0]:"");
                pane.setCenter(field);
                e.consume();
            }
        });
        pane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                final String text = field.getText();
                center.setText(!text.isEmpty()?text:center.getText());
                note.setTitle(center.getText());
                if (!text.isBlank() && note != null) {
                    onChange.accept(note);
                    updateItem(note, false);
                } else if (text.isBlank() && note!=null && center.getText().isBlank())
                    updateItem(note, true);
                pane.setCenter(center);
                field.clear();
            }
            e.consume();
        });
    }

    @Override
    protected void updateItem(Note item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
//            String title = item.getTitle()+" at ";
            String title = item.getTitle();

//            if (item.isModified()) {
//                title += format.format(item.getModDate());
//            } else title += format.format(item.getCreation());

            note = item;
            center.setText(title);
            setGraphic(pane);
        } else {
            setGraphic(null);
            note = null;
        }
    }


}
