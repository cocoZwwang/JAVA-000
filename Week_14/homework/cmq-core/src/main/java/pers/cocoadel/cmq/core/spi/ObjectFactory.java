package pers.cocoadel.cmq.core.spi;


import java.util.Iterator;
import java.util.ServiceLoader;

public class ObjectFactory {

    public static <T> T createObject(Class<T> type) {
        ServiceLoader<T> loader =
                ServiceLoader.load(type, Thread.currentThread().getContextClassLoader());
        Iterator<T> cmqBrokers = loader.iterator();
        return cmqBrokers.hasNext() ? cmqBrokers.next() : null;
    }
}
