package pers.cocoadel.learning.rpcfx.api;

public class ReflectRpcfxResolver implements RpcfxResolver {

    @Override
    public Object resolve(String serviceClass) {
        try {
            Class<?> clazz = Class.forName(serviceClass);
            return clazz.newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
