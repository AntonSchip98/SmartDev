package it.schipani.businessLayer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import java.io.Serial;
import java.util.List;

public class ApiValidationException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public final List<ObjectError> errorsList;

    public final HttpStatus status;

    public ApiValidationException(List<ObjectError> errorsList) {
        this.errorsList = errorsList;
        this.status = HttpStatus.SERVICE_UNAVAILABLE;
    }

    public ApiValidationException(List<ObjectError> errors, HttpStatus status){
        this.errorsList = errors;
        this.status = status;
    }
}
