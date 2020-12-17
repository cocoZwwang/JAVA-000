package pers.cocoadel.learning.http;

import io.netty.channel.Channel;

public interface HttpChannelPool {

    int idleChannelCount();

    int channelCount();

    Channel getChannel(String host,int port);

    void clean();
}
