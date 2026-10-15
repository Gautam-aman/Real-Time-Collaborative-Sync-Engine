package com.cfs.backend.service;

import com.cfs.backend.dto.AppliedOperation;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationTransformServiceTest {

    private OperationTransformService service;

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