package pers.cocoadel.cmq.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import pers.cocoadel.cmq.core.spi.ObjectFactory;
import pers.cocoadel.cmq.server.codec.CmqFrameDecoder;
import pers.cocoadel.cmq.server.codec.CmqFrameEncoder;
import pers.cocoadel.cmq.server.codec.CmqProtocolDecoder;
import pers.cocoadel.cmq.server.codec.CmqProtocolEncoder;
import pers.cocoadel.cmq.server.exchange.ExchangeBroker;
import pers.cocoadel.cmq.server.exchange.PollExchangeHandler;
import pers.cocoadel.cmq.server.exchange.SendExchangeHandler;

public class CmqServerBootstrap {

    public static void main(String[] args) {
        CmqServerBootstrap cmqServerBootstrap = new CmqServerBootstrap();
        cmqServerBootstrap.start();
    }

    public void start(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        final EventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        final EventLoopGroup workGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("work"));
        //获取 broker
        final ExchangeBroker exchangeBroker = ObjectFactory.createObject(ExchangeBroker.class);
        try {
            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            //
                            pipeline.addLast("",new CmqFrameDecoder());
                            pipeline.addLast("",new CmqFrameEncoder());
                            // 协议解析
                            pipeline.addLast("",new CmqProtocolDecoder());
                            pipeline.addLast("",new CmqProtocolEncoder());
                            // 服务处理
                            pipeline.addLast("",new PollExchangeHandler(exchangeBroker));
                            pipeline.addLast("",new SendExchangeHandler(exchangeBroker));
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) channelFuture1 -> {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
                System.out.println("server shutdown !");
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
