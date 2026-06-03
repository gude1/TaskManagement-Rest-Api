package org.example.task_management_rest_api.dto.response;

import org.example.task_management_rest_api.model.Task;

import lombok.Getter;

@Getter
public class TaskResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final boolean isCompleted;
    private final Long userId;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.isCompleted = task.isCompleted();
        this.userId = task.getUser().getId();
    }
}
