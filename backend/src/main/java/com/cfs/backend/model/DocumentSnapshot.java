package com.cfs.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Table(name = "document_snapshots")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DocumentSnapshot {

	@Id
    private String  documentId;

	@Lob
	private String  content;

	private Long version;

}
