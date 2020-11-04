package pers.cocoadel.netty;

/**
 * 基于Netty实现的HttpServer
 */
public class HttpServerBootstrap {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        httpServer.bind(8802);
    }
}
