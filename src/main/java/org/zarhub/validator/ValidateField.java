package org.zarhub.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidateFieldValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateField {

    String message() default "validation.FieldNotValid.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entityClass();

    String fieldName();
}
