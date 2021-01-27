package com.marufeb.note.repository;

import com.marufeb.note.model.Note;
import com.marufeb.note.model.exceptions.ExceptionsHandler;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This repository provides you CRUD operations
 * @author fabiomaruca
 * @since January 2021
 */
public class NoteRepo implements Repository<Note, Long> {

    private final EntityManager EM = Persistence.createEntityManagerFactory("notes").createEntityManager();

    @Override
    public Optional<Note> get(Long key) {
        final TypedQuery<Note> query = EM.createNamedQuery("Note.byUUID", Note.class);
        query.setParameter(1, key);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            ExceptionsHandler.register(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Note> getAll() {
        final TypedQuery<Note> query = EM.createNamedQuery("Note.list", Note.class);
        return query.getResultList();
    }

    @Override
    public void update(Note obj) {
        RepoUtils.executeNotes(em->em.merge(obj));
    }

    @Override
    public void remove(Note obj) {
        RepoUtils.executeNotes(em->em.remove(obj));
    }

    @Override
    public void add(Note obj) {
        RepoUtils.executeNotes(em-> em.persist(obj));
    }

    @Override
    public void drop() {
        try {
            EM.createNamedQuery("Note.list", Note.class)
                    .getResultStream()
                    .forEach(it->RepoUtils.executeNotes(em->em.remove(it)));
        } catch (Exception ignore) { }
    }
}
