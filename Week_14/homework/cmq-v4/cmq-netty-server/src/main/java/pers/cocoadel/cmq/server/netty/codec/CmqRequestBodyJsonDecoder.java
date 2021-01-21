package pers.cocoadel.cmq.server.netty.codec;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.comm.enums.ResponseStatus;
import pers.cocoadel.cmq.comm.request.RequestBody;
import pers.cocoadel.cmq.server.netty.comm.ByteStreamRequest;
import pers.cocoadel.cmq.server.netty.comm.OperationType;
import pers.cocoadel.cmq.server.netty.comm.StreamRequest;
import pers.cocoadel.cmq.server.netty.comm.StreamResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 请求体进行 Json 反序列化
 */
@Slf4j
public class CmqRequestBodyJsonDecoder extends MessageToMessageDecoder<ByteStreamRequest> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteStreamRequest msg, List<Object> out) throws Exception {
        try {
            byte[] body = msg.getBody();
            String json = new String(body, StandardCharsets.UTF_8);
            OperationType operationType = msg.getOperationType();
            RequestBody requestBody = JSON.parseObject(json, operationType.getRequestBodyClass());
            //构造新的 StreamRequest
            StreamRequest<RequestBody> streamRequest = StreamRequest.createStreamRequestWithoutBody(msg);
            streamRequest.setBody(requestBody);
            //继续往下传递
            out.add(streamRequest);
        } catch (Exception e) {
            StreamResponse response = StreamResponse.createStreamResponse(msg, ResponseStatus.REQUEST_ERROR);
            response.setResultMessage("request deserializer error: " + e.getMessage());
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(response);
            }else{
                //如果当前通过不可，暂时丢弃然后打印日志
                log.error("dropped cmqResponse: " + response);
            }
        }
    }
}
