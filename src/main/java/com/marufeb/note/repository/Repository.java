package com.marufeb.note.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    Optional<T> get(K key);
    List<T> getAll();
    void update(T obj);
    void remove(T obj);
    void drop();
}
