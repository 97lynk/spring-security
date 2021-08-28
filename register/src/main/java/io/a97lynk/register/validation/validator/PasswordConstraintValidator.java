package io.a97lynk.register.validation.validator;

import io.a97lynk.register.validation.annotation.ValidPassword;
import org.passay.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

@Component
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	private final MessageSource messageSource;

	private final PasswordValidator validator;

	public PasswordConstraintValidator(MessageSource messageSource) {
		this.messageSource = messageSource;
		validator = new PasswordValidator(Arrays.asList(
				new LengthRule(8, 30),
				new UppercaseCharacterRule(1),
				new DigitCharacterRule(1),
				new SpecialCharacterRule(1),
				new NumericalSequenceRule(3, true),
				new AlphabeticalSequenceRule(3, true),
				new WhitespaceRule()));
	}

	@Override
	public void initialize(ValidPassword arg0) {
		// empty method
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		RuleResult result = validator.validate(new PasswordData(password));

		if (result.isValid()) return true;


		context.disableDefaultConstraintViolation();

		StringBuilder message = new StringBuilder();

		result.getDetails()
				.stream()
				.limit(5)
				.map(this::getMessage)
				.forEach(message::append);

		context.buildConstraintViolationWithTemplate(message.toString())
				.addConstraintViolation();

		return false;
	}

	private String getMessage(RuleResultDetail detail) {
		return String.format("- %s<br/>", messageSource.getMessage(detail.getErrorCode(), detail.getValues(), LocaleContextHolder.getLocale()));
	}
}