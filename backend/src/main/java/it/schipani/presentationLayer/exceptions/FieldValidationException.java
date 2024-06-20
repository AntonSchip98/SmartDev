package it.schipani.presentationLayer.exceptions;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.Serial;
import java.util.List;

/*Rappresenta un'eccezione personalizzata per la gestione degli errori di validazione dei campi.
 * extends ValidationException indica che FieldValidationException è un tipo di eccezione
 *  di validazione*/
public class FieldValidationException extends ValidationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public final List<FieldError> errors;
    public final List<ObjectError> otherErrors;

    public FieldValidationException(List<ObjectError> errors) {
        this.errors = errors.stream().filter(a -> a instanceof FieldError).map(a -> (FieldError) a).toList();
        this.otherErrors = errors.stream().filter(a -> !(a instanceof FieldError)).toList();
    }
}
