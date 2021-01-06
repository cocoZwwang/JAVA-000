package pers.cocoadel.client.netty;

import java.util.concurrent.atomic.AtomicLong;

public class StreamIdCreatorImpl implements StreamIdCreator {
    private final AtomicLong atomicLong = new AtomicLong();

    @Override
    public long nextId() {
        return atomicLong.incrementAndGet();
    }
}
