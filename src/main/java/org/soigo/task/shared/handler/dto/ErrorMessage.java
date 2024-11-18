package org.soigo.task.shared.handler.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ErrorMessage {
    private Object error;
}
