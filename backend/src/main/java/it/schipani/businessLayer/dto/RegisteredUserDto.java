package it.schipani.businessLayer.dto;

//DTO per la rappresentazione di un utente registrato
import it.schipani.dataLayer.entities.Identity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class RegisteredUserDto {
    private Long id;
    private String username;
    private String email;
    private List<Identity> identities;
}
