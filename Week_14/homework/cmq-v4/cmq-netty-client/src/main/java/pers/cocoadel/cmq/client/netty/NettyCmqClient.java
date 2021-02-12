package pers.cocoadel.cmq.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.cmq.netty.comm.StreamRequest;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class NettyCmqClient {

    private ChannelFuture channelFuture;

    private final RequestPendingCenter requestPendingCenter = new RequestPendingCenter();

    private final String ip;

    private final int port;

    private final AtomicLong streamIdCreator = new AtomicLong();


    NettyCmqClient(String ip,int port) {
        this.ip = ip;
        this.port = port;
    }

    void start() {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {

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

    RequestFuture sendMessage(Object msg) {
        if (channelFuture != null) {
           Channel channel = channelFuture.channel();
            if (channel.isActive() && channel.isWritable()) {
                StreamRequest<?> request = new StreamRequest<>();
                request.setStreamId(streamIdCreator.incrementAndGet());
                RequestFuture requestFuture = new RequestFuture();
                requestPendingCenter.addFuture(request.getStreamId(), requestFuture);
                channel.writeAndFlush(request);
                return requestFuture;
            }
        }
        return null;
    }
}
