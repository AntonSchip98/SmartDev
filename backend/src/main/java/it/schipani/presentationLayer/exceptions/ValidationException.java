package it.schipani.presentationLayer.exceptions;

import java.io.Serial;

/*
Rappresenta un'eccezione personalizzata per la gestione delle eccezioni di validazione.
Estende RuntimeException, il che significa che è un'eccezione non controllata.
Questa eccezione personalizzata può essere utilizzata per gestire errori di validazione in
un'applicazione, fornendo messaggi esplicativi e cause delle eccezioni.
*/
public class ValidationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ValidationException() {
        super();
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable cause) {
        super(cause);

    }

}