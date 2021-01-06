package pers.cocoadel.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.LinkedBlockingDeque;
import static java.util.concurrent.ThreadPoolExecutor.*;

/**
 * 基于BIO实现的HttpServer
 */
public class BioHttpServer {
    private final static int THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2;
    private static  final ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(THREAD_NUM,THREAD_NUM,1, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(1000),Executors.defaultThreadFactory(),new AbortPolicy());
    private static final  int port = 8801;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                threadPool.execute(() -> {
                    service(socket);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void service(Socket socket){
        try {
            Thread.sleep(20);
//            String value = getHeader("nio",socket);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            printWriter.println();
            printWriter.write(String.format("hello %s! I am BIO Server !","value"));
            printWriter.close();
            socket.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String getHeader(String key, Socket socket) {
        String value = "";
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
}
