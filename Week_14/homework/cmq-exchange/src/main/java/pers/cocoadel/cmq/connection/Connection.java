package pers.cocoadel.cmq.connection;

import java.io.IOException;

public interface Connection {
    void connect(String ip, int port) throws IOException;

    void disConnect() throws IOException;

}
