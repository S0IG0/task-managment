package org.soigo.task.task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CommentCreateRequest {

    @Schema(
            description = "Комментарий к задаче",
            example = "Настроить pipeline  для микросервисного приложения"
    )
    @NotBlank
    @NotNull
    private String text;
}
