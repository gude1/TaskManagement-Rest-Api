package org.example.task_management_rest_api.service;

import org.example.task_management_rest_api.repository.TaskRespository;

import java.util.List;
import org.example.task_management_rest_api.exception.TaskNotFoundException;
import org.example.task_management_rest_api.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Service
public class TaskService {
    private final TaskRespository taskRespository;

    public TaskService(TaskRespository taskRespository) {
        this.taskRespository = taskRespository;
    }

    public ResponseEntity<List<Task>> getAllTasks() {
        return new ResponseEntity<List<Task>>(taskRespository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Task> getTaskById(Long id) {
        return new ResponseEntity<Task>(taskRespository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found")), HttpStatus.OK);
    }

    public ResponseEntity<Task> createTask(Task task) {
        return new ResponseEntity<Task>(taskRespository.save(task), HttpStatus.CREATED);
    }

    public ResponseEntity<Task> updateTask(Long id, Task task) {
        Task existingTask = taskRespository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setCompleted(task.isCompleted());
        Task updated = taskRespository.save(existingTask);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteTask(Long id) {
        taskRespository.deleteById(id);
        return new ResponseEntity<String>("Task deleted successfully: " + id, HttpStatus.OK);
    }
}
