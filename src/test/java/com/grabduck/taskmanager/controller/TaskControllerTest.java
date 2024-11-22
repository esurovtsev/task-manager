package com.grabduck.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.dto.PagedResponseDto;
import com.grabduck.taskmanager.exception.InvalidTaskException;
import com.grabduck.taskmanager.exception.TaskNotFoundException;
import com.grabduck.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private Task testTask;
    private UUID testTaskId;

    @BeforeEach
    void setUp() {
        testTaskId = UUID.randomUUID();
        Task newTask = Task.createNew(
                "Test Task",
                "Test Description",
                LocalDateTime.now().plusDays(1),
                TaskPriority.MEDIUM,
                Set.of("test", "unit-test")
        );
        testTask = new Task(
                testTaskId,
                newTask.name(),
                newTask.description(),
                newTask.dueDate(),
                newTask.status(),
                newTask.priority(),
                newTask.tags()
        );
    }

    @Test
    void createTask_ValidTask_ReturnsCreatedTask() throws Exception {
        Task newTask = Task.createNew(
                "New Task",
                "New Description",
                LocalDateTime.now().plusDays(1),
                TaskPriority.LOW,
                Set.of("new", "task")
        );

        UUID createdId = UUID.randomUUID();
        Task createdTask = new Task(
                createdId,
                newTask.name(),
                newTask.description(),
                newTask.dueDate(),
                newTask.status(),
                newTask.priority(),
                newTask.tags()
        );

        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdTask.id().toString()))
                .andExpect(jsonPath("$.name").value(createdTask.name()))
                .andExpect(jsonPath("$.description").value(createdTask.description()))
                .andExpect(jsonPath("$.status").value(createdTask.status().toString()))
                .andExpect(jsonPath("$.priority").value(createdTask.priority().toString()))
                .andExpect(jsonPath("$.tags", hasSize(2)));
    }

    @Test
    void createTask_InvalidTask_ReturnsBadRequest() throws Exception {
        Task invalidTask = Task.createNew(
                "",  // Invalid empty title
                "Description",
                LocalDateTime.now().plusDays(1),
                TaskPriority.LOW,
                Set.of("test")
        );

        doThrow(new InvalidTaskException("Title cannot be empty"))
                .when(taskService).createTask(any(Task.class));

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Title cannot be empty"));
    }

    @Test
    void getTask_ExistingTask_ReturnsTask() throws Exception {
        when(taskService.getTaskById(testTaskId)).thenReturn(testTask);

        mockMvc.perform(get("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testTaskId.toString())))
                .andExpect(jsonPath("$.name", is("Test Task")));
    }

    @Test
    void getTask_NonExistingTask_ReturnsNotFound() throws Exception {
        when(taskService.getTaskById(testTaskId))
                .thenThrow(new TaskNotFoundException(testTaskId));

        mockMvc.perform(get("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_ExistingTask_ReturnsUpdatedTask() throws Exception {
        Task newTask = Task.createNew(
                "Updated Task",
                "Updated Description",
                testTask.dueDate(),
                TaskPriority.HIGH,
                Set.of("updated", "test")
        );
        
        Task updatedTask = new Task(
                testTaskId,
                newTask.name(),
                newTask.description(),
                newTask.dueDate(),
                TaskStatus.IN_PROGRESS,
                newTask.priority(),
                newTask.tags()
        );

        when(taskService.updateTask(eq(testTaskId), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/v1/tasks/{taskId}", testTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Task")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    void deleteTask_ExistingTask_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_NonExistingTask_ReturnsNotFound() throws Exception {
        doThrow(new TaskNotFoundException(testTaskId))
                .when(taskService).deleteTask(testTaskId);

        mockMvc.perform(delete("/api/v1/tasks/{taskId}", testTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTasks_ValidParameters_ReturnsPagedResponse() throws Exception {
        Page<Task> page = new Page<>(
                new ArrayList<>(Set.of(testTask)),
                1L,
                1,
                10,
                0
        );

        when(taskService.getTasks(any(), any(), any(), any(), eq(0), eq(10), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Task")));
    }

    @Test
    void getTasks_InvalidPageSize_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "0")
                        .param("size", "1001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTasks_WithFilters_ReturnsFilteredTasks() throws Exception {
        Task filteredTask = Task.createNew(
                "Filtered Task",
                "Filtered Description",
                LocalDateTime.now().plusDays(1),
                TaskPriority.HIGH,
                Set.of("filtered", "important")
        );

        Page<Task> page = new Page<>(
                new ArrayList<>(Set.of(filteredTask)),
                1L,
                1,
                10,
                0
        );

        when(taskService.getTasks(
                eq("Filtered"),
                eq(TaskStatus.NOT_STARTED),
                eq(TaskPriority.HIGH),
                any(),
                eq(0),
                eq(10),
                any()
        )).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks")
                        .param("search", "Filtered")
                        .param("status", "NOT_STARTED")
                        .param("priority", "HIGH")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Filtered Task")))
                .andExpect(jsonPath("$.content[0].status", is("NOT_STARTED")))
                .andExpect(jsonPath("$.content[0].priority", is("HIGH")));
    }
}
