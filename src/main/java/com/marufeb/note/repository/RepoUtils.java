package com.marufeb.note.repository;

import com.marufeb.note.model.exceptions.ExceptionsHandler;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.util.function.Consumer;

/**
 * @author fabiomaruca
 * @since January 2021
 */
public class RepoUtils {

    @PersistenceContext(name = "notes")
    public static EntityManager EM_NOTES;

    public static void executeNotes(Consumer<EntityManager> operation) {
        final EntityTransaction transaction = EM_NOTES.getTransaction();
        transaction.begin();
        try {
            operation.accept(EM_NOTES);
            transaction.commit();
        } catch (Exception e) {
            ExceptionsHandler.register(e);
            transaction.rollback();
        }
    }

}

