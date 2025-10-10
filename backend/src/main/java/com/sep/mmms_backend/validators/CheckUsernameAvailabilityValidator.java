package com.sep.mmms_backend.validators;


import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.global_constants.ValidationErrorMessages;
import com.sep.mmms_backend.service.AppUserService;
import com.sep.mmms_backend.service.MemberService;
import com.sep.mmms_backend.validators.annotations.CheckUsernameAvailability;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CheckUsernameAvailabilityValidator implements ConstraintValidator<CheckUsernameAvailability, String> {

    @Autowired
    MemberService memberService;


    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if(username==null){
            return false;
        }

        Member member = memberService.findAccessibleMemberByUsername(username);
        if (member == null) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ValidationErrorMessages.USERNAME_NOT_AVAILABLE);
            return true;
        }
        return false;
    }

    @Override
    public void initialize(CheckUsernameAvailability constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
