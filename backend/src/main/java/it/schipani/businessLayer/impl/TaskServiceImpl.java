package it.schipani.businessLayer.impl;

import it.schipani.businessLayer.dto.TaskDto.CreateTaskDto;
import it.schipani.businessLayer.dto.TaskDto.TaskDto;
import it.schipani.businessLayer.dto.TaskDto.UpdateTaskDto;
import it.schipani.businessLayer.exceptions.PersistEntityException;
import it.schipani.businessLayer.services.TaskService;
import it.schipani.dataLayer.entities.Identity;
import it.schipani.dataLayer.entities.Task;
import it.schipani.dataLayer.repositories.IdentityRepository;
import it.schipani.dataLayer.repositories.TaskRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final IdentityRepository identityRepository;

    @Override
    public TaskDto createTask(Long identityId, CreateTaskDto taskDto) {
        Identity identity = identityRepository.findById(identityId).orElseThrow(() -> new EntityNotFoundException("Identity not found"));
        if (identity.getTasks().size() >= 3) {
            throw new EntityExistsException("Identity cannot have more than 3 tasks");
        }
        try {
            var task = new Task();
            BeanUtils.copyProperties(taskDto, task);
            task.setIdentity(identity);
            taskRepository.save(task);

            return TaskDto.builder()
                    .withId(task.getId())
                    .withTitle(task.getTitle())
                    .withDescription(task.getDescription())
                    .withCue(task.getCue())
                    .withCraving(task.getCraving())
                    .withResponse(task.getResponse())
                    .withReward(task.getReward())
                    .withCompleted(task.isCompleted())
                    .withCreatedAt(task.getCreatedAt())
                    .withIdentityId(task.getIdentity().getId())
                    .build();
        } catch (Exception e) {
            log.error(String.format("Exception saving task %s", taskDto), e);
            throw new PersistEntityException(taskDto);
        }
    }

    @Override
    public Optional<TaskDto> getTaskById(Long id) {
        return taskRepository.findById(id).map(task -> TaskDto.builder()
                .withId(task.getId())
                .withTitle(task.getTitle())
                .withDescription(task.getDescription())
                .withCue(task.getCue())
                .withCraving(task.getCraving())
                .withResponse(task.getResponse())
                .withReward(task.getReward())
                .withCompleted(task.isCompleted())
                .withCreatedAt(task.getCreatedAt())
                .withIdentityId(task.getIdentity().getId())
                .build());
    }

    @Override
    public Optional<TaskDto> updateTask(Long id, UpdateTaskDto taskDto) {
        return taskRepository.findById(id).map(task -> {
            BeanUtils.copyProperties(taskDto, task);
            taskRepository.save(task);
            return TaskDto.builder()
                    .withId(task.getId())
                    .withTitle(task.getTitle())
                    .withDescription(task.getDescription())
                    .withCue(task.getCue())
                    .withCraving(task.getCraving())
                    .withResponse(task.getResponse())
                    .withReward(task.getReward())
                    .withCompleted(task.isCompleted())
                    .withCreatedAt(task.getCreatedAt())
                    .withIdentityId(task.getIdentity().getId())
                    .build();
        });
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskDto> getAllTasksByIdentity(Long identityId) {
        return taskRepository.findAllByIdentityId(identityId).stream()
                .map(task -> TaskDto.builder()
                        .withId(task.getId())
                        .withTitle(task.getTitle())
                        .withDescription(task.getDescription())
                        .withCue(task.getCue())
                        .withCraving(task.getCraving())
                        .withResponse(task.getResponse())
                        .withReward(task.getReward())
                        .withCompleted(task.isCompleted())
                        .withCreatedAt(task.getCreatedAt())
                        .withIdentityId(task.getIdentity().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TaskDto> completeTask(Long id) {
        return taskRepository.findById(id).map(task -> {
            task.setCompleted(true);
            taskRepository.save(task);
            return TaskDto.builder()
                    .withId(task.getId())
                    .withTitle(task.getTitle())
                    .withDescription(task.getDescription())
                    .withCue(task.getCue())
                    .withCraving(task.getCraving())
                    .withResponse(task.getResponse())
                    .withReward(task.getReward())
                    .withCompleted(task.isCompleted())
                    .withCreatedAt(task.getCreatedAt())
                    .withIdentityId(task.getIdentity().getId())
                    .build();
        });
    }
}
