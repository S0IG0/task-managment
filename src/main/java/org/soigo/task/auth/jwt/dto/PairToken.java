package org.soigo.task.auth.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class PairToken {
    @Schema(description = "Access JWT")
    private String access;
    @Schema(description = "Refresh JWT")
    private String refresh;
}
