package org.example.task_management_rest_api.repository;

import java.util.Optional;

import org.example.task_management_rest_api.model.RefreshToken;
import org.example.task_management_rest_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
