package org.example.task_management_rest_api.repository;

import org.example.task_management_rest_api.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRespository extends JpaRepository<Task, Long> {
    Page<Task> findByUser_Id(Long userId, Pageable pageable);

    Optional<Task> findByIdAndUser_Id(Long id, Long userId);
}
