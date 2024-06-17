package it.schipani.errors;

import it.schipani.businessLayer.exceptions.FieldValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)//Assicura che questa configurazione di gestione delle eccezioni abbia la massima priorità.
@RestControllerAdvice // Combina le funzionalità di @ControllerAdvice e @ResponseBody, permettendo di gestire le eccezioni lanciate dai controller
// restituendo direttamente le risposte HTTP.
public class ErrorHandler extends ResponseEntityExceptionHandler {

    // Classe interna per gestire gli errori di validazione specifici del campo
    public record ValidationError(String field, String message) {
    }
    // Gestione di FieldValidationException
    @ExceptionHandler(FieldValidationException.class)
    protected ResponseEntity<?> handleFieldValidationException(FieldValidationException e) {
        List<ValidationError> body = e.errors.stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Gestione di ServiceException
    @ExceptionHandler(ServiceException.class)
    protected ResponseEntity<String> handleServiceException(ServiceException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Gestione di EntityNotFoundException
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException error) {
        return new ResponseEntity<>(error.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Gestione di EntityExistsException
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleEntityExistsException(EntityExistsException error) {
        return new ResponseEntity<>(error.getMessage(), HttpStatus.CONFLICT);
    }

    // Gestione di MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException error) {
        Map<String, String> errorResponse = new HashMap<>();
        error.getBindingResult().getAllErrors().forEach(
                er -> {
                    FieldError fieldError = (FieldError) er;
                    String fieldName = fieldError.getField();
                    String errorMessage = er.getDefaultMessage();
                    errorResponse.put(fieldName, errorMessage);
                }
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
