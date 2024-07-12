package it.schipani.businessLayer.dto.UserDto;

import it.schipani.businessLayer.dto.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class LoginUserDto extends DtoBase {
    private Long id;
    private String username;
    private String email;
    private String token;
}
