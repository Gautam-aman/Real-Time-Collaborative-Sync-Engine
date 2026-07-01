package com.cfs.backend.repository;

import javax.swing.event.DocumentEvent;

import com.cfs.backend.dto.DocumentEventEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentEventRepository  extends JpaRepository<DocumentEventEntity, Long> {



}
