package it.schipani.businessLayer.exceptions;

import it.schipani.businessLayer.dto.DtoBase;

import java.io.Serial;

/*Estende ServiceException per rappresentare un'eccezione durante il salvataggio di un'entità.*/

public class PersistEntityException extends ServiceException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Dato per cui non è stato possibile il salvataggio.
     */
    public final DtoBase invalidDto;

    public PersistEntityException(DtoBase invalidDto) {
        this.invalidDto = invalidDto;
    }
}