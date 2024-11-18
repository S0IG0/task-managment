package org.soigo.task.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.soigo.task.shared.dto.ObjectWithUUID;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class UserResponse extends ObjectWithUUID {

    @Schema(
            description = "Username",
            example = "soigo"
    )
    private String username;

    @Schema(
            description = "Почта пользователя",
            example = "daniil.chibitock@yandex.ru"
    )
    private String email;

    @Schema(
            description = "Имя",
            example = "Даниил"
    )
    private String first_name;

    @Schema(
            description = "Фамилия",
            example = "Чибиток"
    )
    private String last_name;

    @Schema(description = "Список ролей")
    private List<String> roles;
}
