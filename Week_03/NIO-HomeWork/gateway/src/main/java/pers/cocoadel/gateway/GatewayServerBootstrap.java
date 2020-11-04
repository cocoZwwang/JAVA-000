package pers.cocoadel.gateway;

public class GatewayServerBootstrap {
    public static void main(String[] args) {
        GateWayServer httpServer = new GateWayServer();
        httpServer.bind(8808);
    }
}
