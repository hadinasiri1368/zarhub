package org.zarhub.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.zarhub.common.DateUtils;
import org.zarhub.common.Utils;
import org.zarhub.constant.Consts;

import java.util.regex.Pattern;

public class PersianDateValidator implements ConstraintValidator<ValidPersianDate, String> {
    private static final Pattern PATTERN = Pattern.compile(Consts.PERSIAN_DATE_REGEX);
    private ValidPersianDate annotation;


    @Override
    public void initialize(ValidPersianDate constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (Utils.isNull(value)) {
            return true;
        }
        if (!PATTERN.matcher(value).matches()) {
            isValid = false;
        } else if (!DateUtils.isValid(value)) {
            isValid = false;
        }
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(annotation.message() + "&" + annotation.fieldName()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
