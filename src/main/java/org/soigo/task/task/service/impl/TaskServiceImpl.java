package org.soigo.task.task.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.soigo.task.user.model.User;
import org.soigo.task.task.exception.TaskNotFoundException;
import org.soigo.task.task.model.Priority;
import org.soigo.task.task.model.Status;
import org.soigo.task.task.model.Task;
import org.soigo.task.task.repository.TaskRepository;
import org.soigo.task.task.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    final TaskRepository taskRepository;

    @Override
    public Task createTask(@NotNull Task task, User author) {
        task.setAuthor(author);
        task.setStatus(Status.IN_WAITING);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(UUID taskId, @NotNull Task task) {
        Task findTask = findTaskById(taskId);

        updateIfNotNull(task.getHeader(), findTask::setHeader);
        updateIfNotNull(task.getDescription(), findTask::setDescription);
        updateIfNotNull(task.getPriority(), findTask::setPriority);
        updateIfNotNull(task.getStatus(), findTask::setStatus);
        updateIfNotNull(task.getExecutor(), findTask::setExecutor);

        return taskRepository.save(findTask);
    }

    @Override
    public Task findTaskById(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException("Task with id " + taskId + " not found")
        );
    }

    @Override
    public Boolean existsByIdAndAuthorId(UUID taskId, @NotNull User user) {
        existsById(taskId);
        return taskRepository.existsByIdAndAuthorId(taskId, user.getId());
    }

    @Override
    public Boolean existsByIdAndExecutorId(UUID taskId, @NotNull User user) {
        existsById(taskId);
        return taskRepository.existsByIdAndExecutorId(taskId, user.getId());
    }

    @Override
    public Boolean existsById(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("Task with id " + taskId + " not found");
        }
        return true;
    }

    @Override
    public void deleteTask(UUID taskId) {
        existsById(taskId);
        taskRepository.deleteById(taskId);
    }

    @Override
    public Page<Task> getFilteredTasks(
            Priority priority,
            Status status,
            UUID authorId,
            UUID executorId,
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

        return taskRepository.findByPriorityAndStatusAndAuthorIdAndExecutorId(
                priority,
                status,
                authorId,
                executorId,
                pageable
        );
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
