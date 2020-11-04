package pers.cocoadel.aio;

/**
 * 基于AIO实现的HttpServer
 */
public class AIOHttpServerBootstrap {
    public static void main(String[] args) {
        AIOHttpServer aioHttpServer = new AIOHttpServer();
        aioHttpServer.bind(8803);
        aioHttpServer.run();
    }
}
