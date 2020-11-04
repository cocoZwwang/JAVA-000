package pers.cocoadel.aio;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.AsynchronousServerSocketChannel;

public class AIOHttpServer implements Runnable {
    protected int port = 8803;
    protected volatile boolean stopAwait = false;
    protected AsynchronousServerSocketChannel serverSocketChannel;
    private ServerSocket awaitSocket;

    public void bind(int port) {
        this.port = port;
        try {
            serverSocketChannel = AsynchronousServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        serverSocketChannel.accept(this, new AcceptCompletionHandler());
        awaitService();
    }

    private void awaitService() {
        try {
            awaitSocket = new ServerSocket(port + 1, 1, InetAddress.getByName("localhost"));
            while (!stopAwait) {
                try (Socket socket = awaitSocket.accept();
                     BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                    String s = br.readLine();
                    stopAwait = "shutdown".equalsIgnoreCase(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            awaitSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
