package pers.cocoadel.learning.http.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import pers.cocoadel.learning.http.event.HttpStageEvent;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ChannelHandler.Sharable
public class DefaultHttpChannelPool extends ChannelDuplexHandler implements HttpChannelPool {
    private final Map<String, ChannelGroup> idleChannelGroupMap = new ConcurrentHashMap<>();

    private final Map<String, ChannelGroup> channelGroupMap = new ConcurrentHashMap<>();

    private final Bootstrap bootstrap;

    private static final Object PRESENT = new Object();

    private final AtomicInteger channelCount = new AtomicInteger(0);

    private int maxChannelCount = 50;

    public DefaultHttpChannelPool(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public DefaultHttpChannelPool(Bootstrap bootstrap,int maxChannelCount) {
        this.bootstrap = bootstrap;
        this.maxChannelCount = maxChannelCount;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        String key = channelKey((InetSocketAddress) socketAddress);
        log.info("socketAddress:{} channel active!", key);
        channelCount.incrementAndGet();
        ChannelGroup channelGroup = channelGroupMap.computeIfAbsent(key, ChannelGroup::new);
        channelGroup.add(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        String key = channelKey((InetSocketAddress) socketAddress);
        log.info("socketAddress{} channel Inactive!\n", key);

        //删除连接缓存
        ChannelGroup channelGroup = channelGroupMap.get(key);
        if (channelGroup != null) {
            channelGroup.delete(ctx.channel());
        }
        channelCount.decrementAndGet();
        //删除空闲连接缓存
        channelGroup = idleChannelGroupMap.get(key);
        if (channelGroup != null) {
            channelGroup.delete(ctx.channel());
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //表示一次Http会话结束，变成空闲连接
        if (evt == HttpStageEvent.HTTP_STAGE_COMPLETED_EVENT) {
            //如果当前连接池里数量小于最大限制数，则把当前空闲连接回收，否则直接关闭
            if (channelCount() < maxChannelCount) {
                SocketAddress socketAddress = ctx.channel().remoteAddress();
                String key = channelKey((InetSocketAddress) socketAddress);
                ChannelGroup channelGroup = idleChannelGroupMap.computeIfAbsent(key, ChannelGroup::new);
                channelGroup.add(ctx.channel());
            } else {
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public int idleChannelCount() {
        return idleChannelGroupMap
                .values()
                .stream()
                .mapToInt(Map::size)
                .sum();
    }

    @Override
    public int channelCount() {
        return channelCount.get();
    }

    @Override
    public int maxChannelCount() {
        return maxChannelCount;
    }

    @Override
    public Channel getChannel(String host, int port) {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(host, port);
        String key = channelKey(socketAddress);
        Channel channel = null;
        if (idleChannelGroupMap.containsKey(key)) {
            ChannelGroup channelGroup = idleChannelGroupMap.get(key);
            channel = channelGroup.get();
        }
        if(channel == null){
            try {
                channel = bootstrap.connect(host,port).sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return channel;
    }

    @Override
    public void clean() {
        channelGroupMap.values().forEach(ConcurrentHashMap::clear);
        channelGroupMap.clear();
        idleChannelGroupMap.values().forEach(ConcurrentHashMap::clear);
        idleChannelGroupMap.clear();
    }

    private String channelKey(InetSocketAddress socketAddress){
        return socketAddress.getHostName() + ":" + socketAddress.getPort();
    }


    private static class ChannelGroup extends ConcurrentHashMap<Channel, Object> {

        private static final long serialVersionUID = 187702158238730620L;

        private ChannelGroup(String key) {
            this.key = key;
        }

        private String key;

        private Channel get() {
            Set<Channel> channelSet = keySet();
            Channel channel = channelSet.stream().findFirst().orElse(null);
            if (channel != null) {
                remove(channel);
            }
            return channel;
        }

        private void delete(Channel channel) {
            remove(channel);
        }

        public void add(Channel channel) {
            put(channel, PRESENT);
        }

        public String getKey() {
            return key;
        }
    }
}
