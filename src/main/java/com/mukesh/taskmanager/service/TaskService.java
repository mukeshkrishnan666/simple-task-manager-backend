package com.mukesh.taskmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mukesh.taskmanager.dto.TaskRequest;
import com.mukesh.taskmanager.dto.TaskResponse;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    Page<TaskResponse> getAllTasks(Pageable pageable);
}
