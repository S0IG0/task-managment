package org.soigo.task.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.soigo.task.shared.validation.annotation.ConstraintByClass;
import org.soigo.task.user.repository.UserRepository;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserRequest {
    @Schema(
            description = "Username",
            example = "soigo"
    )
    @NotBlank
    @NotNull
    @ConstraintByClass(
            supportClass = UserRepository.class,
            method = "existsByUsername",
            message = "must be unique"
    )
    private String username;

    @Schema(
            description = "Почта пользователя",
            example = "daniil.chibitock@yandex.ru"
    )
    @NotBlank
    @NotNull
    @Email
    @ConstraintByClass(
            supportClass = UserRepository.class,
            method = "existsByEmail",
            message = "must be unique"
    )
    private String email;

    @Schema(
            description = "Пароль",
            example = "password"
    )
    @NotBlank
    @NotNull
    private String password;

    @Schema(
            description = "Имя",
            example = "Даниил"
    )
    @NotBlank
    @NotNull
    private String first_name;

    @Schema(
            description = "Фамилия",
            example = "Чибиток"
    )
    @NotBlank
    @NotNull
    private String last_name;
}
