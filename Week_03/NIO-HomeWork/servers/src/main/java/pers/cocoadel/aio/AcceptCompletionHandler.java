package pers.cocoadel.aio;


import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AIOHttpServer> {

    @Override
    public void completed(AsynchronousSocketChannel result, AIOHttpServer attachment) {
        attachment.serverSocketChannel.accept(attachment, this);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        result.read(byteBuffer, byteBuffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AIOHttpServer attachment) {
        attachment.serverSocketChannel.accept(attachment, this);
    }
}
