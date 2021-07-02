package io.a97lynk.formlogin.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginAttemptService {

	Map<String, Integer> cacheAttempts = new HashMap<>();

//	private static final Integer MAX_ATTEMPTS = 3;

	public void attemptFailure(String remoteAddr) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if (cacheAttempts.containsKey(username)) {
			cacheAttempts.computeIfPresent(username, (s, v) -> v + 1);
		} else {
			cacheAttempts.put(username, 1);
		}
	}

	public void attemptSuccess(String remoteAddr) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if (cacheAttempts.containsKey(username)) {
			cacheAttempts.computeIfPresent(username, (s, v) -> (v == 0) ? 0 : v - 1);
		} else {
			cacheAttempts.put(username, 0);
		}
	}

	public boolean isBlocked(String username) {
		if (cacheAttempts.containsKey(username)) {
			return cacheAttempts.get(username) >= 3;
		} else {
			return false;
		}
	}
}
