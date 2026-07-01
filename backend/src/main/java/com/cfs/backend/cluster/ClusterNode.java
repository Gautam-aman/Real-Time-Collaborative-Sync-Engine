package com.cfs.backend.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ClusterNode {
	private String nodeId;
	private String address;
}
