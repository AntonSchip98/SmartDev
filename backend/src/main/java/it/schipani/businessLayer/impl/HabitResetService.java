package it.schipani.businessLayer.impl;

import it.schipani.dataLayer.entities.Task;
import it.schipani.dataLayer.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitResetService {
    private final TaskRepository taskRepository;

    @Scheduled(cron = "0 * * * * ?") // Esegui ogni giorno a mezzanotte
    public void resetHabits() {
        List<Task> tasks = taskRepository.findAll();
        tasks.forEach(task -> {
            task.setCompleted(false);
        });
        taskRepository.saveAll(tasks);
        log.info("All habit tasks have been reset to incomplete.");
    }
}
