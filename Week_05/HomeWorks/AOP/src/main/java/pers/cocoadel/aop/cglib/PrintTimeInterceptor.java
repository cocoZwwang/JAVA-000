package pers.cocoadel.aop.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class PrintTimeInterceptor implements MethodInterceptor {

    public Object getInstance(Class<?> clazz){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object value = methodProxy.invokeSuper(o, objects);
        long time = System.currentTimeMillis() - startTime;
        System.out.printf("invoke method took %s ms\n",time);
        return value;
    }
}
