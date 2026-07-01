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
				.bodyValue(message)// convert into json
				.retrieve() // wait for response === exchange()
				.bodyToMono(Void.class)// dont care about response body (200 OK)
				.block(); // wait untill execution finish
				// since mono is lazy we have to use block to pause execution untill response
	}

}
