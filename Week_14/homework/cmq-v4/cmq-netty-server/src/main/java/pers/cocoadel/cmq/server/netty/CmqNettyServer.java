package pers.cocoadel.cmq.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import pers.cocoadel.cmq.core.broker.CmqBroker;
import pers.cocoadel.cmq.core.spi.ObjectFactory;
import pers.cocoadel.cmq.exchange.ServerExchangeCmqConsumer;
import pers.cocoadel.cmq.exchange.ServerExchangeCmqProducer;
import pers.cocoadel.cmq.netty.comm.codec.CmqFrameDecoder;
import pers.cocoadel.cmq.netty.comm.codec.CmqFrameEncoder;
import pers.cocoadel.cmq.server.netty.codec.*;
import pers.cocoadel.cmq.server.netty.exchange.ExchangeConsumerHandler;
import pers.cocoadel.cmq.server.netty.exchange.ExchangeProducerHandler;

public class CmqNettyServer {
    private final CmqBroker cmqBroker;

    private final ServerExchangeCmqProducer exchangeCmqProducer;

    private final ServerExchangeCmqConsumer exchangeCmqConsumer;

    CmqNettyServer() {
        cmqBroker = ObjectFactory.createObject(CmqBroker.class);
        exchangeCmqConsumer = new ServerExchangeCmqConsumer();
        exchangeCmqConsumer.setCmqBroker(cmqBroker);
        exchangeCmqProducer = new ServerExchangeCmqProducer();
        exchangeCmqProducer.setBroker(cmqBroker);
    }


    public static void main(String[] args) {
        CmqNettyServer cmqNettyServer = new CmqNettyServer();
        cmqNettyServer.start();
    }

    private void start(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //0 ： Netty 默认配置的线程数 = NettyRuntime.availableProcessors() * 2
        final EventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        final EventLoopGroup workGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("work"));
        try {
            serverBootstrap.group(bossGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            //编解码
                            pipeline.addLast("CmqFrameDecoder", new CmqFrameDecoder());
                            pipeline.addLast("CmqFrameEncoder", new CmqFrameEncoder());
                            pipeline.addLast("CmqProtocolDecoder", new CmqProtocolDecoder());
                            pipeline.addLast("CmqProtocolEncoder", new CmqProtocolEncoder());
                            pipeline.addLast("CmqRequestBodyJsonDecoder", new CmqRequestBodyJsonDecoder());
                            pipeline.addLast("CmqRequestBodyJsonDecoder", new CmqRequestBodyJsonDecoder());
                            //日志
                            pipeline.addLast("LoggingHandler", new LoggingHandler());
                            //消息服务处理
                            pipeline.addLast("ExchangeConsumerHandler", new ExchangeConsumerHandler(exchangeCmqConsumer));
                            pipeline.addLast("ExchangeProducerHandler", new ExchangeProducerHandler(exchangeCmqProducer));
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
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
