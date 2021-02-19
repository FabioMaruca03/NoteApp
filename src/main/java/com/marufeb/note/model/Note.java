package com.marufeb.note.model;

import com.marufeb.note.model.exceptions.InvalidContentException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
    private final List<Content> content = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "treatment", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "treatment_id"))
    private final List<Treatment> treatments = new ArrayList<>();

    /**
     * Empty constructor - used by HIBERNATE -
     */
    public Note() {
        addTreatment(new Treatment());
    }

    /**
     * Creates a new note with a title and a creation date
     * @param creation The creation date
     * @param title The title of the note
     */
    public Note(Date creation, String title) {
        this();
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
    @PostLoad
    void postLoad() {
        modified = modDate !=null;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void addContent(String name, String value) {
        addContent(new Content(name, value, this));
    }

    public void addTreatment(Treatment treatment) {
        System.out.println("Created treatment");
        if (!treatments.contains(treatment)) {
            treatment.setReference(this);
            treatment.setNumber(treatments.size());
            treatments.add(treatment);
        }
    }

    public void remTreatment(Treatment treatment) {
        treatments.remove(treatment);
    }

    public void addContent(Content content) {
        content.parent = this;
        this.content.add(content);
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
        this.modified = true;
        this.modDate = modified;
    }

    public int getTreatmentsNumber() {
        return treatments.size();
    }

    public List<Treatment> getTreatments() {
        return treatments;
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

        @ManyToOne
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id &&
                modified == note.modified &&
                Objects.equals(modDate, note.modDate) &&
                Objects.equals(relatedForm, note.relatedForm) &&
                Objects.equals(creation, note.creation) &&
                Objects.equals(title, note.title) &&
                Objects.equals(content, note.content) &&
                Objects.equals(treatments, note.treatments);
    }

    @Override
    public int hashCode() {
        if (content.size()>0) {
            final String[] strings = content.stream().map(Content::getValue).toArray(String[]::new);
            return Objects.hash(id, modDate, relatedForm, modified, creation, title, Arrays.deepHashCode(strings), treatments.hashCode());
        }
        return Objects.hash(id, modDate, relatedForm, modified, creation, title, content, treatments);
    }
}
