package io.a97lynk.basicauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.AbstractMap;
import java.util.Map;

@RestController
public class SampleController {

	// @formatter:off
	@GetMapping("/home/foo")
	public Map.Entry<String, String> getHome() { return new AbstractMap.SimpleEntry<>("foo", "home"); }

	@GetMapping("/user/foo")
	public Map.Entry<String, String> getFooUser() { return new AbstractMap.SimpleEntry<>("foo", "user"); }

	@GetMapping("/admin/foo")
	public Map.Entry<String, String> getFooAdmin() { return new AbstractMap.SimpleEntry<>("foo", "admin"); }
	// @formatter:on
}
