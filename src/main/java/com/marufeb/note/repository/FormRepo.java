package com.marufeb.note.repository;

import com.marufeb.note.model.Form;
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
public class FormRepo implements Repository<Form, UUID> {

    @PersistenceContext(name = "notes")
    private EntityManager EM;

    @Override
    public Optional<Form> get(UUID key) {
        final TypedQuery<Form> query = EM.createQuery("Form.byUUID", Form.class);
        query.setParameter(0, key);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            ExceptionsHandler.register(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Form> getAll() {
        final TypedQuery<Form> query = EM.createQuery("Form.list", Form.class);
        return query.getResultList();
    }

    @Override
    public void update(Form obj) {
        RepoUtils.executeNotes(em->em.persist(obj));
    }

    @Override
    public void remove(Form obj) {
        RepoUtils.executeNotes(em->em.remove(obj));
    }

    @Override
    public void drop() {
        EM.createQuery("Form.list", Note.class)
                .getResultStream()
                .forEach(it->RepoUtils.executeNotes(em->em.remove(it)));
    }
}
