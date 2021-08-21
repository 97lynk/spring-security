package io.a97lynk.register.dto;


import io.a97lynk.register.validation.annotation.PasswordMatches;
import io.a97lynk.register.validation.annotation.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@PasswordMatches
public class ChangePasswordDto {

	@NotNull
	@NotEmpty
	private String password;
	private String matchingPassword;

	@ValidEmail
	@NotNull
	@NotEmpty
	private String email;

}