package it.schipani.dataLayer.entities;

import jakarta.persistence.*;
import jdk.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq")
    private long id;

    @Column(length = 50)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(length = 125)
    private String password;

    private String avatar;

    @OneToMany(orphanRemoval = true)
    private List<Identity> identities;
}
