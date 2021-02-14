package pers.cocoadel.cmq.client.netty.codec;

import com.google.common.base.Throwables;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.client.netty.RequestPendingCenter;
import pers.cocoadel.cmq.netty.comm.ByteStreamResponse;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.nio.charset.StandardCharsets;

@Slf4j
public class CmqResponseBodyJsonDecoder extends SimpleChannelInboundHandler<ByteStreamResponse> {

    private final RequestPendingCenter requestPendingCenter;

    public CmqResponseBodyJsonDecoder(RequestPendingCenter requestPendingCenter) {
        this.requestPendingCenter = requestPendingCenter;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteStreamResponse msg) throws Exception {
        try {
            byte[] bytes = msg.getBytes();
            if (bytes != null && bytes.length > 0) {
                String json = new String(bytes, StandardCharsets.UTF_8);
                msg.setBody(json);
            }
        } catch (Exception e) {
            log.error("CmqResponseBodyJsonDecoder: " + Throwables.getStackTraceAsString(e));
        } finally {
            requestPendingCenter.set(msg.getStreamId(), msg);
        }
    }

}
