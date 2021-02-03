package com.marufeb.note.graphics.form;

import com.marufeb.note.model.Form;

/**
 * @author fabiomaruca
 * @since January 2021
 */
public class InvalidFormException extends RuntimeException{
    public InvalidFormException(Form form) {
        super("Unable to initialize the note: The form " + form.getName() + " is not linked with this note!");
    }
}
