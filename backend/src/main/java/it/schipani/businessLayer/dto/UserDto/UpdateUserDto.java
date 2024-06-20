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
public class UpdateUserDto extends DtoBase {
    private String username;
    private String email;
    private String avatar;
    private String password;
    private String token;
}
