package com.cfs.backend.model;

import java.util.ArrayList;
import java.util.List;

import com.cfs.backend.dto.AppliedOperation;
import lombok.Data;

@Data
public class Document {

	private String id;

	private StringBuilder content;

	private long version;
	private List<AppliedOperation> history ;

	public Document(String id) {
		this.id = id;
		this.content = new StringBuilder();
		this.version = 0;
		this.history = new ArrayList<>();
	}
}
