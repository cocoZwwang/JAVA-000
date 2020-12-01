package pers.cocoadel.learing.mysql.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 多数据源
 * spring boot 的多数据源最主要是通过AbstractRoutingDataSource来实现的
 */
public class CustomMultiDataSource extends AbstractRoutingDataSource{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMultiDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        //取出当前线程应用上下的数据源的key
        String dataSourceName = MultiDataSourceHolder.getDataSourceRouterKey();
        LOGGER.info("当前数据源是：{}",dataSourceName);
        return dataSourceName;
    }
}
