package com.cfs.backend.repository;

import com.cfs.backend.model.DocumentSnapshot;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSnapshotRepository extends JpaRepository<DocumentSnapshot, String> {
}
