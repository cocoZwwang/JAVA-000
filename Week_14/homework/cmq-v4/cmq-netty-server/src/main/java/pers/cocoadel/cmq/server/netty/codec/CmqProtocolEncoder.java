package pers.cocoadel.cmq.server.netty.codec;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.netty.comm.ByteStreamResponse;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.util.List;

/**
 * 响应实体 序列化成二进制
 */
@Slf4j
public class CmqProtocolEncoder extends MessageToMessageEncoder<ByteStreamResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteStreamResponse msg, List<Object> out) throws Exception {
        try {
            ByteBuf buf = ctx.alloc().buffer();
            msg.encode(buf);
            out.add(buf);
        } catch (Exception e) {
            log.error("CmqProtocolEncoder error: " + Throwables.getStackTraceAsString(e));
        }
    }
}
