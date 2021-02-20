package pers.cocoadel.cmq.client.netty;

import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RequestPendingCenter {

    private final Map<Long, RequestFuture> futureMap = new ConcurrentHashMap<>();

    public void addFuture(Long streamId, RequestFuture future) {
        futureMap.put(streamId, future);
    }

    public void set(Long streamId, StreamResponse response) {
        if (futureMap.containsKey(streamId)) {
            RequestFuture requestFuture = futureMap.get(streamId);
            requestFuture.setSuccess(response);
            futureMap.remove(streamId);
        }
    }
}
