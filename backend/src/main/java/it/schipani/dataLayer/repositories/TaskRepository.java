package it.schipani.dataLayer.repositories;

import it.schipani.dataLayer.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByIdentityId(Long identityId);
}
