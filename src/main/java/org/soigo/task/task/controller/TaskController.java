package org.soigo.task.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.soigo.task.shared.handler.dto.ErrorMessage;
import org.soigo.task.user.model.User;
import org.soigo.task.task.dto.request.OnlyStatusRequest;
import org.soigo.task.task.dto.request.TaskCreateRequest;
import org.soigo.task.task.dto.request.TaskUpdateRequest;
import org.soigo.task.task.dto.response.TaskResponse;
import org.soigo.task.task.model.Priority;
import org.soigo.task.task.model.Status;
import org.soigo.task.task.model.Task;
import org.soigo.task.task.permission.annotation.IsAuthor;
import org.soigo.task.task.permission.annotation.IsAuthorOrExecutor;
import org.soigo.task.task.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class TaskController {
    final TaskService taskService;
    final ModelMapper mapper;

    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Tag(name = "Admin")
    @Operation(
            summary = "Создание новой задачи",
            description = "Создать задачу может только администратор, при создании указывается id исполнителя, у созданной задачи автоматически проставляется статус (IN_WAITING)"
    )
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Validated @NotNull TaskCreateRequest taskRequest,
            @AuthenticationPrincipal User user
    ) {
        Task task = mapper.map(taskRequest, Task.class);
        return new ResponseEntity<>(
                mapper.map(taskService.createTask(task, user), TaskResponse.class),
                HttpStatus.CREATED
        );
    }

    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Tag(name = "Admin")
    @Operation(
            summary = "Обновление данных задачи по ее id",
            description = "Обновить данные может только администратор, который создавал эту задачу"
    )
    @PatchMapping("{taskId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @IsAuthor
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @RequestBody @Validated @NotNull TaskUpdateRequest taskRequest,
            @AuthenticationPrincipal User ignoredUser
    ) {
        Task task = mapper.map(taskRequest, Task.class);
        return ResponseEntity.ok(
                mapper.map(taskService.updateTask(taskId, task), TaskResponse.class)
        );
    }

    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Tag(name = "Admin")
    @Operation(
            summary = "Удаление задачи по ее id",
            description = "Удалить задачу может только администратор, который создавал эту задачу"
    )
    @DeleteMapping("{taskId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @IsAuthor
    public ResponseEntity<?> deleteTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal User ignoredUser
    ) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(
            summary = "Изменение статуса задачи по ее id",
            description = "Изменить статус задачи может только исполнитель этой задачи или администратор, который ее создал"
    )
    @PatchMapping("/change-status/{taskId}")
    @IsAuthorOrExecutor
    public ResponseEntity<TaskResponse> changeStatusTask(
            @PathVariable UUID taskId,
            @RequestBody @Validated @NotNull OnlyStatusRequest onlyStatusRequest,
            @AuthenticationPrincipal User ignoredUser
    ) {
        Task task = mapper.map(onlyStatusRequest, Task.class);
        return ResponseEntity.ok(
                mapper.map(taskService.updateTask(taskId, task), TaskResponse.class)
        );
    }

    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(
            summary = "Получение задачи по ее id"
    )
    @GetMapping("{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(
                mapper.map(taskService.findTaskById(taskId), TaskResponse.class)
        );
    }

    @Operation(
            summary = "Получение списка задач",
            description = "Если фильтры не указаны, они не применяются. " +
                    "Можно получать задачи конкретного автора или исполнителя и т.д. " +
                    "По умолчанию сортировка задач по дате их создания"
    )
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) UUID authorId,
            @RequestParam(required = false) UUID executorId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "false") Boolean reverse
    ) {
        Page<Task> tasks = taskService.getFilteredTasks(
                priority,
                status,
                authorId,
                executorId,
                page,
                size,
                sortBy,
                reverse
        );

        return ResponseEntity.ok(
                tasks.map(task -> mapper.map(task, TaskResponse.class))
        );
    }
}
