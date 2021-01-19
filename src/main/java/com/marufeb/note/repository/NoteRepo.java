package com.marufeb.note.repository;

import com.marufeb.note.model.Note;

import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author fabiomaruca
 * @since January 2021
 */
public class NoteRepo implements Repository<Note, UUID> {

    @Override
    public Optional<Note> get(UUID key) {
        return Optional.empty();
    }

    @Override
    public List<Note> getAll() {
        return null;
    }

    @Override
    public void update(Note obj) {

    }

    @Override
    public void remove(Note obj) {

    }

    @Override
    public void drop() {

    }
}
