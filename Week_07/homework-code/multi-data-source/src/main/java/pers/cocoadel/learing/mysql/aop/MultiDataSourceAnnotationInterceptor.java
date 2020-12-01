package pers.cocoadel.learing.mysql.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pers.cocoadel.learing.mysql.annotation.DataSource;
import pers.cocoadel.learing.mysql.context.MultiDataSourceHolder;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class MultiDataSourceAnnotationInterceptor implements MethodInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceAnnotationInterceptor.class);


    /**
     * 缓存方法注解值
     */
    private static final Map<Method, String> METHOD_CACHE = new HashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            //根据@DataSource注解的值，选择不同的数据源
            String datasource = determineDatasource(invocation);
            if (! MultiDataSourceHolder.hasDataSourceRouterKey(datasource)) {
                logger.info("数据源[{}]不存在，使用默认数据源 >", datasource);
            }
            // 数据源key 放到当前线程的应用上下文中去
            MultiDataSourceHolder.setDataSourceRouterKey(datasource);
            return invocation.proceed();
        } finally {
            MultiDataSourceHolder.removeDataSourceRouterKey();
        }
    }


    private String determineDatasource(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (METHOD_CACHE.containsKey(method)) {
            return METHOD_CACHE.get(method);
        } else {
            //从方法或者类上标注的注解@DataSource,获取它的值
            DataSource ds = method.isAnnotationPresent(DataSource.class) ? method.getAnnotation(DataSource.class)
                    : AnnotationUtils.findAnnotation(method.getDeclaringClass(), DataSource.class);
            METHOD_CACHE.put(method, ds.value());
            return ds.value();
        }
    }
}
