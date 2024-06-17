package it.schipani.dataLayer.repositories;

import it.schipani.dataLayer.entities.Identity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityRepository extends JpaRepository<Identity, Long> {
}
