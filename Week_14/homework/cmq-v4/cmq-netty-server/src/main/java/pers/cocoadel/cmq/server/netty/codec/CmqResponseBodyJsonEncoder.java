package pers.cocoadel.cmq.server.netty.codec;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import pers.cocoadel.cmq.server.netty.comm.StreamResponse;

import java.util.List;

/**
 * 对返回体 进行 Json 序列化
 */
public class CmqResponseBodyJsonEncoder extends MessageToMessageEncoder<StreamResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, StreamResponse msg, List<Object> out) throws Exception {
        StreamResponse streamResponse = new StreamResponse();
        Object body = streamResponse.getBody();
        if (body != null) {
            streamResponse.setBody(JSON.toJSONString(body));
        }
        out.add(streamResponse);
    }
}
