package org.soigo.task.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.soigo.task.shared.handler.dto.ErrorMessage;
import org.soigo.task.user.model.User;
import org.soigo.task.task.dto.request.CommentCreateRequest;
import org.soigo.task.task.dto.response.CommentResponse;
import org.soigo.task.task.model.Comment;
import org.soigo.task.task.permission.annotation.IsAuthorOrExecutor;
import org.soigo.task.task.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/task")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class CommentController {
    final CommentService commentService;
    final ModelMapper mapper;

    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(
            summary = "Добавление комментария к задаче",
            description = "Добавить комментарий к задаче может только исполнитель этой задачи или администратор, который создавал эту задачу"
    )
    @PostMapping("{taskId}/comment")
    @IsAuthorOrExecutor
    public ResponseEntity<CommentResponse> addCommentToTask(
            @PathVariable UUID taskId,
            @RequestBody @Validated @NotNull CommentCreateRequest commentCreateRequest,
            @AuthenticationPrincipal User author
    ) {
        Comment comment = mapper.map(commentCreateRequest, Comment.class);

        return new ResponseEntity<>(
                mapper.map(commentService.addCommentToTaskById(comment, taskId, author), CommentResponse.class),
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Получение списка комментариев к задаче",
            description = "По умолчанию отсортировывает комментарии по дате их создания"
    )
    @GetMapping("{taskId}/comment")
    public ResponseEntity<Page<CommentResponse>> getCommentsForTask(
            @PathVariable UUID taskId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "false") Boolean reverse
    ) {
        Page<Comment> comments = commentService.getFilteredCommentsByTaskId(
                taskId,
                page,
                size,
                sortBy,
                reverse
        );

        return ResponseEntity.ok(
                comments.map(comment -> mapper.map(comment, CommentResponse.class))
        );
    }
}
