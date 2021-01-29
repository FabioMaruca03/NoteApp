package com.marufeb.note.graphics.note;

import com.marufeb.note.model.Note;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;


/**
 * Represents a custom note list cell
 * @author fabiomaruca
 * @since January 2021
 */
public class CustomNote extends ListCell<Note> {

    private final BorderPane pane = new BorderPane();
    private final Label center = new Label();

    public CustomNote() {
        pane.setCenter(center);
        getChildren().add(pane);
    }

    @Override
    protected void updateItem(Note item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            String title = item.getTitle()+" at ";

            if (item.isModified()) {
                title += item.getModDate().toString();
            } else title += item.getCreation().toString();

            center.setText(title);
        }
        getListView().getItems().sort((o1, o2) -> o1.isModified()?(o1.getModDate().after(o2.isModified()?o2.getModDate():o2.getCreation())?1:0):o1.getCreation().after(o2.isModified()?o2.getModDate():o2.getCreation())?1:0);
    }
}
