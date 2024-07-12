package it.schipani.presentationLayer.model.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginUserRequest(
        @NotBlank(message = "Il nome utente non può essere vuoto")
        @Size(max = 125, message = "Il nome utente non può superare i 125 caratteri")
        String username,

        @NotBlank(message = "La password non può essere vuota")
        @Size(max = 15, message = "La password non può superare i 15 caratteri")
        String password
) {
}
