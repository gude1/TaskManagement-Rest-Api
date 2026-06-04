package org.example.task_management_rest_api.controller;

import org.example.task_management_rest_api.dto.request.CreateUserRequest;
import org.example.task_management_rest_api.dto.response.PagedResponse;
import org.example.task_management_rest_api.dto.response.UserResponse;
import org.example.task_management_rest_api.model.User;
import org.example.task_management_rest_api.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        User saved = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(saved));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        PagedResponse<UserResponse> response = PagedResponse.from(
                userService.getAllUsers(pageable),
                UserResponse::new);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(new UserResponse(userService.getUserById(id)));
    }
}
