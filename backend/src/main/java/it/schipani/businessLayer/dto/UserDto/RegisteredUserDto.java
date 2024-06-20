package it.schipani.businessLayer.dto.UserDto;

import it.schipani.businessLayer.dto.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class RegisteredUserDto extends DtoBase {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private List<Long> identities;

}
