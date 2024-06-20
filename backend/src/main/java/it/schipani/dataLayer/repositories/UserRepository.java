package it.schipani.dataLayer.repositories;

import it.schipani.dataLayer.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//Questi repository estendono JpaRepository, che fornisce metodi CRUD
// predefiniti (come save, findById, findAll, delete, ecc.).
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByUsernameAndPassword(String username, String password);
    Optional<User> findOneByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
