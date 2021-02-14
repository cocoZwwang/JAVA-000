package pers.cocoadel.cmq.server.netty.codec;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.netty.comm.ByteStreamResponse;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 对返回体 进行 Json 序列化
 */
@Slf4j
public class CmqResponseBodyJsonEncoder extends MessageToMessageEncoder<StreamResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, StreamResponse msg, List<Object> out) throws Exception {
        try {
            ByteStreamResponse byteStreamResponse = ByteStreamResponse.createByteStreamResponse(msg);
            Object body = msg.getBody();
            if (body != null) {
                String json = JSON.toJSONString(body);
                byteStreamResponse.setBytes(json.getBytes(StandardCharsets.UTF_8));
            }
            out.add(byteStreamResponse);
        } catch (Exception e) {
            log.error("CmqResponseBodyJsonEncoder error: " + Throwables.getStackTraceAsString(e));
        }
    }
}
