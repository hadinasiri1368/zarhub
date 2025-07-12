package org.zarhub.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.zarhub.common.Utils;

public class NationalCodeValidator implements ConstraintValidator<ValidNationalCode, String> {
    private ValidNationalCode annotation;

    @Override
    public void initialize(ValidNationalCode constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext context) {
        if(!Utils.checkNationalCode(string)){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(annotation.message()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
