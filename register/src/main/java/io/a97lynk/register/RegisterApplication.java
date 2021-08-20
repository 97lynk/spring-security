package io.a97lynk.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class RegisterApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RegisterApplication.class, args);
	}

	@Autowired
	private DataSource dataSource;

	@Override
	public void run(String... args) throws Exception {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setContinueOnError(true);
		populator.setSeparator(";");
		populator.setSqlScriptEncoding(StandardCharsets.UTF_8.name());

		Resource resource = new ClassPathResource("sql/init-data.sql");
		populator.addScript(resource);

		DatabasePopulatorUtils.execute(populator, dataSource);
	}
}
