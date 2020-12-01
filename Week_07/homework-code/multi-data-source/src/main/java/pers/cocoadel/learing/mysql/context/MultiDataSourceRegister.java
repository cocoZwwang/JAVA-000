package pers.cocoadel.learing.mysql.context;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MultiDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceRegister.class);

    /**
     * 配置上下文（也可以理解为配置文件的获取工具）
     */
    private Environment evn;

    /**
     * 别名
     */
    private static final ConfigurationPropertyNameAliases ALIASES = new ConfigurationPropertyNameAliases();
    static {
        ALIASES.addAliases("url", "jdbc-url");
        ALIASES.addAliases("username", "user");
    }

    /**
     * 存储数据源
     */
    private Map<String, DataSource> customDataSources = new HashMap<>();


    /**
     * 参数绑定工具
     */
    Binder binder;

    /**
     * 注册多数据源
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取数据源属性
        Map defaultDataSourceProperties = binder.bind("spring.datasource.master", Map.class).get();
        //获取数据源类型字符串
        String typeText = evn.getProperty("spring.datasource.master.type");
        //获取数据源类型
        Class<? extends DataSource> type = getDataSourceType(typeText);
        //创建默认数据源并且绑定参数
        DataSource defaultDataSource = bind(type,defaultDataSourceProperties);
        //添加默认数据源key到应用上下文
        MultiDataSourceHolder.addDataSourceRouterKey("master");
        //获取非默认数据源属性
        logger.info("注册默认数据源成功");
        List<Map> config = binder.bind("spring.datasource.cluster", Bindable.listOf(Map.class)).get();
        for (int i = 0; i < config.size(); i++) {
            //重复上面的步骤创建新数据源，并且存储到自定义Map里面去
            Map customDataSourceProperties = config.get(i);
            type = getDataSourceType((String) customDataSourceProperties.get("type"));
            DataSource customDataSource = bind(type, customDataSourceProperties);
            String dataSourceName = (String) customDataSourceProperties.get("key");
            MultiDataSourceHolder.addDataSourceRouterKey(dataSourceName);
            customDataSources.put(dataSourceName, customDataSource);
            logger.info("注册自定义数据源{}成功",dataSourceName);
        }
        //创建CustomMultiDataSource的BeanDefinition
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(CustomMultiDataSource.class);
        //BeanDefinition 添加属性 默认数据源 和 自定义数据源
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        propertyValues.add("defaultTargetDataSource",defaultDataSource);
        propertyValues.add("targetDataSources",customDataSources);
        //注册BeanDefinition
        registry.registerBeanDefinition("multiDataSource", beanDefinition);
    }

    /**
     * 通过字符串获取数据源类型
     */
    private Class<? extends DataSource> getDataSourceType(String className) {
        Class<? extends DataSource> type = null;
        if (StringUtils.hasText(className)) {
            try {
                type =  (Class<? extends DataSource>) Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(type == null){
            logger.info("获取数据源{}失败，采用默认数据源{}",className,"HikariDataSource");
            type = HikariDataSource.class;
        }
        return type;
    }

    /**
     * 绑定参数，以下三个方法都是参考DataSourceBuilder的bind方法实现的，目的是尽量保证我们自己添加的数据源构造过程与springboot保持一致
     */
    private void bind(DataSource dataSource,Map properties) {
        ConfigurationPropertySource propertySource = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(propertySource.withAliases(ALIASES));
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(dataSource));
    }

    /**
     * 通过属性和类名称，生成对象
     */
    private <T extends DataSource> T bind(Class<T> tClass,Map properties){
        ConfigurationPropertySource propertySource = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(propertySource.withAliases(ALIASES));
        return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(tClass)).get();
    }

    private <T extends  DataSource> T bind(Class<T> tClass,String sourcePath) {
        Map config = binder.bind(sourcePath, Map.class).get();
        return bind(tClass, config);
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.evn = environment;
        this.binder = Binder.get(environment);
    }
}
