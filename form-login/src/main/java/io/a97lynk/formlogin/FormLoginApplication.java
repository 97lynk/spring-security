package io.a97lynk.formlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class FormLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(FormLoginApplication.class, args);
	}

}
