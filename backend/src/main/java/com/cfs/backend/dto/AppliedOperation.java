package com.cfs.backend.dto;


import com.cfs.backend.model.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppliedOperation {
	private long version;
	private OperationType operation;
	private int position;
	private String value;
	private String userId;
}
