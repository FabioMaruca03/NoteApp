package com.marufeb.note.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Describes a new Form.
 * A may contains multiple {@link Field} in order to match the end user requirements
 */
@Entity(name = "form")
@NamedQuery(name = "Form.byUUID", query = "FROM form WHERE id = ?")
@NamedQuery(name = "Form.list", query = "FROM form ")
@Table(name = "form")
public class Form implements Serializable {

    @OneToMany(targetEntity = Field.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Column(name = "field")
    public List<Field> fields;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    public Form() { }

    /**
     * Adds a field to the Form
     * @param field The field you want to be added
     */
    public void addField(Field field) {
        if (fields.stream().noneMatch(it->it.id.equals(field.id))) {
            field.parent = this;
            fields.add(field);
        }
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    /**
     * Describes a field.
     * @see FieldType
     */
    @Entity(name = "form_field")
    @NamedQuery(name = "Note.byName", query = "FROM note WHERE id = ?")
    @Table(name = "form_fields")
    public static class Field implements Serializable{

        @Column(name = "name", unique = true)
        private String id;

        @Column(name = "index")
        private int index;

        @Enumerated(EnumType.STRING)
        private FieldType type;

        @ManyToOne(targetEntity = Form.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @Column(name = "parent")
        private Form parent;

        public Field(FieldType type, Form parent) {
            this.type = type;
            this.parent = parent;
        }

        public Field() { }

        public void setId(String id) {
            this.id = id;
        }

        @Id
        public String getId() {
            return id;
        }

        public FieldType getType() {
            return type;
        }

        public void setType(FieldType type) {
            this.type = type;
        }

        public Form getParent() {
            return parent;
        }

        public void setParent(Form parent) {
            this.parent = parent;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    /**
     * Defines field types for an hypothetical GUI API
     */
    public enum FieldType {
        TEXT_FIELD, CHECKBOX, NUMERIC_FIELD, EMAIL, LABEL
    }

}

