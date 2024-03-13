package org.example.distanceapplication.service;

import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface DataService<T> {
    boolean create(T entity);

    List<T> read();

    T getByName(String name);

    T getByID(Long id);

    boolean update(T entity);

    boolean delete(Long id);
}
