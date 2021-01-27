package com.marufeb.note.repository;

import com.marufeb.note.model.exceptions.ExceptionsHandler;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import java.util.function.Consumer;

/**
 * Provides methods for repositories
 * @author fabiomaruca
 * @since January 2021
 */
public class RepoUtils {

    public static final EntityManager EM_NOTES = Persistence.createEntityManagerFactory("notes").createEntityManager();

    /**
     * Executes an operation inside an {@link EntityTransaction}
     * @param operation The operation you are going to execute
     */
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

