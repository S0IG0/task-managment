package org.soigo.task.task.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.soigo.task.shared.dto.ObjectWithUUID;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class TaskResponse extends ObjectWithUUID {

    @Schema(description = "Исполнитель")
    private ObjectWithUUID executor;

    @Schema(description = "Автор")
    private ObjectWithUUID author;

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
    private String priority;

    @Schema(
            description = "Статус",
            allowableValues = {"IN_WAITING", "IN_PROGRESS", "COMPLETED"},
            defaultValue = "IN_PROGRESS",
            example = "IN_PROGRESS"
    )
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
