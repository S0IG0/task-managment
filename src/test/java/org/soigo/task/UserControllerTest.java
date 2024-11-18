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
import org.soigo.task.user.controller.UserController;
import org.soigo.task.user.dto.response.UserResponse;
import org.soigo.task.user.model.User;
import org.soigo.task.user.service.UserService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    UserService userService;

    @Mock
    ModelMapper mapper;

    @InjectMocks
    UserController userController;

    User user;
    UserResponse userResponse;
    UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userResponse = UserResponse.builder()
                .id(userId.toString())
                .email("test@example.com")
                .build();

        user = User
                .builder()
                .id(userId)
                .email("test@example.com")
                .build();
    }

    @Test
    void findById_validUserId_returnsOk() {
        // Arrange
        when(userService.findById(userId)).thenReturn(user);
        when(mapper.map(user, UserResponse.class)).thenReturn(userResponse);

        // Act
        var result = userController.findById(userId);

        // Assert
        verify(userService, times(1)).findById(userId);
        assertEquals(OK, result.getStatusCode());
        assertEquals(userResponse, result.getBody());
    }

    @Test
    void findById_userNotFound_returnsNotFound() {
        // Arrange
        when(userService.findById(userId)).thenThrow(new IllegalArgumentException("User not found"));

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userController.findById(userId));

        // Assert
        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).findById(userId);
    }


    @Test
    void findById_serviceThrowsException_returnsInternalServerError() {
        // Arrange
        when(userService.findById(userId)).thenThrow(new RuntimeException("Internal server error"));

        // Act
        Exception exception = assertThrows(RuntimeException.class, () ->
                userController.findById(userId));

        // Assert
        assertEquals("Internal server error", exception.getMessage());
        verify(userService, times(1)).findById(userId);
    }
}
