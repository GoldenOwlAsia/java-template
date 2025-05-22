package com.goldenowl.springboottemplate.app.exception;

import com.goldenowl.springboottemplate.app.constant.ProfileConstant;
import com.goldenowl.springboottemplate.app.dto.ErrorResponseDTO;
import com.goldenowl.springboottemplate.app.utils.ExceptionHandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@Profile(ProfileConstant.PRODUCTION)
class ProdExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex, WebRequest request) {
        return ExceptionHandlerUtils.generateErrorResponse(ex, "Internal server error", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorResponseDTO> handleRuntimeException(Exception ex, WebRequest request) {
        return ExceptionHandlerUtils.generateErrorResponse(ex, "Unexpected error occurred", request, HttpStatus.BAD_REQUEST);
    }
}
