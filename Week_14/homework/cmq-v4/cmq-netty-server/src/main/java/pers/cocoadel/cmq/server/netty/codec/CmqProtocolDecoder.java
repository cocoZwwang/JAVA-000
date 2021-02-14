package pers.cocoadel.cmq.server.netty.codec;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.netty.comm.ByteStreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.util.List;

/**
 * 二进制 反序列化成 obj
 */
@Slf4j
public class CmqProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteStreamRequest request = new ByteStreamRequest();
        try {
            request.decode(in);
            //如果 out 的 size 是 0，ctx 不会往下发射事件，所以 decode 发生异常可以直接返回。
            out.add(request);
        } catch (IllegalArgumentException e) {
            StreamResponse response = StreamResponse.createStreamResponse(request, ResponseStatus.REQUEST_ERROR);
            response.setResultMessage("request deserializer error: " + e.getMessage());
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(response);
            }else{
                //如果当前通过不可，暂时丢弃然后打印日志
                log.error("dropped cmqResponse: " + response);
            }
            log.error("CmqProtocolDecoder error: " + Throwables.getStackTraceAsString(e));
        }

    }
}
