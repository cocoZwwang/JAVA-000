package pers.cocoadel.cmq.server.netty.codec;

import io.netty.handler.codec.LengthFieldPrepender;

import java.nio.ByteOrder;

public class CmqFrameEncoder extends LengthFieldPrepender {
    public CmqFrameEncoder() {
        super(ByteOrder.BIG_ENDIAN, 2, 0, false);
    }
}
