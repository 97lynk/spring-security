package io.a97lynk.register.service;

import io.a97lynk.register.entity.Token;
import io.a97lynk.register.exceptions.EmailNotFoundException;
import io.a97lynk.register.exceptions.ExpiredTokenException;
import io.a97lynk.register.exceptions.NotFoundTokenException;

public interface TokenService {

	Token getToken(String tokenStr) throws NotFoundTokenException, ExpiredTokenException;

	Token getTokenIgnoreExpired(String tokenStr) throws NotFoundTokenException;

	Token createVerificationToken(String email) throws EmailNotFoundException;

	Token createPasswordToken(String email) throws EmailNotFoundException;


}
