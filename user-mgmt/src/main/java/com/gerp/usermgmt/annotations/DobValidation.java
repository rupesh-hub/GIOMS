package com.gerp.usermgmt.annotations;

import com.gerp.usermgmt.annotations.validator.DobValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DobValidator.class)
public @interface DobValidation {
    String message() default "Invalid Age of Employee";
    int minAge() default 18;

    int maxAge() default 58;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
