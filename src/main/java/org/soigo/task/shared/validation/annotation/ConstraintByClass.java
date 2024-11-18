package org.soigo.task.shared.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.soigo.task.shared.validation.ConstraintValidatorByClass;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConstraintValidatorByClass.class)
@Documented
public @interface ConstraintByClass {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean invert() default false;
    Class<?> supportClass();
    String method();
}
