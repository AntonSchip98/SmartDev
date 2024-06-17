package it.schipani.dataLayer.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "identities")
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "identity_seq")
    @SequenceGenerator(name = "identity_seq", sequenceName = "identity_seq")
    private long id;

    @Column(length = 50, name = "identity_name")
    private String identityName;

    @Column(name = "identity_description")
    private String description;

    private LocalDateTime createdAt;

    @OneToMany
    private List<Task> tasks;
}
