package com.mukesh.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mukesh.taskmanager.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
