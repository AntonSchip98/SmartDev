package it.schipani.dataLayer.repositories;

import it.schipani.dataLayer.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
