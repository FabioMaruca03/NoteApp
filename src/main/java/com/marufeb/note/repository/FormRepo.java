package com.marufeb.note.repository;

import com.marufeb.note.model.Form;
import com.marufeb.note.model.Note;
import com.marufeb.note.model.exceptions.ExceptionsHandler;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This repository provides you CRUD operations
 * @author fabiomaruca
 * @since January 2021
 */
public class FormRepo implements Repository<Form, Long> {

    private final EntityManager EM = Persistence.createEntityManagerFactory("notes").createEntityManager();

    @Override
    public Optional<Form> get(Long key) {
        final TypedQuery<Form> query = EM.createNamedQuery("Form.byUUID", Form.class);
        query.setParameter(1, key);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            ExceptionsHandler.register(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Form> getAll() {
        final TypedQuery<Form> query = EM.createNamedQuery("Form.list", Form.class);
        return query.getResultList();
    }

    @Override
    public void update(Form obj) {
        RepoUtils.executeNotes(em->em.merge(obj));
    }

    @Override
    public void remove(Form obj) {
        RepoUtils.executeNotes(em -> {
            obj.getFields().forEach(em::remove);
            em.remove(obj);
        });
    }

    @Override
    public void add(Form obj) {
        RepoUtils.executeNotes(em-> {
            obj.getFields().forEach(entity -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
            em.persist(em.contains(obj) ? obj : em.merge(obj));
        });
    }

    @Override
    public void drop() {
        EM.createNamedQuery("Form.list", Note.class)
                .getResultStream()
                .forEach(it->RepoUtils.executeNotes(em->em.remove(it)));
    }
}
