package com.cfs.backend.cluster;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cluster") // for multiple @Value annotation
@Getter
@Setter
public class ClusterProperties {
	private String nodeId;
	private String nodeAddress;
}