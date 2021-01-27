package pers.cocoadel.cmq.client.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import pers.cocoadel.cmq.comm.request.AuthRequestBody;
import pers.cocoadel.cmq.comm.request.CommRequestBody;
import pers.cocoadel.cmq.comm.response.AuthResponseBody;
import pers.cocoadel.cmq.connection.Connection;
import pers.cocoadel.cmq.core.consumer.CmqConsumer;
import pers.cocoadel.cmq.core.producer.CmqProducer;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class CmqTopicNettyConnection implements Connection  {

    private AtomicLong streamCreator = new AtomicLong();

    private ChannelFuture channelFuture;

    private RequestPendingCenter requestPendingCenter;

    private static final Long TIME_OUT = 1000L * 5;

    private String token;

    @Override
    public void connect(String ip, int port) throws IOException {
        Channel channel = channelFuture.channel();
        if (channel.isActive() && channel.isWritable()) {
            StreamRequest<AuthRequestBody> streamRequest = new StreamRequest<>();
            streamRequest.setStreamId(streamCreator.incrementAndGet());
            streamRequest.setOperationType(OperationType.AUTH);
            AuthRequestBody authRequestBody = new AuthRequestBody();
            authRequestBody.setName("admin");
            authRequestBody.setPassword("123456");
            streamRequest.setBody(authRequestBody);
            RequestFuture requestFuture = new RequestFuture();
            requestPendingCenter.addFuture(streamRequest.getStreamId(), requestFuture);
            channel.writeAndFlush(streamRequest);
            try {
                StreamResponse response = requestFuture.get(TIME_OUT, TimeUnit.MILLISECONDS);
                if (response.isSuccessful()) {
                    AuthResponseBody authResponseBody = (AuthResponseBody) response.getBody();
                    token = authResponseBody.getToken();
                    return;
                }
                throw new IOException(response.getResultMessage());
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    @Override
    public void disConnect() throws IOException {
        doClose();
    }

    private void doClose() {

    }

    @Override
    public <T> CmqConsumer<T> createConsumer(String topic, String groupId, Class<T> tClass) {
        return null;
    }

    @Override
    public CmqProducer createProducer() {
        return null;
    }
}
