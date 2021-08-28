package io.a97lynk.register.dto;


import io.a97lynk.register.validation.annotation.PasswordMatches;
import io.a97lynk.register.validation.annotation.ValidEmail;
import io.a97lynk.register.validation.annotation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@PasswordMatches
public class UserDto {

	@NotNull
	@NotEmpty
	private String firstName;

	@NotNull
	@NotEmpty
	private String lastName;

	@NotNull
	@NotEmpty
	@ValidPassword
	private String password;
	private String matchingPassword;

	@ValidEmail
	@NotNull
	@NotEmpty
	private String email;

}