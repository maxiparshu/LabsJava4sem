package org.example.distanceapplication.exception;

import java.util.Date;

@SuppressWarnings("checkstyle:MissingJavadocType")
public record ExceptionDetails(Date date, String exceptionMessage) {
}
