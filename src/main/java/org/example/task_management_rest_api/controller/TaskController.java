package org.example.task_management_rest_api.controller;

import org.example.task_management_rest_api.dto.request.CreateTaskRequest;
import org.example.task_management_rest_api.dto.response.PagedResponse;
import org.example.task_management_rest_api.dto.response.TaskResponse;
import org.example.task_management_rest_api.model.Task;
import org.example.task_management_rest_api.service.TaskService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

@RestController
@RequestMapping("/api/user/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskResponse>> getAllTasks(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        PagedResponse<TaskResponse> response = PagedResponse.from(
                taskService.getAllTasks(pageable),
                TaskResponse::new);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity.ok(new TaskResponse(taskService.getTaskById(taskId)));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = toTask(request);
        Task saved = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponse(saved));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateTaskRequest request) {
        Task task = toTask(request);
        Task updated = taskService.updateTask(taskId, task);
        return ResponseEntity.ok(new TaskResponse(updated));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
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
