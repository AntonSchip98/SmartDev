package it.schipani.businessLayer.exceptions;

import java.io.Serial;

public class InvalidLoginException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public final  String username;
    public final  String password;

    public InvalidLoginException(String username, String password, String message) {
        super(message);
        this.username = username;
        this.password = password;
    }

    public InvalidLoginException(String username, String password) {
        this(username, password, "Invalid credentials" );
    }
}
