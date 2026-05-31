package org.example.task_management_rest_api.repository;

import org.example.task_management_rest_api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRespository extends JpaRepository<Task, Long> {

}
