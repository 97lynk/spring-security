package io.a97lynk.register.validation.validator;

import io.a97lynk.register.dto.UserDto;
import io.a97lynk.register.validation.annotation.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
		implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(PasswordMatches constraintAnnotation) {
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		UserDto user = (UserDto) obj;
		return user.getPassword().equals(user.getMatchingPassword());
	}
}