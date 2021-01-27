package pers.cocoadel.cmq.client.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pers.cocoadel.cmq.netty.comm.ByteStreamRequest;

public class CmqProtocolEncoder extends MessageToByteEncoder<ByteStreamRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteStreamRequest msg, ByteBuf out) throws Exception {

    }
}
