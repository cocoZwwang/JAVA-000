package pers.cocoadel.cmq.client.netty.codec;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.netty.comm.ByteStreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class CmqRequestBodyJsonEncoder extends MessageToMessageEncoder<StreamRequest<?>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, StreamRequest<?> msg, List<Object> out) throws Exception {
        try {
            ByteStreamRequest request = new ByteStreamRequest();
            request.setStreamId(msg.getStreamId());
            request.setOperationType(msg.getOperationType());
            Object body = msg.getBody();
            if (body != null) {
                String json = JSON.toJSONString(body);
                request.setBody(json.getBytes(StandardCharsets.UTF_8));
            }
            out.add(request);
        } catch (Exception e) {
            log.error("CmqRequestBodyJsonEncoder: " + Throwables.getStackTraceAsString(e));
        }
    }
}
