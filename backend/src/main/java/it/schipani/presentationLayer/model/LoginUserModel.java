package it.schipani.presentationLayer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginUserModel(

        @NotBlank(message = "Lo username non può contenere solo spazi vuoti")
        @Size(max = 20, message = "Il tuo username è troppo lungo, max 20 caratteri")
        String username,

        @NotBlank(message = "La password non può contenere solo spazi vuoti")
        @Size(max = 25, message = "La password è troppo lunga, max 20 caratteri")
        String password
) {
}
