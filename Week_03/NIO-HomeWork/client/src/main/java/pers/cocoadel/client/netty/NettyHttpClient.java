package pers.cocoadel.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import pers.cocoadel.client.netty.dispatcher.HttpRequestHttpPendingCenter;
import pers.cocoadel.client.netty.dispatcher.HttpResponseFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class NettyHttpClient {
    private EventLoopGroup workGroup;

    private Bootstrap bootstrap;

    private HttpRequestHttpPendingCenter requestHttpPendingCenter;

    private StreamIdCreator idCreator = StreamIdCreatorFactory.getDefaultStreamIdCreator();

    private static volatile NettyHttpClient nettyHttpClient;

    private final Map<String, ChannelFuture> connectChannelFutureMap = new ConcurrentHashMap<>();

    private NettyHttpClient(){

    }

    public static NettyHttpClient getInstance() {
        if(nettyHttpClient == null){
            synchronized (NettyHttpClient.class){
                if(nettyHttpClient == null){
                    nettyHttpClient = new NettyHttpClient();
                    nettyHttpClient.init();
                }
            }
        }
        return nettyHttpClient;
    }

    private void init() {
        try {
            workGroup = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            requestHttpPendingCenter = new HttpRequestHttpPendingCenter();
            bootstrap.channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("httpClientCodec", new HttpClientCodec());
                            pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(1024 * 10 * 1024));
                            pipeline.addLast("HttpContentDecompressor", new HttpContentDecompressor());
                            pipeline.addLast("HttpClientHandler", new HttpClientHandler(requestHttpPendingCenter));
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse request(String host, int port, FullHttpRequest request){
        try {
            String streamId = idCreator.nextId() + "";
            HttpResponseFuture future = new HttpResponseFuture();
            request.headers().add(HttpRequestHttpPendingCenter.HEADER_STREAM_ID,streamId);
            requestHttpPendingCenter.add(streamId,future);
            ChannelFuture channelFuture = getConnectChannelFuture(host, port);
            channelFuture.channel().writeAndFlush(request);
            HttpResponse response = future.get();
            System.out.println("get FullHttpResponse!");
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public ChannelFuture getConnectChannelFuture(String host,int port){
        String key = host + ":" + port;
        if (!connectChannelFutureMap.containsKey(key)) {
            synchronized (connectChannelFutureMap){
                if(!connectChannelFutureMap.containsKey(key)){
                    ChannelFuture channelFuture = connect(host, port);
                    connectChannelFutureMap.put(key, channelFuture);
                }
            }
        }
        return connectChannelFutureMap.get(key);
    }

    public ChannelFuture connect(String host, int port) {
        try {
            return bootstrap.connect(host,port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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


    //    public void connect(String host, int port) {
//        ChannelHandler[] channelHandlers = new ChannelHandler[]{
//                //包含编码器和解码器
//                new HttpClientCodec(),
//                //聚合
//                new HttpObjectAggregator(1024 * 10 * 1024),
//                //解压
//                new HttpContentDecompressor(),
//                new DefaultHttpClientHandler()};
//        connect(host, port, channelHandlers);
//    }


}
