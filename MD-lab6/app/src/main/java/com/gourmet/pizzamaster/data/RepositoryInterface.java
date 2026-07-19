package com.gourmet.pizzamaster.data;

import java.util.List;

public interface RepositoryInterface<E> {
    E add(E entry);
    E modify(E entry);
    boolean remove(long identifier);
    E getById(long identifier);
    List<E> getAllEntries();
}
