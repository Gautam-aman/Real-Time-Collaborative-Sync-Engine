package com.cfs.backend.service;

import com.cfs.backend.repository.DocumentEventRepository;
import com.cfs.backend.repository.DocumentSnapshotRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecoveryService {
	private final
	DocumentSnapshotRepository snapshotRepository;
	private final DocumentEventRepository eventRepository;
}
