package it.schipani.businessLayer.exceptions;

import jakarta.validation.ValidationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.Serial;
import java.util.List;

/**
 * Eccezione nella validazione di un campo del model.
 *
 */
public class FieldValidationException extends ValidationException {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Errori sui campi.
     */
    public final List<FieldError> errors;
    /**
     * Altri errori di validazione.
     */
    public final List<ObjectError> otherErrors;

    public FieldValidationException(List<ObjectError> errors) {
        this.errors = errors.stream().filter(a -> a instanceof FieldError).map(a -> (FieldError) a).toList();
        this.otherErrors = errors.stream().filter(a -> !(a instanceof FieldError)).toList();
    }
}
