package org.example.distanceapplication.service;

import java.util.List;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@SuppressWarnings("checkstyle:MissingJavadocType")
@Service
public interface DataService<T, D> {
  void create(T entity) throws BadRequestException;

  List<T> read();

  T getByName(String name) throws ResourceNotFoundException;

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  T getByID(Long id) throws ResourceNotFoundException;

  void update(T entity) throws ResourceNotFoundException;

  void delete(Long id) throws ResourceNotFoundException;

  void createBulk(List<D> list) throws BadRequestException;
}
