package org.soigo.task.task.permission.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.soigo.task.user.model.User;
import org.soigo.task.task.service.TaskService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class TaskPermissionAspect {
    final TaskService taskService;

    @Around("@annotation(org.soigo.task.task.permission.annotation.IsAuthor)")
    public Object checkAuthor(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] extractedArgs = getArguments(joinPoint);
        UUID tskId = (UUID) extractedArgs[0];
        User user = (User) extractedArgs[1];

        if (!taskService.existsByIdAndAuthorId(tskId, user)) {
            throw new AccessDeniedException("You are not the author of this task");
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(org.soigo.task.task.permission.annotation.IsExecutor)")
    public Object checkExecutor(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] extractedArgs = getArguments(joinPoint);
        UUID tskId = (UUID) extractedArgs[0];
        User user = (User) extractedArgs[1];

        if (!taskService.existsByIdAndExecutorId(tskId, user)) {
            throw new AccessDeniedException("You are not the executor of this task");
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(org.soigo.task.task.permission.annotation.IsAuthorOrExecutor)")
    public Object checkAuthorOrExecutor(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] extractedArgs = getArguments(joinPoint);
        UUID tskId = (UUID) extractedArgs[0];
        User user = (User) extractedArgs[1];

        if ((!taskService.existsByIdAndExecutorId(tskId, user)) & (!taskService.existsByIdAndAuthorId(tskId, user))) {
            throw new AccessDeniedException("You are not the executor or author of this task");
        }

        return joinPoint.proceed();
    }

    @Contract("_ -> new")
    private Object @NotNull [] getArguments(@NotNull ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID tskId = null;
        User user = null;

        for (Object arg : args) {
            if (arg instanceof UUID) {
                tskId = (UUID) arg;
            } else if (arg instanceof User) {
                user = (User) arg;
            }
        }
        return new Object[]{tskId, user};
    }
}
