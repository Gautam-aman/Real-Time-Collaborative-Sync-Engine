package com.cfs.backend.Metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {

	private final Counter operationCounter;

	private final AtomicInteger activeDocuments;

	public MetricsService(MeterRegistry registry) {

		this.operationCounter =
				Counter.builder("sync.operations.total")
						.description("Total operations")
						.register(registry);

		this.activeDocuments =
				registry.gauge("sync.documents.active", new AtomicInteger(0));
	}

	public void incrementOperations() {
		operationCounter.increment();
	}

	public void setActiveDocuments(int count) {
		activeDocuments.set(count);
	}
}