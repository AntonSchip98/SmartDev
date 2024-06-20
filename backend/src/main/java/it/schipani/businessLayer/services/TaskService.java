package it.schipani.businessLayer.services;

import it.schipani.businessLayer.dto.TaskDto.CreateTaskDto;
import it.schipani.businessLayer.dto.TaskDto.TaskDto;
import it.schipani.businessLayer.dto.TaskDto.UpdateTaskDto;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    TaskDto createTask(Long identityId, CreateTaskDto taskDto);
    Optional<TaskDto> getTaskById(Long id);
    Optional<TaskDto> updateTask(Long id, UpdateTaskDto taskDto);
    void deleteTask(Long id);
    List<TaskDto> getAllTasksByIdentity(Long identityId);
    Optional<TaskDto> completeTask(Long id);
}
