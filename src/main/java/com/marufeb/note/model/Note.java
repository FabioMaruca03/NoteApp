package com.marufeb.note.model;

import com.marufeb.note.model.exceptions.InvalidContentException;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author fabiomaruca
 * @since January 2021
 */
@Entity(name = "note")
@NamedQuery(name = "Note.byUUID", query = "FROM note WHERE id = ?")
@NamedQuery(name = "Note.list", query = "DELETE FROM note")
@Table(name = "notes")
public class Note {

    @Column(name = "note_id")
    private UUID id;

    @Column(name = "note_modified")
    private Date modDate;

    @Transient
    private boolean modified = false;

    @Column(name = "note_creation")
    private Date creation;

    @Column(name = "note_title")
    private String title;

    @OneToMany(targetEntity = Content.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Column(name = "note_contents")
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

    public void setId(UUID id) {
        this.id = id;
    }

    @Id
    public UUID getId() {
        return id;
    }

    public Content addContent(String name, String value) {
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

    /**
     * Represents a field in a particular note
     */
    @Entity(name = "note_content")
    @Table(name = "note_contents")
    public static class Content {

        @Column(name = "content_id")
        private Long id;

        @ManyToOne(targetEntity = Note.class, cascade = CascadeType.ALL)
        @Column(name = "note", nullable = false)
        private Note parent;

        @Column(name = "name")
        private String name;

        @Column(name = "value")
        private String value;

        public Content(String name, String value, Note note) {
            if (parent == null)
                throw new InvalidContentException("Unable to construct content", new NullPointerException("Parent is null"));
            else if (parent.content.stream().anyMatch(it->it.name.equals(name)))
                throw new InvalidContentException("Unable to construct content", new NullPointerException("Duplicate names"));
            else if (value == null)
                throw new InvalidContentException("Unable to construct content", new NullPointerException("Value is null"));

            parent = note;
            this.name = name;
            this.value = value;
        }

        public Content() { }

        public Note getParent() {
            return parent;
        }

        public void setParent(Note parent) {
            this.parent = parent;
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

        public void setId(Long id) {
            this.id = id;
        }

        @Id
        public Long getId() {
            return id;
        }
    }
}
