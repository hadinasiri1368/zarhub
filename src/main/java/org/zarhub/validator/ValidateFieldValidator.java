package org.zarhub.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.zarhub.common.Utils;
import org.zarhub.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.List;

public class ValidateFieldValidator implements ConstraintValidator<ValidateField, Object> {
    private ValidateField annotation;
    private Class<?> entityClass;
    private String fieldName;
    private final JpaRepository jpaRepository;

    public ValidateFieldValidator(JpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void initialize(ValidateField constraintAnnotation) {
        this.annotation = constraintAnnotation;
        this.entityClass = constraintAnnotation.entityClass();
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean valid = true;
        if (Utils.isNull(value)) {
            return true;
        }
        List<?> list = jpaRepository.findAll(annotation.entityClass());
        try {
            for (Object entity : list) {
                Field field = getFieldFromClassHierarchy(entity.getClass(), fieldName);
                field.setAccessible(true);
                Object fieldValue = field.get(entity);
                if (!Utils.isNull(fieldValue) && fieldValue.toString().equals(value.toString())) {
                    return true;
                }
            }
            valid = false;
        } catch (Exception e) {
            valid = false;
        }
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(annotation.message() + "&" + annotation.fieldName()).addConstraintViolation();
            return false;
        }
        return true;
    }


    private Field getFieldFromClassHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = clazz;

        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy.");
    }

}