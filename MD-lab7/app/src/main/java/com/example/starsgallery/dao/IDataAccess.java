package com.example.starsgallery.dao;

import java.util.List;

public interface IDataAccess<T> {
    boolean add(T entity);
    boolean modify(T entity);
    boolean remove(T entity);
    T getById(int id);
    List<T> getAll();
}
