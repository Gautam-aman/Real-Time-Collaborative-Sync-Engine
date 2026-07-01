package com.cfs.backend.repository;

import java.util.List;

import com.cfs.backend.dto.DocumentEventEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentEventRepository  extends JpaRepository<DocumentEventEntity, Long> {


	List<DocumentEventEntity> findByDocumentIdAndVersionGreaterThanOrderByVersionAsc(String documentId, long version);
}
