package com.cfs.backend.service;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cfs.backend.dto.SessionInfo;

import org.springframework.stereotype.Service;

@Service
public class SessionRegistry {
	private final Map<String, SessionInfo>
			sessions =
			new ConcurrentHashMap<>();

	public void register(String sessionId, SessionInfo sessionInfo) {
		sessions.put(sessionId, sessionInfo);
	}

	public void remove(String sessionId) {
		sessions.remove(sessionId);
	}

	public SessionInfo get(String sessionId) {
		return sessions.get(sessionId);
	}

}
