package com.grabduck.taskmanager.service;

import com.grabduck.taskmanager.domain.Task;
import com.grabduck.taskmanager.domain.TaskPriority;
import com.grabduck.taskmanager.domain.TaskStatus;
import com.grabduck.taskmanager.domain.Page;
import com.grabduck.taskmanager.domain.SortOption;
import com.grabduck.taskmanager.dto.CreateTaskRequest;
import com.grabduck.taskmanager.exception.InvalidTaskException;
import com.grabduck.taskmanager.exception.TaskNotFoundException;
import com.grabduck.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    public Task createTask(CreateTaskRequest request) {
        try {
            validateCreateTaskRequest(request);
            Task task = Task.createNew(
                    request.name(),
                    request.description(),
                    request.dueDate(),
                    request.priority(),
                    request.tags()
            );
            return taskRepository.save(task);
        } catch (Exception e) {
            log.error("Failed to create task: {}", request, e);
            throw new RuntimeException("Failed to create task due to database error", e);
        }
    }

    public Task getTaskById(UUID taskId) {
        try {
            if (taskId == null) {
                throw new InvalidTaskException("Task ID cannot be null");
            }
            return taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));
        } catch (Exception e) {
            log.error("Failed to get task with id: {}", taskId, e);
            throw new RuntimeException("Failed to get task due to database error", e);
        }
    }

    public Task updateTask(UUID taskId, Task task) {
        try {
            if (taskId == null) {
                throw new InvalidTaskException("Task ID cannot be null");
            }
            validateTask(task);
            
            // Verify task exists
            if (!taskRepository.findById(taskId).isPresent()) {
                throw new TaskNotFoundException(taskId);
            }
            
            return taskRepository.save(task);
        } catch (Exception e) {
            log.error("Failed to update task with id: {}", taskId, e);
            throw new RuntimeException("Failed to update task due to database error", e);
        }
    }

    public void deleteTask(UUID taskId) {
        try {
            if (taskId == null) {
                throw new InvalidTaskException("Task ID cannot be null");
            }
            
            // Verify task exists before deletion
            if (!taskRepository.findById(taskId).isPresent()) {
                throw new TaskNotFoundException(taskId);
            }
            
            taskRepository.deleteById(taskId);
        } catch (Exception e) {
            log.error("Failed to delete task with id: {}", taskId, e);
            throw new RuntimeException("Failed to delete task due to database error", e);
        }
    }

    public Page<Task> getTasks(
            String search,
            TaskStatus status,
            TaskPriority priority,
            String tag,
            int page,
            int size,
            SortOption sortOption
    ) {
        try {
            return taskRepository.findTasks(search, status, priority, tag, page, size, sortOption);
        } catch (Exception e) {
            log.error("Failed to find tasks with filters: search={}, status={}, priority={}, tag={}", search, status, priority, tag, e);
            throw new RuntimeException("Failed to find tasks due to database error", e);
        }
    }

    private void validateTask(Task task) {
        if (task == null) {
            throw new InvalidTaskException("Task cannot be null");
        }
        if (task.name() == null || task.name().trim().isEmpty()) {
            throw new InvalidTaskException("Task name cannot be empty");
        }
        if (task.name().length() > 255) {
            throw new InvalidTaskException("Task name cannot be longer than 255 characters");
        }
        if (task.description() != null && task.description().length() > 1000) {
            throw new InvalidTaskException("Task description cannot be longer than 1000 characters");
        }
        if (task.tags() == null || task.tags().isEmpty()) {
            throw new InvalidTaskException("Task must have at least one tag");
        }
        if (task.tags().size() > 10) {
            throw new InvalidTaskException("Task cannot have more than 10 tags");
        }
        if (task.dueDate() != null && task.dueDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTaskException("Task due date cannot be in the past");
        }
    }

    private void validateCreateTaskRequest(CreateTaskRequest request) {
        if (request == null) {
            throw new InvalidTaskException("Task request cannot be null");
        }
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new InvalidTaskException("Task name cannot be empty");
        }
        if (request.name().length() > 255) {
            throw new InvalidTaskException("Task name cannot be longer than 255 characters");
        }
        if (request.description() != null && request.description().length() > 1000) {
            throw new InvalidTaskException("Task description cannot be longer than 1000 characters");
        }
        if (request.tags() == null || request.tags().isEmpty()) {
            throw new InvalidTaskException("Task must have at least one tag");
        }
    }
}
