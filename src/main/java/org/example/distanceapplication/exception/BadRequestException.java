package org.example.distanceapplication.exception;

@SuppressWarnings("checkstyle:MissingJavadocType")
public class BadRequestException extends RuntimeException {
  public BadRequestException(final String message) {
    super(message);
  }
}
