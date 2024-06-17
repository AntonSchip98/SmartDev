package it.schipani.businessLayer.exceptions;

//Eccezzione personalizzata che può essere utilizzata per gestire scenari in cui le dimensioni
// dei file superano un certo limite.
public class FileSizeExceededException extends RuntimeException{
    public FileSizeExceededException(String message){
        super(message);
    }
}
