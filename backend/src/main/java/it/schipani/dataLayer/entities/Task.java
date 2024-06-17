package it.schipani.dataLayer.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_seq")
    private long id;

    @Column(length = 100, name = "task_title")
    private String taskTitle;

    @Column(name = "task_description")
    private String taskDescription;

    private String cue;
    private String craving;
    private String response;
    private String reward;
    private boolean completed = false;

    private LocalDateTime createdAt;

}
