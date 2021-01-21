package pers.cocoadel.cmq.server.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;


public class CmqFrameDecoder extends LengthFieldBasedFrameDecoder {

    public CmqFrameDecoder() {
        super(ByteOrder.BIG_ENDIAN, Integer.MAX_VALUE, 0, 2,
                0, 2, true);
    }
}
