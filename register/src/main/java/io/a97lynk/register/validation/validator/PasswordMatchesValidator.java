package io.a97lynk.register.validation.validator;

import io.a97lynk.register.validation.annotation.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(PasswordMatches constraintAnnotation) {
		// empty method
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		Class clazz = obj.getClass();
		try {
			String password = String.valueOf(clazz.getMethod("getPassword").invoke(obj));
			String matchingPassword = String.valueOf(clazz.getMethod("getMatchingPassword").invoke(obj));

			return password.equals(matchingPassword);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}