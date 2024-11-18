package org.soigo.task.task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.soigo.task.shared.dto.UserWithId;
import org.soigo.task.task.dto.validation.annotation.EnumValue;
import org.soigo.task.task.model.Priority;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class TaskCreateRequest {

    @Schema(
            description = "Заголовок",
            example = "Разработка микросервисного приложения"
    )
    @NotBlank
    @NotNull
    private String header;

    @Schema(
            description = "Описание",
            example = "Разработка микросервисного приложения на spring cloud"
    )
    @NotBlank
    @NotNull
    private String description;

    @Schema(
            description = "Приоритет",
            allowableValues = {"HIGH", "MEDIUM", "LOW"},
            defaultValue = "LOW",
            example = "LOW"
    )
    @NotNull
    @NotBlank
    @EnumValue(enumClass = Priority.class)
    private String priority;

    @Schema(description = "Исполнитель")
    @NotNull
    @Valid
    private UserWithId executor;
}
