package com.mukesh.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mukesh.taskmanager.dto.TaskRequest;
import com.mukesh.taskmanager.dto.TaskResponse;
import com.mukesh.taskmanager.exception.GlobalExceptionHandler;
import com.mukesh.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void givenValidTask_whenCreateTask_thenReturn201() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("Task Description");

        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("New Task")
                .description("Task Description")
                .createdAt(LocalDateTime.now())
                .build();

        given(taskService.createTask(any(TaskRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void givenEmptyTitle_whenCreateTask_thenReturn400BadRequest() throws Exception {
        TaskRequest invalidRequest = new TaskRequest();
        invalidRequest.setTitle("");
        invalidRequest.setDescription("Description with blank title");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void givenTasks_whenGetAllTasks_thenReturn200AndPage() throws Exception {
        TaskResponse response = TaskResponse.builder()
                .id(1L)
                .title("New Task")
                .description("Task Description")
                .createdAt(LocalDateTime.now())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        given(taskService.getAllTasks(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("New Task"));
    }

    @Test
    void givenInvalidUrl_whenGetRequest_thenReturn404NotFound() throws Exception {
        mockMvc.perform(get("/api/invalid-endpoint")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("The requested endpoint does not exist"));
    }

    @Test
    void givenSystemFailure_whenGetAllTasks_thenReturn500InternalServerError() throws Exception {
        given(taskService.getAllTasks(any(Pageable.class)))
                .willThrow(new RuntimeException("Database connection timed out"));

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected internal server error occurred"));
    }
}
