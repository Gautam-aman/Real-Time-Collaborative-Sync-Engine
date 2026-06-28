package com.cfs.backend.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationMessage {
	private Long documentId;
	private Long userId;

	private OperationType operation;
	private Integer position;
	private String value;
}
