package com.marufeb.note.repository;

import com.marufeb.note.model.Note;
import com.marufeb.note.model.exceptions.ExceptionsHandler;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This repository provides you CRUD operations
 * @author fabiomaruca
 * @since January 2021
 */
public class NoteRepo implements Repository<Note, UUID> {

    @PersistenceContext(name = "notes")
    private EntityManager EM;

    @Override
    public Optional<Note> get(UUID key) {
        final TypedQuery<Note> query = EM.createQuery("Note.byUUID", Note.class);
        query.setParameter(0, key);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            ExceptionsHandler.register(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Note> getAll() {
        return null;
    }

    @Override
    public void update(Note obj) {
        RepoUtils.executeNotes(em->em.persist(obj));
    }

    @Override
    public void remove(Note obj) {
        RepoUtils.executeNotes(em->em.remove(obj));
    }

    @Override
    public void drop() {
        EM.createQuery("Note.list", Note.class)
                .getResultStream()
                .forEach(it->RepoUtils.executeNotes(em->em.remove(it)));
    }
}
