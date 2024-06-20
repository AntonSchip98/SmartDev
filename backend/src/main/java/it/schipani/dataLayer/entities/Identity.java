package it.schipani.dataLayer.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "identities")
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@EntityListeners(AuditingEntityListener.class)
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "identity_seq")
    @SequenceGenerator(name = "identity_seq", sequenceName = "identity_seq")
    private long id;

    @Column(length = 50, name = "identity_name")
    private String title;

    @Column(name = "identity_description")
    private String description;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "identity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(max = 3)
    private List<Task> tasks = new ArrayList<>();


}