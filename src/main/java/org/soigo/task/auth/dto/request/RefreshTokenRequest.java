package org.soigo.task.auth.dto.request;

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
public class RefreshTokenRequest {

    @Schema(description = "Refresh JWT")
    @NotBlank
    @NotNull
    private String refresh;
}
