package org.soigo.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.soigo.task.task.controller.TaskController;
import org.soigo.task.task.dto.request.OnlyStatusRequest;
import org.soigo.task.task.dto.request.TaskCreateRequest;
import org.soigo.task.task.dto.request.TaskUpdateRequest;
import org.soigo.task.task.dto.response.TaskResponse;
import org.soigo.task.task.service.TaskService;
import org.soigo.task.task.model.Task;
import org.soigo.task.user.model.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    TaskService taskService;

    @Mock
    ModelMapper mapper;

    @InjectMocks
    TaskController taskController;

    Task task;
    TaskResponse taskResponse;
    TaskCreateRequest taskCreateRequest;
    TaskUpdateRequest taskUpdateRequest;
    OnlyStatusRequest onlyStatusRequest;
    User user;

    @BeforeEach
    void setUp() {
        task = new Task();
        taskResponse = new TaskResponse();
        taskCreateRequest = new TaskCreateRequest();
        taskUpdateRequest = new TaskUpdateRequest();
        onlyStatusRequest = new OnlyStatusRequest();
        user = new User();

        taskCreateRequest.setHeader("Task 1");
        taskCreateRequest.setDescription("Description 1");
        taskUpdateRequest.setHeader("Updated Task");
        taskUpdateRequest.setDescription("Updated Description");

        taskResponse.setId(UUID.randomUUID().toString());
        taskResponse.setHeader("Task 1");
        taskResponse.setDescription("Description 1");

        onlyStatusRequest.setStatus("IN_WAITING");

        onlyStatusRequest.setStatus("IN_PROGRESS");

        taskResponse.setStatus("IN_PROGRESS");
    }

    @Test
    void createTask_validRequest_returnsCreated() {
        when(mapper.map(taskCreateRequest, Task.class)).thenReturn(task);
        when(taskService.createTask(task, user)).thenReturn(task);
        when(mapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        var result = taskController.createTask(taskCreateRequest, user);

        verify(taskService, times(1)).createTask(task, user);
        assertEquals(CREATED, result.getStatusCode());
        assertEquals(taskResponse, result.getBody());
    }

    @Test
    void updateTask_validRequest_returnsOK() {
        UUID taskId = UUID.randomUUID();
        when(mapper.map(taskUpdateRequest, Task.class)).thenReturn(task);
        when(taskService.updateTask(taskId, task)).thenReturn(task);
        when(mapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        var result = taskController.updateTask(taskId, taskUpdateRequest, user);

        verify(taskService, times(1)).updateTask(taskId, task);
        assertEquals(OK, result.getStatusCode());
        assertEquals(taskResponse, result.getBody());
    }

    @Test
    void changeStatusTask_validRequest_returnsOK() {
        UUID taskId = UUID.randomUUID();
        when(mapper.map(onlyStatusRequest, Task.class)).thenReturn(task);
        when(taskService.updateTask(taskId, task)).thenReturn(task);
        when(mapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        var result = taskController.changeStatusTask(taskId, onlyStatusRequest, user);

        verify(taskService, times(1)).updateTask(taskId, task);
        assertEquals(OK, result.getStatusCode());
        assertEquals(taskResponse, result.getBody());
    }

    @Test
    void deleteTask_validTaskId_returnsNoContent() {
        UUID taskId = UUID.randomUUID();

        var result = taskController.deleteTask(taskId, user);

        verify(taskService, times(1)).deleteTask(taskId);
        assertEquals(NO_CONTENT, result.getStatusCode());
    }


    @Test
    void getTask_validTaskId_returnsOK() {
        UUID taskId = UUID.randomUUID();
        when(taskService.findTaskById(taskId)).thenReturn(task);
        when(mapper.map(task, TaskResponse.class)).thenReturn(taskResponse);

        var result = taskController.getTask(taskId);

        verify(taskService, times(1)).findTaskById(taskId);
        assertEquals(OK, result.getStatusCode());
        assertEquals(taskResponse, result.getBody());
    }

    @Test
    void getTasks_validRequest_returnsOK() {
        when(taskService.getFilteredTasks(
                null,
                null,
                null,
                null,
                0,
                10,
                "createdAt",
                false)
        ).thenReturn(Page.empty());
        var result = taskController.getTasks(
                null,
                null,
                null,
                null,
                0, 10,
                "createdAt",
                false
        );

        assertEquals(OK, result.getStatusCode());
    }

    @Test
    void createTask_invalidRequest_returnsBadRequest() {
        when(mapper.map(taskCreateRequest, Task.class)).thenThrow(new IllegalArgumentException("Invalid task"));

        var exception = assertThrows(IllegalArgumentException.class, () ->
                taskController.createTask(taskCreateRequest, user));

        assertEquals("Invalid task", exception.getMessage());
    }
}
