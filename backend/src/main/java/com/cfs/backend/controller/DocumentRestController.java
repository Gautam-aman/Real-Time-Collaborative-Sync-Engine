package com.cfs.backend.controller;


import com.cfs.backend.model.Document;
import com.cfs.backend.model.SyncResponse;
import com.cfs.backend.service.DocumentService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentRestController {

	private final DocumentService documentService;


	@GetMapping("/{id}")
	public SyncResponse getDocument(
			@PathVariable String id) {

		Document document =
				documentService.getOrCreate(id);

		return SyncResponse.builder()
				.documentId(id)
				.content(
						document.getContent()
								.toString()
				)
				.version(
						document.getVersion()
				)
				.build();
	}

}
