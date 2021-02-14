package pers.cocoadel.cmq.client.netty.codec;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.netty.comm.ByteStreamResponse;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.util.List;

@Slf4j
public class CmqProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            ByteStreamResponse streamResponse = new ByteStreamResponse();
            streamResponse.decode(in);
            out.add(streamResponse);
        } catch (Exception e) {
            log.error("CmqProtocolDecoder: " + Throwables.getStackTraceAsString(e));
        }
    }
}
