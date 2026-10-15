package com.cfs.backend.service;

import com.cfs.backend.dto.AppliedOperation;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationTransformServiceTest {

    private OperationTransformService service;

    @Test
    void shouldShiftByInsertedTextLength() {

        AppliedOperation applied = AppliedOperation.builder()
                .version(1)
                .operation(OperationType.INSERT)
                .position(1)
                .value("XYZ")
                .userId("user-A")
                .build();

        OperationMessage incoming = OperationMessage.builder()
                .documentId("doc-1")
                .userId("user-B")
                .operation(OperationType.INSERT)
                .position(3)
                .value("!")
                .version(0)
                .build();

        service.transform(incoming, applied);

        assertThat(incoming.getPosition()).isEqualTo(6);
    }

    @Test
    void shouldShiftDeleteAfterMultiCharacterInsert() {

        AppliedOperation applied = AppliedOperation.builder()
                .version(1)
                .operation(OperationType.INSERT)
                .position(1)
                .value("XYZ")
                .build();

        OperationMessage incoming = OperationMessage.builder()
                .operation(OperationType.DELETE)
                .position(2)
                .version(0)
                .build();

        service.transform(incoming, applied);

        assertThat(incoming.getPosition()).isEqualTo(5);
    }

    @Test
    void shouldMakeDuplicateDeleteANoop() {
        AppliedOperation applied = AppliedOperation.builder()
                .operation(OperationType.DELETE)
                .position(1)
                .version(1)
                .build();

        OperationMessage incoming = OperationMessage.builder()
                .operation(OperationType.DELETE)
                .position(1)
                .version(0)
                .build();

        service.transform(incoming, applied);

        assertThat(incoming.getPosition()).isEqualTo(-1);
    }

    @Test
    void shouldShiftDeleteLeftAfterEarlierDelete() {
        AppliedOperation applied = AppliedOperation.builder()
                .operation(OperationType.DELETE)
                .position(1)
                .version(1)
                .build();

        OperationMessage incoming = OperationMessage.builder()
                .operation(OperationType.DELETE)
                .position(3)
                .version(0)
                .build();

        service.transform(incoming, applied);

        assertThat(incoming.getPosition()).isEqualTo(2);
    }

    @Test
    void shouldNotShiftDeleteAfterLaterPositionDeleted() {
        AppliedOperation applied = AppliedOperation.builder()
                .operation(OperationType.DELETE)
                .position(4)
                .version(1)
                .build();

        OperationMessage incoming = OperationMessage.builder()
                .operation(OperationType.DELETE)
                .position(1)
                .version(0)
                .build();

        service.transform(incoming, applied);

        assertThat(incoming.getPosition()).isEqualTo(1);
    }

    @Test
    void shouldPreserveNoopDeleteAcrossLaterTransforms() {

        OperationMessage incoming = OperationMessage.builder()
                .operation(OperationType.DELETE)
                .position(1)
                .version(0)
                .build();

        AppliedOperation firstDelete = AppliedOperation.builder()
                .operation(OperationType.DELETE)
                .position(1)
                .version(1)
                .build();

        AppliedOperation laterInsert = AppliedOperation.builder()
                .operation(OperationType.INSERT)
                .position(0)
                .value("XYZ")
                .version(2)
                .build();

        service.transform(incoming, firstDelete);

        assertThat(incoming.getPosition()).isEqualTo(-1);

        service.transform(incoming, laterInsert);

        assertThat(incoming.getPosition()).isEqualTo(-1);
    }

    @Test
    void shouldTransformOperationAcrossMultipleHistoryEntries() {

        OperationMessage incoming = OperationMessage.builder()
                .operation(OperationType.DELETE)
                .position(1)
                .version(0)
                .build();

        AppliedOperation first = AppliedOperation.builder()
                .operation(OperationType.INSERT)
                .position(1)
                .value("XYZ")
                .version(1)
                .build();

        AppliedOperation second = AppliedOperation.builder()
                .operation(OperationType.INSERT)
                .position(2)
                .value("!")
                .version(2)
                .build();

        service.transform(incoming, first);
        service.transform(incoming, second);

        assertThat(incoming.getPosition()).isEqualTo(5);
    }

    @BeforeEach
    void setUp() {
        service = new OperationTransformService();
    }

    @Test
    void shouldShiftWhenAppliedInsertComesBeforeIncoming() {

        AppliedOperation applied = AppliedOperation.builder()
                .operation(OperationType.INSERT)
                .position(1)
                .value("B")
                .build();

        OperationMessage incoming = OperationMessage.builder()
                .operation(OperationType.INSERT)
                .position(3)
                .value("X")
                .build();

        service.transform(incoming, applied);

        assertThat(incoming.getPosition()).isEqualTo(4);
    }

    @Test
    void shouldNotShiftWhenAppliedInsertComesAfterIncoming() {

        AppliedOperation applied = AppliedOperation.builder()
                .operation(OperationType.INSERT)
                .position(4)
                .value("B")
                .build();

        OperationMessage incoming = OperationMessage.builder()
                .operation(OperationType.INSERT)
                .position(1)
                .value("X")
                .build();

        service.transform(incoming, applied);

        assertThat(incoming.getPosition()).isEqualTo(1);
    }

    @Test
    void shouldShiftConcurrentInsertAtSamePosition() {

        AppliedOperation applied = AppliedOperation.builder()
                .version(1)
                .operation(OperationType.INSERT)
                .position(1)
                .value("B")
                .userId("user-A")
                .build();

        OperationMessage incoming = OperationMessage.builder()
                .documentId("doc-1")
                .userId("user-B")
                .operation(OperationType.INSERT)
                .position(1)
                .value("X")
                .version(0)
                .build();

        service.transform(incoming, applied);

        assertThat(incoming.getPosition()).isEqualTo(2);
    }
}