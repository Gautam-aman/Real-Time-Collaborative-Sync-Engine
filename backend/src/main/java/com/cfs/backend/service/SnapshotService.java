package com.cfs.backend.service;

import com.cfs.backend.model.Document;
import com.cfs.backend.model.DocumentSnapshot;
import com.cfs.backend.repository.DocumentSnapshotRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SnapshotService {
	private final DocumentSnapshotRepository documentSnapshotRepository;

	public void saveSnapshot(Document document){
		documentSnapshotRepository.save(
				DocumentSnapshot.builder()
						.documentId(document.getId())
						.content(document.getContent().toString())
						.version(document.getVersion())
						.build()
		);
	}

}
