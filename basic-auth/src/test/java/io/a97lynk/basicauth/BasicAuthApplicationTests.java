package io.a97lynk.basicauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "logging.level.root=trace")
public class BasicAuthApplicationTests {

	private TestRestTemplate restTemplate;
	private URL base;

	@LocalServerPort
	private int port;

	@BeforeEach
	public void setUp() throws MalformedURLException {
		restTemplate = new TestRestTemplate();
		base = new URL("http://localhost:" + port);
	}

	@Test
	public void whenAnonymousRequestsPublic_ThenSuccess()
			throws IllegalStateException, IOException {
		ResponseEntity<Map> response = restTemplate.getForEntity(base.toString() + "/home/foo", Map.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().containsKey("foo"));
		assertTrue(response.getBody().containsValue("home"));
	}

	@Test
	public void whenLoggedUserRequests_ThenSuccess()
			throws IllegalStateException, IOException {
		restTemplate = new TestRestTemplate("user1", "user1Pass");
		ResponseEntity<Map> response = restTemplate.getForEntity(base.toString() + "/user/foo", Map.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().containsKey("foo"));
		assertTrue(response.getBody().containsValue("user"));
	}

	@Test
	public void whenUserWithoutCredentials_thenUnauthorizedPage()
			throws Exception {
		ResponseEntity<Map> response = restTemplate.getForEntity(base.toString() + "/user/foo", Map.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertTrue(response.getBody().containsValue("Unauthorized"));
	}

	@Test
	public void whenUserWithWrongCredentials_thenUnauthorizedPage()
			throws Exception {

		restTemplate = new TestRestTemplate("user1", "wrongpassword");
		ResponseEntity<Map> response =
				restTemplate.getForEntity(base.toString() + "/user/foo", Map.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
}