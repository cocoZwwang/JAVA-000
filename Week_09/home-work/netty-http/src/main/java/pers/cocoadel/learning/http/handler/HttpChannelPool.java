package pers.cocoadel.learning.http.handler;

import io.netty.channel.Channel;

public interface HttpChannelPool {

    int idleChannelCount();

    int channelCount();

    Channel getChannel(String host,int port);

    void clean();
}
