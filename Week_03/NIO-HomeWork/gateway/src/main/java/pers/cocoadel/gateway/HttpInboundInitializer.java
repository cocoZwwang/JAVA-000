package pers.cocoadel.gateway;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import pers.cocoadel.gateway.filter.DefaultHttpRequestFilter;
import pers.cocoadel.gateway.router.RandomHttpEndpointRouter;

import java.util.Arrays;

public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast(new HttpServerCodec());
        channelPipeline.addLast(new HttpObjectAggregator(1024 * 1024));
        //
        GatewayInboundHandler gatewayInboundHandler = new GatewayInboundHandler();
        gatewayInboundHandler.setDefaultEndPoint("127.0.0.1:8802");
        gatewayInboundHandler.setEndpoints(Arrays.asList("127.0.0.1:8801","127.0.0.1:8802","127.0.0.1:8803"));
        //请求过滤器
        gatewayInboundHandler.setHttpRequestFilter(new DefaultHttpRequestFilter());
        //随机路由
        gatewayInboundHandler.setHttpEndpointRouter(new RandomHttpEndpointRouter());
        channelPipeline.addLast(gatewayInboundHandler);
    }
}
