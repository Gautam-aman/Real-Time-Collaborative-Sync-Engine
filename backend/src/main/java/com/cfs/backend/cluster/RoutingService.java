package com.cfs.backend.cluster;

import com.cfs.backend.model.OperationMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class RoutingService {

	private final WebClient webClient;

	public void forwardOperation(String ownerAddress , OperationMessage message){
		webClient.post().uri(ownerAddress  + "/internal/edit")
				.bodyValue(message)
				.retrieve()
				.bodyToMono(Void.class)
				.block();
	}

}
