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

	public void transformInsertAgainstInsert(OperationMessage operationMessage , AppliedOperation appliedOperation)
	{
		if (appliedOperation.getPosition() <= operationMessage.getPosition()){
			int insertedLength = appliedOperation.getValue().length();
			operationMessage.setPosition(insertedLength + operationMessage.getPosition());
			//operationMessage.setPosition(operationMessage.getPosition()+1);
		}
	}

	public void transform(OperationMessage incoming, AppliedOperation applied){

		if(incoming.getOperation() == OperationType.INSERT && applied.getOperation() == OperationType.INSERT){
			transformInsertAgainstInsert(incoming, applied);
		}

		if(incoming.getOperation() == OperationType.INSERT && applied.getOperation() == OperationType.DELETE){
			transformInsertAgainstDelete(incoming, applied);
		}

		if(incoming.getOperation() == OperationType.DELETE && applied.getOperation() == OperationType.INSERT){
			transformDeleteAgainstInsert(incoming, applied);
		}

		if(incoming.getOperation() == OperationType.DELETE && applied.getOperation() == OperationType.DELETE){
			transformDeleteAgainstDelete(incoming, applied);
		}
	}

	public void transformInsertAgainstDelete(OperationMessage incoming, AppliedOperation applied){
		if (applied.getPosition() < incoming.getPosition()){
			incoming.setPosition(incoming.getPosition()-1);
		}
	}

	public void transformDeleteAgainstInsert(OperationMessage incoming, AppliedOperation applied){
		if (applied.getPosition() < incoming.getPosition()){
			int insertedLength = applied.getValue().length();
			incoming.setPosition(incoming.getPosition() + insertedLength);
			//incoming.setPosition(incoming.getPosition()+1);
		}
	}

	private void transformDeleteAgainstDelete(OperationMessage incoming, AppliedOperation applied){

		if (applied.getPosition() == incoming.getPosition()){
			incoming.setPosition(-1);
			return;
		}

		if(applied.getPosition() < incoming.getPosition()){
			incoming.setPosition(incoming.getPosition()-1);
		}
	}

}
