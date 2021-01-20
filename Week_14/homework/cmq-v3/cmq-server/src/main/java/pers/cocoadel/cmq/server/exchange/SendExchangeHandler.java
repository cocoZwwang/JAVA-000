package pers.cocoadel.cmq.server.exchange;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.server.comm.request.CmqRequest;
import pers.cocoadel.cmq.server.comm.request.RequestBody;
import pers.cocoadel.cmq.server.comm.request.SendTextMessageRequestBody;
import pers.cocoadel.cmq.server.comm.response.CmqResponse;
import pers.cocoadel.cmq.server.comm.enums.ResponseStatus;

@Slf4j
public class SendExchangeHandler extends SimpleChannelInboundHandler<CmqRequest<?>> {

    private final CmqBroker cmqBroker;

    private CmqProducer cmqProducer;

    public SendExchangeHandler(CmqBroker cmqBroker) {
        this.cmqBroker = cmqBroker;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CmqRequest<?> msg) throws Exception {
        RequestBody requestBody = msg.getRequestBody();
        if (requestBody instanceof SendTextMessageRequestBody) {
            SendTextMessageRequestBody sendRequestBody = (SendTextMessageRequestBody) requestBody;
            CmqResponse<?> cmqResponse = new CmqResponse<>();
            cmqResponse.setOperationType(msg.getOperationType());
            cmqResponse.setStreamId(msg.getStreamId());
            try {
                //检查参数
                if (sendRequestBody.check()) {
                    //存储消息到本地服务
                    boolean res = doSend(sendRequestBody);
                    cmqResponse.setResponseStatus(res ? ResponseStatus.OK : ResponseStatus.SERVER_ERROR);
                } else {
                    //参数不正确
                    cmqResponse.setResponseStatus(ResponseStatus.REQUEST_ERROR);
                }
            } catch (Exception e) {
                cmqResponse.setResponseStatus(ResponseStatus.SERVER_ERROR);
                cmqResponse.setResultMessage(e.getMessage());
            } finally {
                //如果当前通道是可写，就写入
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    ctx.writeAndFlush(cmqResponse);
                } else {
                    //如果当前通过不可，暂时丢弃然后打印日志
                    log.error("dropped message: " + cmqResponse);
                }
            }
        }
    }

    private boolean doSend(SendTextMessageRequestBody sendRequestBody) {
        //对于同一条连接 不需要重新创建本地生产者
        if (cmqProducer == null) {
            cmqProducer = cmqBroker.createProducer();
        }
        return cmqProducer.send(sendRequestBody.getTopic(), sendRequestBody.getBody());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
