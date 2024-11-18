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
import org.soigo.task.task.controller.CommentController;
import org.soigo.task.task.dto.request.CommentCreateRequest;
import org.soigo.task.task.dto.response.CommentResponse;
import org.soigo.task.task.model.Comment;
import org.soigo.task.task.service.CommentService;
import org.soigo.task.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.UUID;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    CommentService commentService;

    @Mock
    ModelMapper mapper;

    @InjectMocks
    CommentController commentController;

    CommentCreateRequest commentCreateRequest;
    Comment comment;
    CommentResponse commentResponse;
    User user;
    UUID taskId;
    Page<Comment> commentPage;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        commentCreateRequest = new CommentCreateRequest("Test comment");
        comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setText("Test comment");

        commentResponse = new CommentResponse();
        commentResponse.setId(comment.getId().toString());
        commentResponse.setText(comment.getText());

        user = new User();
        user.setId(UUID.randomUUID());

        commentPage = new PageImpl<>(Collections.singletonList(comment));
    }

    @Test
    void addCommentToTask_validRequest_returnsCreated() {
        // Arrange
        when(mapper.map(commentCreateRequest, Comment.class)).thenReturn(comment);
        when(commentService.addCommentToTaskById(comment, taskId, user)).thenReturn(comment);
        when(mapper.map(comment, CommentResponse.class)).thenReturn(commentResponse);

        // Act
        ResponseEntity<CommentResponse> result = commentController.addCommentToTask(taskId, commentCreateRequest, user);

        // Assert
        assertEquals(CREATED, result.getStatusCode());
        assertEquals(commentResponse, result.getBody());
        verify(commentService, times(1)).addCommentToTaskById(comment, taskId, user);
    }


    @Test
    void getCommentsForTask_validRequest_returnsOk() {
        // Arrange
        when(commentService.getFilteredCommentsByTaskId(taskId, 0, 10, "createdAt", false))
                .thenReturn(commentPage);
        when(mapper.map(comment, CommentResponse.class)).thenReturn(commentResponse);

        // Act
        ResponseEntity<Page<CommentResponse>> result = commentController
                .getCommentsForTask(taskId, 0, 10, "createdAt", false);

        // Assert
        assertEquals(OK, result.getStatusCode());
        assertEquals(1, Objects.requireNonNull(result.getBody()).getContent().size());
        assertEquals(commentResponse, result.getBody().getContent().get(0));
    }

    @Test
    void getCommentsForTask_noComments_returnsOkWithEmptyList() {
        // Arrange
        Page<Comment> emptyPage = new PageImpl<>(Collections.emptyList());
        when(commentService.getFilteredCommentsByTaskId(taskId, 0, 10, "createdAt", false))
                .thenReturn(emptyPage);

        // Act
        ResponseEntity<Page<CommentResponse>> result = commentController
                .getCommentsForTask(taskId, 0, 10, "createdAt", false);

        // Assert
        assertEquals(OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).getContent().isEmpty());
    }
}
