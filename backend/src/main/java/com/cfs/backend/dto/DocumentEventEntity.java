package com.cfs.backend.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="document_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEventEntity {

	@Id
	@GeneratedValue
	private Long id;
	private String documentId;
	private String userId;
	private String operation;
	private Integer position;
	private String value;
	private Long version;
	private Long timestamp;
}
