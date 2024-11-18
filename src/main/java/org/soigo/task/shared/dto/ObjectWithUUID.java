package org.soigo.task.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.UUID;


@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
@Getter
@Setter
@ToString
public class ObjectWithUUID {

    @Schema(
            description = "UUID",
            format = "UUID",
            example = "a87c2b46-0f86-4e9b-9ef4-c9a00f6186a6"
    )
    @NotBlank
    @NotNull
    @UUID
    protected String id;
}
