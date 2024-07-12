package it.schipani.presentationLayer.controller;

import it.schipani.businessLayer.dto.TaskDto.CreateTaskDto;
import it.schipani.businessLayer.dto.TaskDto.TaskDto;
import it.schipani.businessLayer.dto.TaskDto.UpdateTaskDto;
import it.schipani.businessLayer.services.TaskService;
import it.schipani.presentationLayer.model.Requests.CreateTaskRequest;
import it.schipani.presentationLayer.model.Requests.UpdateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/{identityId}")
    public ResponseEntity<TaskDto> createTask(@PathVariable Long identityId, @RequestBody CreateTaskRequest taskRequest) {
        var createTaskDto = CreateTaskDto.builder()
                .withTitle(taskRequest.title())
                .withDescription(taskRequest.description())
                .withCue(taskRequest.cue())
                .withCraving(taskRequest.craving())
                .withResponse(taskRequest.response())
                .withReward(taskRequest.reward())
                .build();

        var createdTask = taskService.createTask(identityId, createTaskDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        Optional<TaskDto> task = taskService.getTaskById(id);

        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest taskRequest) {
        var updateTaskDto = UpdateTaskDto.builder()
                .withTitle(taskRequest.title())
                .withDescription(taskRequest.description())
                .withCue(taskRequest.cue())
                .withCraving(taskRequest.craving())
                .withResponse(taskRequest.response())
                .withReward(taskRequest.reward())
                .withCompleted(taskRequest.completed())
                .build();

        Optional<TaskDto> updatedTask = taskService.updateTask(id, updateTaskDto);

        return updatedTask.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/identity/{identityId}")
    public ResponseEntity<List<TaskDto>> getAllTasksByIdentity(@PathVariable Long identityId) {
        List<TaskDto> tasks = taskService.getAllTasksByIdentity(identityId);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskDto> completeTask(@PathVariable Long id) {
        Optional<TaskDto> updatedTask = taskService.completeTask(id);

        return updatedTask.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
