package pers.cocoadel.cmq.server.netty.exchange;

import pers.cocoadel.cmq.netty.comm.OperationType;

import java.util.Set;

public abstract class AbstractExchangeHandler implements ExchangeHandler {
    private final Set<OperationType> operationTypeSet;

    public AbstractExchangeHandler(Set<OperationType> operationTypeSet){
        this.operationTypeSet = operationTypeSet;
    }

    @Override
    public boolean isMatch(OperationType operationType) {
        return operationTypeSet.contains(operationType);
    }

    @Override
    public Set<OperationType> supportOperationTypes() {
        return operationTypeSet;
    }
}
