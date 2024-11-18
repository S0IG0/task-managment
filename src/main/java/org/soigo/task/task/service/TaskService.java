package org.soigo.task.task.service;

import org.soigo.task.user.model.User;
import org.soigo.task.task.model.Priority;
import org.soigo.task.task.model.Status;
import org.soigo.task.task.model.Task;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TaskService {
    Task createTask(Task task, User author);
    Task updateTask(UUID taskId, Task task);
    Task findTaskById(UUID taskId);
    Boolean existsByIdAndAuthorId(UUID taskId, User user);
    Boolean existsByIdAndExecutorId(UUID taskId, User user);
    Boolean existsById(UUID taskId);
    void deleteTask(UUID taskId);
    Page<Task> getFilteredTasks(
            Priority priority,
            Status status,
            UUID authorId,
            UUID executorId,
            Integer page,
            Integer size,
            String sortBy,
            Boolean reverse
    );

}
