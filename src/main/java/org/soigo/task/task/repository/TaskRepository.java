package org.soigo.task.task.repository;

import org.soigo.task.task.model.Priority;
import org.soigo.task.task.model.Status;
import org.soigo.task.task.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    boolean existsByIdAndAuthorId(@Param("taskId") UUID taskId, @Param("userId") UUID userId);
    boolean existsByIdAndExecutorId(@Param("taskId") UUID taskId, @Param("userId") UUID userId);

    @Query(value = "SELECT t FROM Task t WHERE " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:authorId IS NULL OR t.author.id = :authorId) AND " +
            "(:executorId IS NULL OR t.executor.id = :executorId)")
    Page<Task> findByPriorityAndStatusAndAuthorIdAndExecutorId(
            @Param("priority") Priority priority,
            @Param("status") Status status,
            @Param("authorId") UUID authorId,
            @Param("executorId") UUID executorId,
            Pageable pageable
    );


}
