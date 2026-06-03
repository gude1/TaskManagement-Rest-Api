package org.example.task_management_rest_api.controller;

import org.example.task_management_rest_api.dto.request.CreateTaskRequest;
import org.example.task_management_rest_api.dto.response.TaskResponse;
import org.example.task_management_rest_api.model.Task;
import org.example.task_management_rest_api.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(@PathVariable Long userId) {
        List<TaskResponse> tasks = taskService.getAllTasksByUserId(userId).stream()
                .map(TaskResponse::new)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long userId, @PathVariable Long taskId) {
        return ResponseEntity.ok(new TaskResponse(taskService.getTaskById(userId, taskId)));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long userId,
            @Valid @RequestBody CreateTaskRequest request) {
        Task task = toTask(request);
        Task saved = taskService.createTask(userId, task);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponse(saved));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long userId,
            @PathVariable Long taskId,
            @Valid @RequestBody CreateTaskRequest request) {
        Task task = toTask(request);
        Task updated = taskService.updateTask(userId, taskId, task);
        return ResponseEntity.ok(new TaskResponse(updated));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long userId, @PathVariable Long taskId) {
        taskService.deleteTask(userId, taskId);
        return ResponseEntity.ok("Task deleted successfully: " + taskId);
    }

    private Task toTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        return task;
    }
}
