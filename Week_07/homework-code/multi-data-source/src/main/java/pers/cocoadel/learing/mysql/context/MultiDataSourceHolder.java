package pers.cocoadel.learing.mysql.context;

import java.util.HashSet;
import java.util.Set;

/**
 * 多数据源应用上下文
 */
public class MultiDataSourceHolder {

    private final static ThreadLocal<String> Holder = new ThreadLocal<>();

    private final static Set<String> dataSourceKeys = new HashSet<>();

    public static void addDataSourceRouterKey(String key) {
        dataSourceKeys.add(key);
    }

    public static String getDataSourceRouterKey() {
        return Holder.get();
    }

    public static void setDataSourceRouterKey(String key) {
        if (dataSourceKeys.contains(key)) {
            Holder.set(key);
        }else{
            throw new IllegalArgumentException("数据源 key:" + key +" 不存在！");
        }
    }

    public static void removeDataSourceRouterKey() {
        Holder.remove();
    }

    public static boolean hasDataSourceRouterKey(String key) {
        return dataSourceKeys.contains(key);
    }
}
