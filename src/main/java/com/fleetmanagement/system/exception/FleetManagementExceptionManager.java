package com.fleetmanagement.system.exception;

import com.fleetmanagement.system.domain.dto.ExceptionDTO;
import com.fleetmanagement.system.domain.dto.ResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FleetManagementExceptionManager extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(
        Exception ex, WebRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO();
        exceptionDTO.setErrorDescription(ex.getMessage());
        exceptionDTO.setErrorCode("001");
        return handleExceptionInternal(ex, ResponseDTO.builder().exception(exceptionDTO).build(), new HttpHeaders(), HttpStatus.BAD_REQUEST,
            request);
    }
}
