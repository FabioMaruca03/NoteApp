package com.marufeb.note.repository;

import java.util.List;
import java.util.Optional;

/**
 * Defines an interface which represents a CRUD repository
 * @param <T> The element type
 * @param <K> The search key for GET operations
 */
public interface Repository<T, K> {

    /**
     * Retrieves an element defined by a Key from the repository
     * @param key The unique search key
     * @return An {@link Optional} which could contain the element
     */
    Optional<T> get(K key);

    /**
     * Collects and returns all elements inside the repository
     * @return A {@link List} of elements
     */
    List<T> getAll();

    /**
     * Updates a particular object inside the repository
     * @param obj The object you want to update
     */
    void update(T obj);

    /**
     * Removes a particular object
     * @param obj The object you want to remove
     */
    void remove(T obj);

    /**
     * Adds a particular object
     * @param obj The object to persist
     */
    void add(T obj);

    /**
     * Deletes the whole repository.
     * <b>NOT</b> the table itself
     */
    void drop();
}
