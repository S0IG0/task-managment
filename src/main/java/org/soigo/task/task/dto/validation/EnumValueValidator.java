package org.soigo.task.task.dto.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jetbrains.annotations.NotNull;
import org.soigo.task.task.dto.validation.annotation.EnumValue;

import java.util.Arrays;
import java.util.List;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(@NotNull EnumValue annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<String> values = Arrays.stream(enumClass.getEnumConstants()).map(Object::toString).toList();

        if (values.contains(value)) {
            return true;
        }

        String allowedValues = String.join(",", values);
        String errorMessage = String.format("The value must match one of the following options: {%s}", allowedValues);

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation();
        return false;
    }
}
