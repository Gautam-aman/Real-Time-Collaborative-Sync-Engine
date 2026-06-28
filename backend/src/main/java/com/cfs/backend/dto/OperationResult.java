package com.cfs.backend.dto;


import com.cfs.backend.model.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResult {
	private String documentId;

	private OperationType operation;

	private Integer position;

	private String value;

	private long version;

	private String userId;
}
