package com.goldenowl.springboottemplate.app.exception;

import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import com.goldenowl.springboottemplate.app.dto.ErrorResponseDTO;
import com.goldenowl.springboottemplate.app.utils.ExceptionHandlerUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private static final String GENERIC_SERVER_ERROR = "Internal server error";

  private final Environment environment;

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

  @ExceptionHandler(HandlerMethodValidationException.class)
  ResponseEntity<ErrorResponseDTO> handleHandlerMethodValidation(
      HandlerMethodValidationException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getParameterValidationResults()
        .forEach(
            result -> {
              String name = result.getMethodParameter().getParameterName();
              String key = name != null ? name : "param";
              result
                  .getResolvableErrors()
                  .forEach(error -> errors.put(key, error.getDefaultMessage()));
            });
    return ExceptionHandlerUtils.generateErrorResponse(
        ex, "Validation failed", request, HttpStatus.BAD_REQUEST, errors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ErrorResponseDTO> handleUnreadableMessage(
      HttpMessageNotReadableException ex, WebRequest request) {
    return ExceptionHandlerUtils.generateErrorResponse(
        ex, "Malformed request body", request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ErrorResponseDTO> handleUnhandled(Exception ex, WebRequest request) {
    String message =
        hideExceptionMessage() || !StringUtils.hasText(ex.getMessage())
            ? GENERIC_SERVER_ERROR
            : ex.getMessage();
    return ExceptionHandlerUtils.generateErrorResponse(
        ex, message, request, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private boolean hideExceptionMessage() {
    return environment.acceptsProfiles(Profiles.of(ProfileConstant.PRODUCTION));
  }
}
