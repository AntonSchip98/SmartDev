package it.schipani.presentationLayer.model.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @NotBlank(message = "Il titolo non può essere vuoto")
        @Size(max = 100, message = "Il titolo non può superare i 100 caratteri")
        String title,

        @NotBlank(message = "La descrizione non può essere vuota")
        @Size(max = 255, message = "La descrizione non può superare i 255 caratteri")
        String description,

        @NotBlank(message = "Il cue non può essere vuoto")
        @Size(max = 255, message = "Il cue non può superare i 255 caratteri")
        String cue,

        @NotBlank(message = "Il craving non può essere vuoto")
        @Size(max = 255, message = "Il craving non può superare i 255 caratteri")
        String craving,

        @NotBlank(message = "Il response non può essere vuoto")
        @Size(max = 255, message = "Il response non può superare i 255 caratteri")
        String response,

        @NotBlank(message = "Il reward non può essere vuoto")
        @Size(max = 255, message = "Il reward non può superare i 255 caratteri")
        String reward,

        boolean completed
) {
}
