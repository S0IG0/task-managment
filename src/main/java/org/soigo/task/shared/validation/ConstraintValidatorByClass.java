package org.soigo.task.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.soigo.task.shared.validation.annotation.ConstraintByClass;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class ConstraintValidatorByClass implements ConstraintValidator<ConstraintByClass, String> {

    private final ApplicationContext applicationContext;

    private Class<?> supportClass;
    private String methodName;
    private boolean invert;

    @Override
    public void initialize(@NotNull ConstraintByClass constraintAnnotation) {
        this.supportClass = constraintAnnotation.supportClass();
        this.methodName = constraintAnnotation.method();
        this.invert = constraintAnnotation.invert();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Object repository = applicationContext.getBean(supportClass);

        try {
            Method method = supportClass.getMethod(methodName, String.class);
            Object result = method.invoke(repository, value);

            if (result instanceof Boolean) {
                if (invert) {
                    return (Boolean) result;
                } else {
                    return !(Boolean) result;
                }
            }

            throw new IllegalStateException("The repository method must return a boolean");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to execute repository method", e);
        }
    }
}

