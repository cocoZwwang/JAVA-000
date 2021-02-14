package pers.cocoadel.cmq.client.netty.codec;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.netty.comm.ByteStreamRequest;

@Slf4j
public class CmqProtocolEncoder extends MessageToByteEncoder<ByteStreamRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteStreamRequest msg, ByteBuf out) throws Exception {
        try {
            msg.encode(out);
        } catch (Exception e) {
            log.error("CmqProtocolEncoder: " + Throwables.getStackTraceAsString(e));
        }
    }
}
