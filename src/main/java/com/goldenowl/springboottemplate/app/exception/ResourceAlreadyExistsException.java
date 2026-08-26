package com.goldenowl.springboottemplate.app.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

  public ResourceAlreadyExistsException(String resourceName, String fieldName, String fieldValue) {
    super(
        "%s already exists with the given input data %s : '%s'"
            .formatted(resourceName, fieldName, fieldValue));
  }

  public ResourceAlreadyExistsException(String message) {
    super(message);
  }
}
