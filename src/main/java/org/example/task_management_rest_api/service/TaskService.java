package org.example.task_management_rest_api.service;

import java.util.ArrayList;
import java.util.Random;
import org.example.task_management_rest_api.model.Task;
import java.util.List;
import org.example.task_management_rest_api.exception.TaskNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Service
public class TaskService {
    private List<Task> tasks = new ArrayList<>();

    public ResponseEntity<List<Task>> getAllTasks() {
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    public ResponseEntity<Task> getTaskById(Long id) {
        return new ResponseEntity<>(tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("Task not found")), HttpStatus.OK);
    }

    public ResponseEntity<Task> createTask(Task task) {
        task.setId(generateTaskId());
        tasks.add(task);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    public ResponseEntity<Task> updateTask(Long id, Task task) {
        Task existingTask = getTaskById(id).getBody();
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        return new ResponseEntity<>(existingTask, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteTask(Long id) {
        tasks.removeIf(t -> t.getId().equals(id));
        return new ResponseEntity<String>("Task deleted successfully: " + id, HttpStatus.OK);
    }

    private Long generateTaskId() {
        return (long) new Random().nextInt(1000000);
    }
}
