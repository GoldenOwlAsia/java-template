package com.goldenowl.springboottemplate.app.exception;

import com.goldenowl.springboottemplate.app.dto.ErrorResponseDTO;
import com.goldenowl.springboottemplate.app.utils.ExceptionHandlerUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    InvalidTokenException.class,
    SignUpNotValidException.class,
    LoginNotValidException.class,
    IllegalArgumentException.class
  })
  ResponseEntity<ErrorResponseDTO> handleBadRequestsException(
      RuntimeException ex, WebRequest request) {
    return ExceptionHandlerUtils.generateErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<ErrorResponseDTO> handleNotFound(
      ResourceNotFoundException ex, WebRequest request) {
    return ExceptionHandlerUtils.generateErrorResponse(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  ResponseEntity<ErrorResponseDTO> handleConflict(
      ResourceAlreadyExistsException ex, WebRequest request) {
    return ExceptionHandlerUtils.generateErrorResponse(ex, request, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BlacklistedTokenException.class)
  ResponseEntity<ErrorResponseDTO> handleBlacklistedToken(
      BlacklistedTokenException ex, WebRequest request) {
    return ExceptionHandlerUtils.generateErrorResponse(ex, request, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
  ResponseEntity<ErrorResponseDTO> handleAccessDenied(RuntimeException ex, WebRequest request) {
    return ExceptionHandlerUtils.generateErrorResponse(
        ex, "Access denied", request, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return ExceptionHandlerUtils.generateErrorResponse(
        ex, "Validation failed", request, HttpStatus.BAD_REQUEST, errors);
  }
}
