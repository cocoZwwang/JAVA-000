package pers.cocoadel.aio;

import com.sun.deploy.net.HttpResponse;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private final AsynchronousSocketChannel socketChannel;

    public ReadCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] bytes = new byte[attachment.remaining()];
        attachment.get(bytes);
        doWrite(getHeader("nio",bytes));
    }

    private String getHeader(String key, byte[] bytes) {
        String value = "";
        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)))){
            String header = null;
            do {
                try {
                    header = bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (header != null && !header.startsWith(key));

            if(header != null){
                value = header.split(":")[1];
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return value.trim();
    }

    private void doWrite(String value) {
        String stringBuilder = "HTTP/1.1 200 OK\r\n" +
                "Content-Type:text/html;charset=utf-8\r\n" +
                "\r\n" +
                String.format("hello %s! I am AIO Server !",value);
        byte[] bytes = stringBuilder.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        socketChannel.write(byteBuffer, byteBuffer, new WriteCompletionHandler(socketChannel));
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel socketChannel;

        public WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            //如果没有发送完成，继续发送
            if (attachment.hasRemaining()) {
                socketChannel.write(attachment, attachment, this);
            }else {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
