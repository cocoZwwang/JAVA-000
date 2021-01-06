package pers.cocoadel.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import pers.cocoadel.client.netty.HttpResponse;
import pers.cocoadel.client.netty.NettyHttpClient;
import pers.cocoadel.gateway.filter.HttpRequestFilter;
import pers.cocoadel.gateway.router.HttpEndpointRouter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GatewayInboundHandler extends ChannelInboundHandlerAdapter {

    private HttpRequestFilter httpRequestFilter;

    private HttpEndpointRouter httpEndpointRouter;

    private List<String> endpoints;

    private String defaultEndPoint;

    private NettyHttpClient nettyHttpClient;

    public GatewayInboundHandler() {
        nettyHttpClient = NettyHttpClient.getInstance();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        if (httpRequestFilter != null) {
            httpRequestFilter.filter(fullHttpRequest, ctx);
        }
        String uri = fullHttpRequest.uri();
        System.out.println("接收到请求uri: " + uri);
        handlerReq(fullHttpRequest, ctx);
    }

    private void handlerReq(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        String endPoint = defaultEndPoint;
        if (httpEndpointRouter != null) {
            endPoint = httpEndpointRouter.route(endpoints);
        }
        FullHttpResponse response =null;
        try {
            //使用Netty实现的HttpClient访问被代理的服务器
            String[] ss = endPoint.split(":");
            HttpResponse httpResponse = nettyHttpClient.request(ss[0], Integer.parseInt(ss[1]), fullHttpRequest);
//            response = nettyHttpClient.request(ss[0], Integer.parseInt(ss[1]), fullHttpRequest);
            byte[] bytes = httpResponse.getBody();
            ByteBuf byteBuf = ctx.alloc().buffer(httpResponse.getBody().length);
            System.out.println("bytes.length: " + bytes.length);
            byteBuf.writeBytes(httpResponse.getBody());
//            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SERVICE_UNAVAILABLE, byteBuf);
            response = errorResponse(ctx);
            response.headers().add(httpResponse.getHttpHeaders());
            System.out.println("response: " + response);
        }catch (Exception e){
            e.printStackTrace();
            response = errorResponse(ctx);
        }finally {
            if (fullHttpRequest != null) {
                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.writeAndFlush(response);
                }
            }
        }
    }

    private FullHttpResponse errorResponse(ChannelHandlerContext ctx){
        String content = "后台服务器错误！";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ByteBuf byteBuf = ctx.alloc().buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.SERVICE_UNAVAILABLE, byteBuf);
        response.headers().add("Content-Type", "text/html;charset=utf-8");
        response.headers().add("Content-Length", response.content().readableBytes());
        return response;
    }

//    private void handlerReq(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
//        String endPoint = defaultEndPoint;
//        if (httpEndpointRouter != null) {
//            endPoint = httpEndpointRouter.route(endpoints);
//        }
//        FullHttpResponse response =null;
//        try {
//            //使用Netty实现的HttpClient访问被代理的服务器
//            NettyHttpClient nettyHttpClient = new NettyHttpClient();
//            String[] ss = endPoint.split(":");
//            GatewayOutBoundHandler gatewayOutBoundHandler = new GatewayOutBoundHandler(fullHttpRequest);
//            ChannelHandler[] channelHandlers = new ChannelHandler[]{
//                    new HttpClientCodec(),
//                    new HttpObjectAggregator(1024 * 10 * 1024),
//                    new HttpContentDecompressor(),
//                    gatewayOutBoundHandler};
//            nettyHttpClient.connect(ss[0], Integer.parseInt(ss[1]), channelHandlers);
//            response = gatewayOutBoundHandler.getResponse();
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            if (response == null) {
//                String content = "后台服务器错误！";
//                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
//                        HttpResponseStatus.SERVICE_UNAVAILABLE,
//                        Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8)));
//                response.headers().add("Content-Type", "text/html;charset=utf-8");
//                response.headers().add("Content-Length", response.content().readableBytes());
//            }
//
//            if (fullHttpRequest != null) {
//                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
//                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
//                } else {
//                    //response.headers().set(CONNECTION, KEEP_ALIVE);
//                    ctx.write(response);
//                }
//            }
////            ctx.flush();
//        }
//
//    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public void setHttpRequestFilter(HttpRequestFilter httpRequestFilter) {
        this.httpRequestFilter = httpRequestFilter;
    }

    public void setHttpEndpointRouter(HttpEndpointRouter httpEndpointRouter) {
        this.httpEndpointRouter = httpEndpointRouter;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    public void setDefaultEndPoint(String defaultEndPoint) {
        this.defaultEndPoint = defaultEndPoint;
    }
}
