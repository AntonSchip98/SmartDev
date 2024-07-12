package it.schipani.businessLayer.dto.TaskDto;

import it.schipani.businessLayer.dto.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class TaskDto extends DtoBase {
    private Long id;
    private String title;
    private String description;
    private String cue;
    private String craving;
    private String response;
    private String reward;
    private boolean completed;
    private LocalDateTime createdAt;
    private Long identityId;
}
