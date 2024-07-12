package it.schipani.presentationLayer.model.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Il nome utente non può essere vuoto")
        @Size(max = 50, message = "Il nome utente non può superare i 50 caratteri")
        String username,

        @NotBlank(message = "L'email non può essere vuota")
        @Email(message = "L'email deve essere valida")
        @Size(max = 100, message = "L'email non può superare i 100 caratteri")
        String email,

        @Size(max = 255, message = "L'avatar non può superare i 255 caratteri")
        String avatar,

        @Size(min = 8, max = 125, message = "La password deve avere tra 8 e 125 caratteri")
        String password // Aggiungi il campo password
) {
}
