package com.cfs.backend.cluster;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.SortedMap;
import java.util.TreeMap;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import static java.util.Objects.hash;

@Component
public class ConsistentHashRing {
	private final SortedMap<Long, ClusterNode> ring = new TreeMap<>();

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
		ring.put(hashFunc(node.getNodeId()), node);
	}

	public void removeNode(ClusterNode node){
		ring.remove(hashFunc(node.getNodeId()));
	}

	public ClusterNode getOwner(String documentId){
		if(ring.isEmpty()){
			throw new IllegalStateException("No nodes");
		}

		long hash = hashFunc(documentId);
		SortedMap<Long,ClusterNode> tailMap = ring.tailMap(hash);

		Long nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();

		return ring.get(nodeHash);

	}

}
