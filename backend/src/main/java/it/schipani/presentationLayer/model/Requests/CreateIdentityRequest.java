package it.schipani.presentationLayer.model.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateIdentityRequest(
        @NotBlank(message = "Il titolo non può essere vuoto")
        @Size(max = 50, message = "Il titolo non può superare i 50 caratteri")
        String title,

        @NotBlank(message = "La descrizione non può essere vuota")
        @Size(max = 255, message = "La descrizione non può superare i 255 caratteri")
        String description
) {
}
