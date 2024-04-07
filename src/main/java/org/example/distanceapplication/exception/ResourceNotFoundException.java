package org.example.distanceapplication.exception;

@SuppressWarnings("checkstyle:MissingJavadocType")
public class ResourceNotFoundException extends Exception {
  public ResourceNotFoundException(final String message) {
    super(message);
  }
}
