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
public class CommentResponse extends ObjectWithUUID {

    @Schema(description = "Задача")
    private ObjectWithUUID task;

    @Schema(description = "Автор комментария")
    private ObjectWithUUID author;

    @Schema(
            description = "Комментарий к задаче",
            example = "Настроить pipeline  для микросервисного приложения"
    )
    private String text;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
