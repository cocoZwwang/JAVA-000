package pers.cocoadel.cmq.client.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import pers.cocoadel.cmq.netty.comm.StreamResponse;

import java.util.List;

public class CmqResponseBodyJsonDecoder extends MessageToMessageDecoder<StreamResponse> {

    @Override
    protected void decode(ChannelHandlerContext ctx, StreamResponse msg, List<Object> out) throws Exception {

    }
}
