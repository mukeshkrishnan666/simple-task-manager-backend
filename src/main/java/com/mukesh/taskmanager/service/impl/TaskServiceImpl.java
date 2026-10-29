package com.mukesh.taskmanager.service.impl;

import com.mukesh.taskmanager.dto.TaskRequest;
import com.mukesh.taskmanager.dto.TaskResponse;
import com.mukesh.taskmanager.model.Task;
import com.mukesh.taskmanager.repository.TaskRepository;
import com.mukesh.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        log.info("Fetching tasks page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return taskRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public TaskResponse createTask(TaskRequest request) {
        log.info("Creating new task with title: {}", request.getTitle());
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
