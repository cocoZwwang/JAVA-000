package pers.cocoadel.cmq.client.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import pers.cocoadel.cmq.netty.comm.StreamRequest;

import java.util.List;

public class CmqRequestBodyJsonEncoder extends MessageToMessageEncoder<StreamRequest<?>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, StreamRequest<?> msg, List<Object> out) throws Exception {

    }
}
