package com.marufeb.note.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Describes a new Form.
 * May contains multiple {@link Field} in order to match the end user requirements
 */
@Entity(name = "form")
@NamedQueries({
        @NamedQuery(name = "Form.byUUID", query = "SELECT f FROM form f WHERE id = ?1"),
        @NamedQuery(name = "Form.list", query = "SELECT f FROM form f")
})
@Table(name = "forms")
public class Form implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @OneToMany(targetEntity = Field.class, cascade = CascadeType.ALL)
    @JoinTable(name="form_to_fields", joinColumns = @JoinColumn(name = "form_id"), inverseJoinColumns = @JoinColumn(name = "field_id"))
    public List<Field> fields;

    @Column
    private String name;

    public Form() {
        fields = new ArrayList<>();
    }

    /**
     * Adds a field to the Form
     * @param field The field you want to be added
     */
    public void addField(Field field) {
        if (fields.size() == 0 || fields.stream().noneMatch(it->it.id.equals(field.id))) {
            field.parent = this;
            fields.add(field);
        }
        name = "unknown";
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        return fields;
    }

    @PostLoad
    void postLoad() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
    }

    /**
     * Describes a field.
     * @see FieldType
     */
    @Entity(name = "form_field")
    @Table(name = "form_fields")
    public static class Field implements Serializable{

        @Id
        @Column(name = "name", nullable = false, unique = true)
        private String id;

        @Column(name = "i")
        private int index;

        @Enumerated(EnumType.STRING)
        private FieldType type;

        @ManyToOne
        private Form parent;

        public Field() { }

        public Field(FieldType type, Form p) {
            this.type = type;
            this.parent = p;
        }

        public void setId(String id) {
            this.id = id;
        }

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

    public enum FieldType implements Serializable{
        TEXT_FIELD, CHECKBOX, NUMERIC_FIELD, EMAIL, LABEL
    }

}

