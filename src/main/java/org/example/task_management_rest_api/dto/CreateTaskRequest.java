package org.example.task_management_rest_api.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 1000, message = "Description must be between 3 and 1000 characters")
    private String description;
    private boolean isCompleted;
}
