package com.cfs.backend.dto;

import com.cfs.backend.model.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DocumentEditEvent {

	private String documentId;
	private String userId;
	private OperationType operation;
	private Integer position;
	private String value;
	private Long version;
	private Long timestamp;

}
