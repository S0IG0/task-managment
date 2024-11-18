package org.soigo.task.task.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.soigo.task.task.dto.validation.annotation.EnumValue;
import org.soigo.task.task.model.Status;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class OnlyStatusRequest {
    @Schema(
            description = "Статус",
            allowableValues = {"IN_WAITING", "IN_PROGRESS", "COMPLETED"},
            defaultValue = "IN_PROGRESS",
            example = "IN_PROGRESS"
    )
    @NotNull
    @NotBlank
    @EnumValue(enumClass = Status.class)
    private String status;
}
