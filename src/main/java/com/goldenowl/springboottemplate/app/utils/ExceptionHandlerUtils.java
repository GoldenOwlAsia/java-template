package com.goldenowl.springboottemplate.app.utils;

import com.goldenowl.springboottemplate.app.dto.ErrorResponseDTO;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@UtilityClass
@Slf4j
public class ExceptionHandlerUtils {

  public ResponseEntity<ErrorResponseDTO> generateErrorResponse(
      Exception ex, String exceptionMessage, WebRequest request, HttpStatus status) {
    return build(ex, exceptionMessage, request, status, null);
  }

  public ResponseEntity<ErrorResponseDTO> generateErrorResponse(
      Exception ex, WebRequest request, HttpStatus status) {
    return build(ex, ex.getMessage(), request, status, null);
  }

  public ResponseEntity<ErrorResponseDTO> generateErrorResponse(
      Exception ex,
      String exceptionMessage,
      WebRequest request,
      HttpStatus status,
      Map<String, String> errors) {
    return build(ex, exceptionMessage, request, status, errors);
  }

  private ResponseEntity<ErrorResponseDTO> build(
      Exception ex,
      String message,
      WebRequest request,
      HttpStatus status,
      Map<String, String> errors) {
    log.error(
        "Exception caught - status: {}, path: {}, message: {}",
        status.value(),
        request.getDescription(false),
        ex.getMessage());

    ErrorResponseDTO errorResponse =
        ErrorResponseDTO.builder()
            .message(message)
            .path(request.getDescription(false))
            .status(status.value())
            .errors(errors)
            .build();
    return new ResponseEntity<>(errorResponse, status);
  }
}
