package com.sep.mmms_backend.validators.annotations;


import com.sep.mmms_backend.validators.CheckUsernameAvailabilityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CheckUsernameAvailabilityValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUsernameAvailability {
    String message() default "username unavailable";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
