package org.example.task_management_rest_api.service;

import org.example.task_management_rest_api.repository.TaskRespository;

import org.example.task_management_rest_api.exception.TaskNotFoundException;
import org.example.task_management_rest_api.model.Task;
import org.example.task_management_rest_api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRespository taskRespository;
    private final UserService userService;

    public TaskService(TaskRespository taskRespository, UserService userService) {
        this.taskRespository = taskRespository;
        this.userService = userService;
    }

    public Page<Task> getAllTasksByUserId(Long userId, Pageable pageable) {
        userService.getUserById(userId);
        return taskRespository.findByUser_Id(userId, pageable);
    }

    public Task getTaskById(Long userId, Long taskId) {
        userService.getUserById(userId);
        return taskRespository.findByIdAndUser_Id(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    public Task createTask(Long userId, Task task) {
        User user = userService.getUserById(userId);
        task.setUser(user);
        return taskRespository.save(task);
    }

    public Task updateTask(Long userId, Long taskId, Task task) {
        Task existingTask = taskRespository.findByIdAndUser_Id(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setCompleted(task.isCompleted());
        return taskRespository.save(existingTask);
    }

    public void deleteTask(Long userId, Long taskId) {
        Task existingTask = taskRespository.findByIdAndUser_Id(taskId, userId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        taskRespository.delete(existingTask);
    }
}
