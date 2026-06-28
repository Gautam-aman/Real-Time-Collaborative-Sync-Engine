package com.cfs.backend.model;

import lombok.Data;

@Data
public class Document {

	private String id;

	private StringBuilder content;

	private long version;

	public Document(String id) {
		this.id = id;
		this.content = new StringBuilder();
		this.version = 0;
	}
}
