package com.marufeb.note.graphics.form;

import com.marufeb.note.model.Form;
import com.marufeb.note.model.FormFactory;
import com.marufeb.note.model.Note;
import com.marufeb.note.model.Treatment;
import javafx.application.Platform;

/**
 * @author fabiomaruca
 * @since February 2021
 */
public class CustomTreatment extends CustomForm {
    private final Treatment treatment;

    private static Form init() {
        final FormFactory formFactory = new FormFactory();
        formFactory.addField("Date", Form.FieldType.TEXT_FIELD, 0);
        formFactory.addField("Evaluation", Form.FieldType.TEXT_FIELD, 1);
        formFactory.addField("Comment", Form.FieldType.TEXT_FIELD, 2);
        formFactory.addField("TTT", Form.FieldType.TEXT_FIELD, 3);
        return formFactory.build();
    }

    public CustomTreatment(Treatment treatment) {
        super(init());

        this.treatment = treatment;
        this.setMinHeight(150);
    }

    @Override
    public void register(Note note) {
        treatment.setReference(note);
        treatment.setDate(components.get("Date").getText());
        treatment.setEvaluation(components.get("Evaluation").getText());
        treatment.setCommentary(components.get("Comment").getText());
        treatment.setTTT(components.get("TTT").getText());
        if (!note.getTreatments().contains(treatment)) {
            note.addTreatment(treatment);
        }
    }

    /**
     * Initializes the form in order to match the already set {@link Treatment}
     */
    public void compose() {
        Platform.runLater(() -> {
            components.get("Date").setText(treatment.getDate());
            components.get("Evaluation").setText(treatment.getEvaluation());
            components.get("Comment").setText(treatment.getCommentary());
            components.get("TTT").setText(treatment.getTTT());
        });
    }
}
