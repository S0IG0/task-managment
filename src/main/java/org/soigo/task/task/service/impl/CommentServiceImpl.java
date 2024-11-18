package org.soigo.task.task.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.soigo.task.user.model.User;
import org.soigo.task.task.model.Comment;
import org.soigo.task.task.model.Task;
import org.soigo.task.task.repository.CommentRepository;
import org.soigo.task.task.service.CommentService;
import org.soigo.task.task.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    final TaskService taskService;
    final CommentRepository commentRepository;


    @Override
    public Comment addCommentToTaskById(@NotNull Comment comment, UUID taskId, User author) {
        taskService.existsById(taskId);

        comment.setTask(
                Task
                        .builder()
                        .id(taskId)
                        .build()
        );
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    @Override
    public Page<Comment> getFilteredCommentsByTaskId(
            UUID taskId,
            Integer page,
            Integer size,
            String sortBy,
            @NotNull Boolean reverse
    ) {
        Sort sort = Sort.by(sortBy);

        if (reverse) {
            sort = sort.reverse();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        return commentRepository.findByTaskId(taskId, pageable);
    }
}
