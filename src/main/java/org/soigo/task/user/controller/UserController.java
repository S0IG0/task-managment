package org.soigo.task.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.soigo.task.shared.handler.dto.ErrorMessage;
import org.soigo.task.user.dto.response.UserResponse;
import org.soigo.task.user.model.User;
import org.soigo.task.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешный запрос"),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class UserController {
    final UserService userService;
    final ModelMapper mapper;

    @Operation(
            summary = "Получение пользователя по id"
    )
    @GetMapping("{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID userId) {
        return ResponseEntity.ok(
                mapper.map(userService.findById(userId), UserResponse.class)
        );
    }

    @Operation(
            summary = "Получение списка всех пользователей"
    )
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "false") Boolean reverse
    ) {
        Page<User> users = userService.getFilteredUsers(
                page,
                size,
                sortBy,
                reverse
        );

        return ResponseEntity.ok(
                users.map(user -> mapper.map(user, UserResponse.class))
        );
    }
}
