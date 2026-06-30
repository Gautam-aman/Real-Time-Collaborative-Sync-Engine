package com.cfs.backend.service;

import com.cfs.backend.dto.AppliedOperation;
import com.cfs.backend.model.OperationMessage;
import com.cfs.backend.model.OperationType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationTransformService {

	public void transformInsertAgainInsert(OperationMessage operationMessage , AppliedOperation appliedOperation)
	{
		if (appliedOperation.getPosition()< operationMessage.getPosition()){
			operationMessage.setPosition(operationMessage.getPosition()+1);
		}
	}

	public void transform(OperationMessage incoming, AppliedOperation applied){
		if (incoming.getOperation() == OperationType.INSERT && applied.getOperation() == OperationType.INSERT){
			transformInsertAgainInsert(incoming, applied);
		}
	}

}
