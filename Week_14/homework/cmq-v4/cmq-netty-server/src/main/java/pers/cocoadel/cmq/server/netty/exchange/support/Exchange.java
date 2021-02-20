package pers.cocoadel.cmq.server.netty.exchange.support;

import lombok.Data;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

@Data
public class Exchange {
    private StreamRequest<?> streamRequest;

    private StreamResponse streamResponse;
}
