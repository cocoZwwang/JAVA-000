package pers.cocoadel.learning.http.handler;

import io.netty.channel.Channel;

public interface HttpChannelPool {

    int idleChannelCount();

    int channelCount();

    int maxChannelCount();

    Channel getChannel(String host,int port) throws RuntimeException;

    void clean();
}
