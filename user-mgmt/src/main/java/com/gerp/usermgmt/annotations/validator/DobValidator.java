package com.gerp.usermgmt.annotations.validator;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.usermgmt.annotations.DobValidation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class DobValidator implements
        ConstraintValidator<DobValidation, LocalDate> {

    private int minAge;
    private int maxAge;
    private String message;

    @Autowired
    private CustomMessageSource customMessageSource;

    @Override
    public void initialize(DobValidation dobValidation) {
        this.minAge = dobValidation.minAge();
        this.maxAge = dobValidation.maxAge();
//        this.message = dobValidation.message();
    }

    @Override
    public boolean isValid(LocalDate s, ConstraintValidatorContext constraintValidatorContext) {
        if(s != null) {
            Period period = s.until(LocalDate.now());
            int age = period.getYears();
            if(age <= minAge) {
                customMessageForValidation(constraintValidatorContext , customMessageSource.get("invalid.min.age" , minAge));
                return false;
            }
            //  todo: make different api for karar and normal employee and add validation for date of birth
//            else if(age >= maxAge) {
//                customMessageForValidation(constraintValidatorContext , customMessageSource.get("invalid.max.age" , maxAge));
//                return false;
//            }
//            else {
//                customMessageForValidation(constraintValidatorContext , "Error while setting employee date of birth");
//                return false;
//            }
        }
        return true;
    }
    private void customMessageForValidation(ConstraintValidatorContext constraintValidatorContext, String message) {
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
