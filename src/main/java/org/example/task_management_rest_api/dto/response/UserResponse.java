package org.example.task_management_rest_api.dto.response;

import org.example.task_management_rest_api.model.User;

import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String email;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }
}
