package it.schipani.businessLayer.dto.IdentityDto;

import it.schipani.businessLayer.dto.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class IdentityDto extends DtoBase {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Long userId;
    private List<Long> tasks;
}
