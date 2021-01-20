package pers.cocoadel.cmq.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import pers.cocoadel.cmq.server.comm.response.CmqResponse;

import java.util.List;

/**
 * 响应实体 序列化
 */
public class CmqProtocolEncoder extends MessageToMessageEncoder<CmqResponse<?>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CmqResponse<?> msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        msg.encode(buf);
        out.add(buf);
    }
}
