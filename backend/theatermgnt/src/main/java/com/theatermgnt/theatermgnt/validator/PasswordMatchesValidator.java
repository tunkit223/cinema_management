package com.theatermgnt.theatermgnt.validator;

import com.theatermgnt.theatermgnt.account.dto.request.PasswordCreationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordCreationRequest> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(PasswordCreationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Let @NotNull handle null case
        }
        return request.getPassword() != null && request.getPassword().equals(request.getConfirmPassword());
    }
}
