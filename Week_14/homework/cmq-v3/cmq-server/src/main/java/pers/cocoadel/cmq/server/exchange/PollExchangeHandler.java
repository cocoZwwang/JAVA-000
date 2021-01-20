package pers.cocoadel.cmq.server.exchange;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.message.GenericCmqMessage;
import pers.cocoadel.cmq.server.comm.request.CmqRequest;
import pers.cocoadel.cmq.server.comm.request.PollMessageRequestBody;
import pers.cocoadel.cmq.server.comm.request.RequestBody;
import pers.cocoadel.cmq.server.comm.response.CmqResponse;
import pers.cocoadel.cmq.server.comm.response.PollMessageResponseBody;
import pers.cocoadel.cmq.server.comm.enums.ResponseStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程消费者 和 本地消费者 交易
 */
@Slf4j
public class PollExchangeHandler extends SimpleChannelInboundHandler<CmqRequest<?>> {

    /**
     * 服务本地的消费者
     *
     * 一个连接只能一个 topic 一次，但是一个连接可以订阅多个不同的 topic。
     *
     * key : channelId + topic
     */
    private final Map<String, CmqConsumer<String>> localConsumerMap = new HashMap<>();

    private final CmqBroker cmqBroker;

    public PollExchangeHandler(CmqBroker cmqBroker) {
        this.cmqBroker = cmqBroker;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CmqRequest<?> msg) throws Exception {
        RequestBody requestBody = msg.getRequestBody();
        if (requestBody instanceof PollMessageRequestBody) {
            PollMessageRequestBody pollMessageRequestBody = (PollMessageRequestBody) requestBody;
            //构建 CmqResponse
            CmqResponse<PollMessageResponseBody> cmqResponse = new CmqResponse<>();
            cmqResponse.setStreamId(msg.getStreamId());
            cmqResponse.setOperationType(msg.getOperationType());
            //构建 PollMessageResponseBody
            PollMessageResponseBody responseBody = new PollMessageResponseBody();
            responseBody.setTopic(pollMessageRequestBody.getTopic());
            responseBody.setGroupId(pollMessageRequestBody.getGroupId());
            cmqResponse.setResponseBody(responseBody);
            //
            try {
                //检查请求参数
                if (pollMessageRequestBody.check()) {
                    //拉取消息
                    List<GenericCmqMessage<String>> list = doPoll(ctx,pollMessageRequestBody);
                    responseBody.setCmqMessages(list);
                    cmqResponse.setResponseStatus(ResponseStatus.OK);
                } else {
                    //参数错误
                    cmqResponse.setResponseStatus(ResponseStatus.REQUEST_ERROR);
                }
            } catch (Exception e) {
                cmqResponse.setResponseStatus(ResponseStatus.SERVER_ERROR);
            } finally {
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    ctx.writeAndFlush(cmqResponse);
                } else {
                    // 如果通道不可写，打印丢弃的信息
                    log.error("channel is unWritable,the response no send: " + cmqResponse);
                }
            }
        }
    }

    private List<GenericCmqMessage<String>>  doPoll(ChannelHandlerContext ctx,PollMessageRequestBody requestBody) {
        //Consumer的名称是连接Id + topic，也就是同一个topic一个连接只能订阅一次，但是可以订阅多个不同topic
        String name = ctx.channel().id() + "-" + requestBody.getTopic();
        CmqConsumer<String> consumer =
                localConsumerMap.computeIfAbsent(name,k -> cmqBroker.createConsumer(requestBody.getTopic(), name));
        // todo 暂时先一次啦一条消息
        // todo 暂时还没处理 group id
        GenericCmqMessage<String> cmqMessage = (GenericCmqMessage<String>) consumer.poll();
        return Collections.singletonList(cmqMessage);
    }
}
