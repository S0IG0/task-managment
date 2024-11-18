package org.soigo.task.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import org.soigo.task.shared.validation.annotation.ConstraintByClass;
import org.soigo.task.user.service.UserService;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserWithId {
    @Schema(
            description = "UUID",
            format = "UUID",
            example = "a87c2b46-0f86-4e9b-9ef4-c9a00f6186a6"
    )
    @NotNull
    @NotBlank
    @UUID
    @ConstraintByClass(
            supportClass = UserService.class,
            method = "existsById",
            message = "not found",
            invert = true
    )
    private String id;
}
