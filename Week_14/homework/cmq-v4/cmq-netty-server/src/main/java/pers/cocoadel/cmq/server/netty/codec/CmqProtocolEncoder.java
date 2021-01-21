package pers.cocoadel.cmq.server.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import pers.cocoadel.cmq.server.netty.comm.StreamResponse;

import java.util.List;

/**
 * 响应实体 序列化成二进制
 */
public class CmqProtocolEncoder extends MessageToMessageEncoder<StreamResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, StreamResponse msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        msg.encode(buf);
        out.add(buf);
    }
}
