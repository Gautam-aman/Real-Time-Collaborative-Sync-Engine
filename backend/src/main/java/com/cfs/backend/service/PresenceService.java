package com.cfs.backend.service;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class PresenceService {

	private final ConcurrentHashMap<
			String,
			ConcurrentHashMap<String,Integer>
			> onlineUsers =
			new ConcurrentHashMap<>();

	public void join(
			String documentId,
			String userId) {

		onlineUsers
				.computeIfAbsent(
						documentId,
						k -> new ConcurrentHashMap<>()
				)
				.merge(
						userId,
						1,
						Integer::sum
				);
	}

	public void leave(
			String documentId,
			String userId) {

		ConcurrentHashMap<String,Integer>
				users =
				onlineUsers.get(documentId);

		if(users == null) {
			return;
		}

		users.computeIfPresent(
				userId,
				(u,count) -> {

					if(count <= 1) {
						return null;
					}

					return count - 1;
				}
		);

		if(users.isEmpty()) {

			onlineUsers.remove(
					documentId
			);
		}
	}

	public Set<String> getUsers(
			String documentId) {

		return onlineUsers
				.getOrDefault(
						documentId,
						new ConcurrentHashMap<>()
				)
				.keySet();
	}
}
