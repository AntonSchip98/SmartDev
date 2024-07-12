package it.schipani.businessLayer.exceptions;

import java.io.Serial;

/*Questa classe viene utilizzata per gestire le eccezioni specifiche del livello di servizio
all'interno diun'applicazione, estende RuntimeException, Ciò significa che ServiceException è
un tipo di eccezione non controllata*/
public class ServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;


    /*In sintesi, la classe ServiceException fornisce diversi costruttori per creare eccezioni con
     vari livelli di dettaglio (messaggio di errore, causa dell'errore,*/
    public ServiceException() {
        super();
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

}