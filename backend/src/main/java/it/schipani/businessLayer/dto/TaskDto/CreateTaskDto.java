package it.schipani.businessLayer.dto.TaskDto;

import it.schipani.businessLayer.dto.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class CreateTaskDto extends DtoBase {
    private String title;
    private String description;
    private String cue;
    private String craving;
    private String response;
    private String reward;
}
