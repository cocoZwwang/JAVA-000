package pers.cocoadel.cmq.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.server.comm.request.CmqRequest;
import pers.cocoadel.cmq.server.comm.response.CmqResponse;
import pers.cocoadel.cmq.server.comm.enums.ResponseStatus;

import java.util.List;

/**
 * 请求报文 反序列化
 */
@Slf4j
public class CmqProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        CmqRequest<?> cmqRequest = new CmqRequest<>();
        try {
            cmqRequest.decode(in);
            //如果 out 的 size 是 0，ctx 不会往下发射事件，所以 decode 发生异常可以直接返回。
            out.add(cmqRequest);
        } catch (IllegalArgumentException e) {
            CmqResponse<?> cmqResponse = new CmqResponse<>();
            cmqResponse.setStreamId(cmqRequest.getStreamId());
            cmqResponse.setOperationType(cmqRequest.getOperationType());
            cmqResponse.setResponseStatus(ResponseStatus.REQUEST_ERROR);
            cmqResponse.setResultMessage(e.getMessage());
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(cmqResponse);
            }else{
                //如果当前通过不可，暂时丢弃然后打印日志
                log.error("dropped cmqResponse: " + cmqResponse);
            }
        }

    }
}
