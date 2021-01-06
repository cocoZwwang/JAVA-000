package pers.cocoadel.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import pers.cocoadel.client.netty.dispatcher.HttpRequestHttpPendingCenter;

public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private final HttpRequestHttpPendingCenter pendingCenter;

    HttpClientHandler( HttpRequestHttpPendingCenter pendingCenter) {
        this.pendingCenter = pendingCenter;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        String streamId = msg.headers().get(HttpRequestHttpPendingCenter.HEADER_STREAM_ID);
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.setAll(msg.headers());
        ByteBuf byteBuf = msg.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        System.out.println("-----bytes.length: " + bytes.length);
        byteBuf.getBytes(0,bytes);
        headers.remove(HttpRequestHttpPendingCenter.HEADER_STREAM_ID);
        pendingCenter.set(streamId, new HttpResponse(headers,bytes));
    }
}
