package com.cfs.backend.service;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class PresenceService {
	private final ConcurrentHashMap<
			String,
			Set<String>
			> onlineUsers = new ConcurrentHashMap<>();

	public void join(String documentId, String userId) {
		onlineUsers.computeIfAbsent(documentId, k -> new HashSet<>()).add(userId);
	}

	public void leave(String documentId, String userId) {
		Set<String> users =
				onlineUsers.get(documentId);

		if(users != null) {

			users.remove(userId);

			if(users.isEmpty()) {
				onlineUsers.remove(documentId);
			}
		}
	}

	public Set<String> getUsers(String documentId) {
		return onlineUsers.getOrDefault(documentId, Set.of());
	}

}
