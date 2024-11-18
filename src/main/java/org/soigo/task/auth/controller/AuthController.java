package org.soigo.task.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.soigo.task.shared.handler.dto.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.soigo.task.auth.dto.request.CredentialRequest;
import org.soigo.task.auth.dto.request.RefreshTokenRequest;
import org.soigo.task.auth.dto.request.UserRequest;
import org.soigo.task.auth.jwt.dto.PairToken;
import org.soigo.task.user.model.User;
import org.soigo.task.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class AuthController {
    final AuthService authService;
    final ModelMapper mapper;

    @Value("${jwt.header.start}")
    String jwtHeaderStart;

    @Operation(
            summary = "Регистрация новых пользователей",
            description = "Данный endpoint используется для регистрации новых пользователей в системе с ролью ROLE_USER"
    )
    @PostMapping("register")
    public ResponseEntity<PairToken> register(@RequestBody @Validated @NotNull UserRequest userRequest) {
        User user = mapper.map(userRequest, User.class);
        PairToken pairToken = authService.register(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pairToken);
    }

    @Operation(
            summary = "Вход для пользователей",
            description = "При успешном входе вы получаете пару JWT токенов (access, refresh)"
    )
    @PostMapping("login")
    public ResponseEntity<PairToken> login(@RequestBody @Validated @NotNull CredentialRequest credentialRequest) {
        PairToken pairToken = authService.login(credentialRequest.getEmail(), credentialRequest.getPassword());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pairToken);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(
            summary = "Получение новой пары токенов по refresh токену",
            description = "При успешном входе вы получаете пару JWT токенов (access, refresh), при этом прошлая пара токенов становится неактуальной"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("refresh-pair-token")
    public ResponseEntity<PairToken> refreshPairToken(@RequestBody @Validated @NotNull RefreshTokenRequest refreshTokenRequest) {
        PairToken pairToken = authService.updatePairTokenByRefreshToken(refreshTokenRequest.getRefresh());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pairToken);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(
            summary = "Выход для текущего устройства",
            description = "Делает текущую пару токенов неактуальной"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("logout-current-device")
    public ResponseEntity<?> logoutCurrentDevice(@Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) @NotNull String authorization) {
        authService.logoutCurrentDevice(authorization.replace(jwtHeaderStart + " ", ""));
        return ResponseEntity
                .ok()
                .build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(
            summary = "Выход на всех устройствах",
            description = "Делает все выпущенные токены для пользователя неактуальными"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("logout-all-device")
    public ResponseEntity<?> logoutAllDevice(@Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) @NotNull String authorization) {
        authService.logoutAllDevice(authorization.replace(jwtHeaderStart + " ", ""));
        return ResponseEntity
                .ok()
                .build();
    }
}
