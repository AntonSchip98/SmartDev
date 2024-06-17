package it.schipani.businessLayer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO per i dati necessari a creare lo user, da utilizzare nel metodo register
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class IdentityRequest {
    private String username;
    private String email;
    private String avatar;
    private String password;
}
