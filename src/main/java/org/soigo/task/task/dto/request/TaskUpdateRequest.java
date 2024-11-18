package org.soigo.task.task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;
import org.soigo.task.shared.dto.UserWithId;
import org.soigo.task.task.dto.validation.annotation.EnumValue;
import org.soigo.task.task.model.Priority;
import org.soigo.task.task.model.Status;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class TaskUpdateRequest {

    @Schema(
            description = "Заголовок",
            example = "Разработка микросервисного приложения"
    )
    private String header;

    @Schema(
            description = "Описание",
            example = "Разработка микросервисного приложения на spring cloud"
    )
    private String description;

    @Schema(
            description = "Приоритет",
            allowableValues = {"HIGH", "MEDIUM", "LOW"},
            defaultValue = "LOW",
            example = "LOW"
    )
    @EnumValue(enumClass = Priority.class)
    private String priority;

    @Schema(
            description = "Статус",
            allowableValues = {"IN_WAITING", "IN_PROGRESS", "COMPLETED"},
            defaultValue = "IN_PROGRESS",
            example = "IN_PROGRESS"
    )
    @EnumValue(enumClass = Status.class)
    private String status;

    @Schema(description = "Исполнитель")
    @Valid
    private UserWithId executor;
}
