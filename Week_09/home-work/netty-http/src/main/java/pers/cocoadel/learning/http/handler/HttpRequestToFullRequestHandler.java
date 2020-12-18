package pers.cocoadel.learning.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.*;
import pers.cocoadel.learning.http.HttpRequest;
import pers.cocoadel.learning.http.HttpUrlInfo;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 用户请求转换成FullHttpRequest
 */
public class HttpRequestToFullRequestHandler extends MessageToMessageEncoder<HttpRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpRequest request, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer(request.getContent() == null ? 0 : request.getContent().length);
        if (request.getContent() != null && request.getContent().length > 0) {
            byteBuf.writeBytes(request.getContent());
        }
        HttpUrlInfo httpUrlInfo = request.parseUrl();
        FullHttpRequest fullHttpRequest =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, request.getHttpMethod(), httpUrlInfo.getPath(), byteBuf);
        if (request.getHttpHeaders() != null) {
            fullHttpRequest.headers().add(request.getHttpHeaders());
        }
        fullHttpRequest.headers()
                .set(HttpHeaderNames.HOST, InetSocketAddress.createUnresolved(httpUrlInfo.getHost(), httpUrlInfo.getPort()))
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                .set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP.toString())
                .set(HttpHeaderNames.USER_AGENT, "Netty Http Client/1.0")
                .set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
        out.add(fullHttpRequest);
    }
}
