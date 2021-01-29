package com.marufeb.note.graphics;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Shows an About popup which tells the end user about me
 * @author fabiomaruca
 * @since January 2021
 */
public class About extends Stage {

    public About() {
        super();
        final BorderPane root = new BorderPane();
        root.setCenter(new Text(getBody()));

        final HBox footer = new HBox();
        footer.setAlignment(Pos.BASELINE_RIGHT);
        footer.setOpacity(0.7);
        footer.getChildren().add(new Label("Powered by Fabio Maruca"));
        HBox.setMargin(footer, new Insets(10));
        root.setBottom(footer);

        final Scene value = new Scene(root);
        setScene(value);
        initModality(Modality.APPLICATION_MODAL);

        setMinWidth(300);
        setMinHeight(200);
    }

    public String getBody() {
        return "Created by Fabio Maruca\nEmail: Fabio.Maruca03@gmail.com";
    }

}
