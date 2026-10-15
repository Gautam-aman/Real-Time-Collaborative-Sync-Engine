package com.cfs.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cfs.backend.Metrics.MetricsService;
import com.cfs.backend.cluster.ClusterNode;
import com.cfs.backend.cluster.ConsistentHashRing;
import com.cfs.backend.model.Document;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

	@Mock
	private OperationTransformService operationTransformService;

	@Mock
	private SnapshotService snapshotService;

	@Mock
	private RecoveryService recoveryService;

	@Mock
	private ConsistentHashRing consistentHashRing;

	@Mock
	private MetricsService metricsService;

	private DocumentService documentService;

	@BeforeEach
	void setUp() {
		documentService = new DocumentService(
				operationTransformService,
				snapshotService,
				recoveryService,
				consistentHashRing,
				metricsService
		);
	}

	@Test
	void processOperationReturnsUpdatedVersion() {

		when(consistentHashRing.getOwner("doc-1"))
				.thenReturn(new ClusterNode("node-1", "http://localhost:8080"));

		when(recoveryService.recover("doc-1")).thenReturn(new Document("doc-1"));

		OperationMessage message = new OperationMessage();
		message.setDocumentId("doc-1");
		message.setOperation(OperationType.INSERT);
		message.setPosition(0);
		message.setValue("A");
		message.setUserId("user-1");
		message.setVersion(0);

		var result = documentService.processOperation(message);

		assertThat(result.getVersion()).isEqualTo(1);
		assertThat(documentService.getDocument("doc-1").getVersion()).isEqualTo(1);
		assertThat(documentService.getDocument("doc-1").getContent().toString())
				.isEqualTo("A");
	}

	@Test
	void shouldRejectInsertWithNegativePosition() {

		Document document = new Document("doc-1");

		when(recoveryService.recover("doc-1"))
				.thenReturn(document);

		OperationMessage message = OperationMessage.builder()
				.documentId("doc-1")
				.userId("user-1")
				.operation(OperationType.INSERT)
				.position(-5)
				.value("X")
				.version(0)
				.build();

		assertThatThrownBy(() ->
				documentService.processOperation(message)
		)
				.isInstanceOf(IllegalArgumentException.class);

		assertThat(document.getContent().toString())
				.isEmpty();

		assertThat(document.getVersion())
				.isZero();

		assertThat(document.getHistory())
				.isEmpty();
	}

	@Test
	void shouldRejectInsertBeyondDocumentLength() {

		Document document = new Document("doc-1");
		document.getContent().append("HELLO");

		when(recoveryService.recover("doc-1"))
				.thenReturn(document);

		OperationMessage message = OperationMessage.builder()
				.documentId("doc-1")
				.userId("user-1")
				.operation(OperationType.INSERT)
				.position(100)
				.value("X")
				.version(0)
				.build();

		assertThatThrownBy(() ->
				documentService.processOperation(message)
		).isInstanceOf(IllegalArgumentException.class);

		assertThat(document.getContent().toString())
				.isEqualTo("HELLO");

		assertThat(document.getVersion())
				.isZero();
	}

	@Test
	void shouldRejectFutureDocumentVersion() {

		Document document = new Document("doc-1");

		when(recoveryService.recover("doc-1"))
				.thenReturn(document);

		OperationMessage message = OperationMessage.builder()
				.documentId("doc-1")
				.userId("user-1")
				.operation(OperationType.INSERT)
				.position(0)
				.value("X")
				.version(10)
				.build();

		assertThatThrownBy(() ->
				documentService.processOperation(message)
		).isInstanceOf(IllegalArgumentException.class);

		assertThat(document.getVersion()).isZero();
		assertThat(document.getContent().toString()).isEmpty();
	}

	@Test
	void loadDocumentUsesRecoveredStateWithoutDuplicatingSnapshotContent() {
		Document recovered = new Document("doc-1");
		recovered.getContent().append("hello");
		recovered.setVersion(3);

		when(recoveryService.recover("doc-1")).thenReturn(recovered);

		Document document = documentService.loadDocument("doc-1");

		assertThat(document.getContent().toString()).isEqualTo("hello");
		assertThat(document.getVersion()).isEqualTo(3);

		verify(recoveryService).recover("doc-1");
	}
}