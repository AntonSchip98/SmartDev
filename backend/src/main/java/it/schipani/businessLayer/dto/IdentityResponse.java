package it.schipani.businessLayer.dto;

import it.schipani.dataLayer.entities.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class IdentityResponse {
    private long id;
    private String identityName;
    private String description;
    private LocalDateTime createdAt;
    private List<Task> tasks;
}
