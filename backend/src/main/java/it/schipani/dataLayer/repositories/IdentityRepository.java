package it.schipani.dataLayer.repositories;

import it.schipani.dataLayer.entities.Identity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, Long> {
    List<Identity> findAllByUserId(Long userId);
}
