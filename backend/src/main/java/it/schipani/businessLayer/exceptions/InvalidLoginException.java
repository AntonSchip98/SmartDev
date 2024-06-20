package it.schipani.businessLayer.exceptions;

import java.io.Serial;


/*Estende ServiceException per rappresentare un'eccezione di login non valido.*/
public class InvalidLoginException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Username per cui non è stato possibile il salvataggio
     */
    public final String username;
    /**
     * Password per cui non è stato possibile il salvataggio
     */
    public final String password;

    public InvalidLoginException(String username, String password) {
        this.username = username;
        this.password = password;
    }
}