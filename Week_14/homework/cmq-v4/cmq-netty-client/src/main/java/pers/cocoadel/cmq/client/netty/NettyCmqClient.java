package pers.cocoadel.cmq.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.client.netty.codec.*;
import pers.cocoadel.cmq.netty.comm.OperationType;
import pers.cocoadel.cmq.netty.comm.StreamRequest;
import pers.cocoadel.cmq.netty.comm.codec.CmqFrameDecoder;
import pers.cocoadel.cmq.netty.comm.codec.CmqFrameEncoder;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class NettyCmqClient {

    private ChannelFuture channelFuture;

    private final RequestPendingCenter requestPendingCenter = new RequestPendingCenter();

    private final String ip;

    private final int port;

    private final AtomicLong streamIdCreator = new AtomicLong();

    private ChannelConnectedHandler channelConnectedHandler;


    NettyCmqClient(String ip,int port) {
        this.ip = ip;
        this.port = port;
    }

    void start() {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        channelConnectedHandler = new ChannelConnectedHandler();
        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast("ChannelConnectedHandler",channelConnectedHandler);
                            //帧编解码
                            channelPipeline.addLast("CmqFrameDecoder", new CmqFrameDecoder());
                            channelPipeline.addLast("CmqFrameEncoder", new CmqFrameEncoder());
                            //业务编码
                            channelPipeline.addLast("CmqProtocolEncoder", new CmqProtocolEncoder());
                            channelPipeline.addLast("CmqProtocolDecoder", new CmqProtocolDecoder());
                            channelPipeline.addLast("CmqRequestBodyJsonEncoder", new CmqRequestBodyJsonEncoder());
                            channelPipeline.addLast("CmqResponseBodyJsonDecoder", new CmqResponseBodyJsonDecoder(requestPendingCenter));
                        }
                    });
            channelFuture = bootstrap.connect(ip,port).sync();
            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                workGroup.shutdownGracefully();
                log.info("client shutdown !");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    RequestFuture sendMessage(Object msg, OperationType operationType) {
        if(channelConnectedHandler == null || !channelConnectedHandler.isActive()){
            throw new IllegalStateException("It's not connected yet");
        }
        if (channelFuture != null) {
           Channel channel = channelFuture.channel();
            if (channel.isActive() && channel.isWritable()) {
                StreamRequest<Object> request = new StreamRequest<>();
                request.setStreamId(streamIdCreator.incrementAndGet());
                request.setBody(msg);
                request.setOperationType(operationType);
                RequestFuture requestFuture = new RequestFuture();
                requestPendingCenter.addFuture(request.getStreamId(), requestFuture);
                channel.writeAndFlush(request);
                return requestFuture;
            }
        }
        throw new IllegalStateException("channel is unWriteable!");
    }

    public void close() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }

    public boolean isActive() {
        return channelFuture != null && channelFuture.channel().isActive();
    }

    public ChannelId getChannelId() {
        if (channelFuture != null) {
            return channelFuture.channel().id();
        }
        return null;
    }
}
