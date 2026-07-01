package com.cfs.backend.cluster;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.SortedMap;
import java.util.TreeMap;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import static java.util.Objects.hash;

@Component
public class ConsistentHashRing {
	private final SortedMap<Long, ClusterNode> ring = new TreeMap<>();
	private static final int VIRTUAL_NODES = 100;

	private long hashFunc(String key){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
			long hash = 0;
			for(int i=0;i<4;i++){
				hash <<= 8;
				hash |= (digest[i] & 0xff);
			}
			return hash;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addNode(ClusterNode node){
		for(int i=0; i<VIRTUAL_NODES; i++){
			ring.put(
					hashFunc(node.getNodeId() + "-" + i),
					node
			);
		}
	}

	public void removeNode(ClusterNode node){
		//ring.remove(hashFunc(node.getNodeId()));
		for(int i=0; i<VIRTUAL_NODES; i++){
			ring.remove(hashFunc(node.getNodeId() + "-" + i), node
			);
		}
	}

	public ClusterNode getOwner(String documentId){
		if(ring.isEmpty()){
			throw new IllegalStateException("No nodes");
		}

		long hash = hashFunc(documentId);
		SortedMap<Long,ClusterNode> tailMap = ring.tailMap(hash); // key >= hash
		Long nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
		return ring.get(nodeHash);

	}

	@PostConstruct
	public void init(){
		addNode(new ClusterNode(
						"node-1",
						"http://localhost:8080"
				)
		);

		addNode(new ClusterNode(
						"node-2",
						"http://localhost:8081"
				)
		);

		addNode(new ClusterNode(
						"node-3",
						"http://localhost:8082"
				)
		);
	}

}
