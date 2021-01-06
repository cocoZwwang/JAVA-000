package pers.cocoadel.client.netty;

public class StreamIdCreatorFactory {

    public static StreamIdCreator getDefaultStreamIdCreator(){
        return  new StreamIdCreatorImpl();
    }
}
