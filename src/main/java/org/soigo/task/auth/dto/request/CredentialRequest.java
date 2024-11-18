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
public class CredentialRequest {

    @Schema(
            description = "Почта пользователя",
            example = "admin@mail.ru"
    )
    @NotBlank
    @NotNull
    @Email
    @ConstraintByClass(
            supportClass = UserRepository.class,
            method = "existsByEmail",
            message = "not found",
            invert = true
    )
    private String email;

    @Schema(
            description = "Пароль",
            example = "password"
    )
    @NotBlank
    @NotNull
    private String password;
}
