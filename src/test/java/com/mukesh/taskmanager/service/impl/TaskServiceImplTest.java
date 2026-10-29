package com.mukesh.taskmanager.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mukesh.taskmanager.dto.TaskRequest;
import com.mukesh.taskmanager.dto.TaskResponse;
import com.mukesh.taskmanager.model.Task;
import com.mukesh.taskmanager.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task savedTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");

        savedTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void givenValidRequest_whenCreateTask_thenReturnTaskResponse() {
        // Given
        given(taskRepository.save(any(Task.class))).willReturn(savedTask);

        // When
        TaskResponse response = taskService.createTask(taskRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void givenTasksExist_whenGetAllTasks_thenReturnPaginatedTasks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(savedTask));
        given(taskRepository.findAll(pageable)).willReturn(taskPage);

        // When
        Page<TaskResponse> response = taskService.getAllTasks(pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Test Task", response.getContent().get(0).getTitle());
        verify(taskRepository).findAll(pageable);
    }
}
