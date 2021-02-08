package com.marufeb.note.repository;

import com.marufeb.note.model.Note;

import java.io.File;
import java.util.List;

/**
 * Will enable export features for .docx, .pdf, .xls and more
 * @author fabiomaruca
 * @since January 2021
 */
public class ResourceLoader {

    /**
     * Loads notes from a given folder
     * @param file The folder which contains all .rtf files
     * @return A list of {@link Note}
     */
    public List<Note> loadNotes(File file) {
//        final Document doc = new Document();
//        final RtfParser rtfParser = new RtfParser(doc);
//        Arrays.stream(Objects.requireNonNull(file.listFiles((name) -> name.getName().endsWith(".rtf")))).forEach(it->{
//            doc.open();
//            doc.close();
//        });
        return List.of();
    }
}
