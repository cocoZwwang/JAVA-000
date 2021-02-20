package pers.cocoadel.cmq.server.netty.exchange.support;

import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.server.netty.exchange.ExchangeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.StreamSupport;

public class ExchangeHandlerRegistry {

    private static ExchangeHandlerRegistry INSTANCE;

    public static ExchangeHandlerRegistry getInstance() {
        if (INSTANCE == null) {
            synchronized (ExchangeHandlerRegistry.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ExchangeHandlerRegistry();
                }
            }
        }
        return INSTANCE;
    }

    private final Map<OperationType, ExchangeHandler> handlerMap = new HashMap<>();

    private ExchangeHandlerRegistry(){
        StreamSupport.
                stream(ServiceLoader.load(ExchangeHandler.class).spliterator(), false)
                .forEach(exchangeHandler -> {
                    Set<OperationType> set = exchangeHandler.supportOperationTypes();
                    if (set == null || set.isEmpty()) {
                        return;
                    }
                    for (OperationType operationType : set) {
                        handlerMap.put(operationType,exchangeHandler);
                    }
                });
    }

    public ExchangeHandler get(OperationType operationType){
        return handlerMap.get(operationType);
    }
}
