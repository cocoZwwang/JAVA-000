package pers.cocoadel.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class NettyHttpClient {
    public void connect(String host, int port) {
        ChannelHandler[] channelHandlers = new ChannelHandler[]{
                //包含编码器和解码器
                new HttpClientCodec(),
                //聚合
                new HttpObjectAggregator(1024 * 10 * 1024),
                //解压
                new HttpContentDecompressor(),
                new DefaultHttpClientHandler()};
        connect(host,port,channelHandlers);
    }

    public void connect(String host, int port, ChannelHandler... clientHandlers) {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            if (clientHandlers != null) {
                                socketChannel.pipeline().addLast(clientHandlers);
                            }
                        }
                    });
            ChannelFuture cf = bootstrap.connect().sync();
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            System.out.println("http 客户端关闭");
        }
    }
}
