package io.a97lynk.register.repository;

import io.a97lynk.register.entity.User;
import io.a97lynk.register.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	VerificationToken findByToken(String token);

	VerificationToken findByUser(User user);
}