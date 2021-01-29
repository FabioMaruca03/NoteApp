package com.marufeb.note.model;

import com.marufeb.note.model.exceptions.InvalidContentException;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Describes a note which is basically a collection of contents
 * @author fabiomaruca
 * @since January 2021
 */
@Entity(name = "note")
@NamedQueries({
        @NamedQuery(name = "Note.byUUID", query = "SELECT n FROM note n WHERE id = ?1"),
        @NamedQuery(name = "Note.list", query = "SELECT n FROM note n")
})
@Table(name = "notes")
public class Note implements Serializable {

    @Column(name = "note_id")
    @GeneratedValue
    @Id
    private long id;

    @Column(name = "note_modified")
    private Date modDate;

    @OneToOne
    private Form relatedForm = new Form();

    @Transient
    private boolean modified = false;

    @Column(name = "note_creation")
    private Date creation;

    @Column(name = "note_title")
    private String title;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "content", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "content_id"))
    private List<Content> content;

    /**
     * Empty constructor - used by HIBERNATE -
     */
    public Note() { }

    /**
     * Creates a new note with a title and a creation date
     * @param creation The creation date
     * @param title The title of the note
     */
    public Note(Date creation, String title) {
        this.creation = creation;
        this.title = title;
    }

    /**
     * Creates a new note with a title, a creation date and a mod date
     * @param modDate The last modification date
     * @param creation The creation date
     * @param title The creation title
     */
    public Note(Date modDate, Date creation, String title) {
        this.modDate = modDate;
        this.creation = creation;
        this.title = title;
    }

    /**
     * Sets initializes the modified flag
     */
    @PostLoad void postLoad() {
        modified = modDate !=null;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Content addContent(String name, String value) {
        if (getContent() == null) {
            content = new ArrayList<>();
        }
        return addContent(new Content(name, value, this));
    }

    public Content addContent(Content content) {
        content.parent = this;
        this.content.add(content);
        return content;
    }

    public void removeUpdateContent(Content content) {
        this.content.remove(content);
    }

    public Optional<Content> getContent(String name) {
        return content.stream().filter(it->it.name.equals(name)).findFirst();
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modified) {
        this.modDate = modified;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isModified() {
        return modified;
    }

    public List<Content> getContent() {
        return content;
    }

    public Form getRelatedForm() {
        return relatedForm;
    }

    public void setRelatedForm(Form relatedForm) {
        this.relatedForm = relatedForm;
    }

    /**
     * Represents a field in a particular note
     */
    @Entity(name = "note_content")
    @Table(name = "note_contents")
    public static class Content implements Serializable {

        @Column(name = "content_id")
        @Id
        @GeneratedValue
        private long id;

        @ManyToOne(cascade = CascadeType.ALL)
        private Note parent;

        private String name;

        private String value;

        /**
         * Empty content constructor
         */
        public Content() { }

        /**
         * Creates a new Content with a name, a value and a parent Note
         * @param name The content name
         * @param value The content value
         * @param note The parent {@link Note}
         */
        public Content(String name, String value, Note note) {
            if (note == null)
                throw new InvalidContentException("Unable to construct content", new NullPointerException("Parent is null"));
            else if (note.content.size() > 0 && note.content.stream().anyMatch(it->it.name.equals(name)))
                throw new InvalidContentException("Unable to construct content", new NullPointerException("Duplicate names"));
            else if (value == null)
                throw new InvalidContentException("Unable to construct content", new NullPointerException("Value is null"));

            parent = note;
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }
}
