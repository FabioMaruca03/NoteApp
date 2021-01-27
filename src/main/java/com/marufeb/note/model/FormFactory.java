package com.marufeb.note.model;

/**
 * Helps you creating a {@link Form}
 */
public class FormFactory {
    // todo: finish the factory
    private Form form;

    /**
     * Creates a new FormFactory.
     * Initializes a new Form
     */
    public FormFactory() {
        form = new Form();
    }

    /**
     * Adds a new {@link com.marufeb.note.model.Form.Field}
     * @param name The field name
     * @param type The field type
     * @param index The field index. If the chosen index is not empty it will put
     *              the field in the next available spot
     * @return The factory (chaining)
     */
    public FormFactory addField(String name, Form.FieldType type, int index) {
        final Form.Field field = new Form.Field(type, form);
        if (form.fields.stream().anyMatch(it->it.getIndex() == index))
            field.setIndex(form.fields.stream().map(Form.Field::getIndex).max(Integer::compareTo).orElse(0)+1);
        else field.setIndex(index);
        field.setId(name);
        form.addField(field);
        return this;
    }

    /**
     * Other configurations
     * @return The factory (chaining)
     */
    public FormFactory configure() {
        return this;
    }

    /**
     * Builds the Form. <b>Single use</b>
     * After after building the form it will be initializing a new Form
     * @return The configured {@link Form}
     */
    public Form build() {
        final Form form = this.form;
        this.form = new Form();
        return form;
    }

}
