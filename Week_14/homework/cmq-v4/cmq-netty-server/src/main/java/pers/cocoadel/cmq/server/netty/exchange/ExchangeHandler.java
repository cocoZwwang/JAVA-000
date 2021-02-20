package pers.cocoadel.cmq.server.netty.exchange;

import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.server.netty.exchange.support.Exchange;

import java.util.Set;

public interface ExchangeHandler {

    boolean isMatch(OperationType operationType);

    Set<OperationType> supportOperationTypes();

    void handle(Exchange exchange);
}
