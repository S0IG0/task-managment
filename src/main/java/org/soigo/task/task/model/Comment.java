package org.soigo.task.task.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.soigo.task.shared.model.BaseModel;
import org.soigo.task.user.model.User;

@Entity
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Comment extends BaseModel {
    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Task task;
}
