package org.soigo.task.task.service;

import org.soigo.task.user.model.User;
import org.soigo.task.task.model.Comment;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CommentService {
    Comment addCommentToTaskById(Comment comment, UUID taskId, User author);
    Page<Comment> getFilteredCommentsByTaskId(
            UUID taskId,
            Integer page,
            Integer size,
            String sortBy,
            Boolean reverse
    );
}
