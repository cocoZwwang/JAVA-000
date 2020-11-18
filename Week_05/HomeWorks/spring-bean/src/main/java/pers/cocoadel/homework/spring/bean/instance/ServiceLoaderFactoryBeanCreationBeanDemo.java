package pers.cocoadel.homework.spring.bean.instance;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import pers.cocoadel.homework.spring.bean.domain.UserFactory;

import java.util.ServiceLoader;

/**
 * 通过{@link org.springframework.beans.factory.serviceloader.ServiceLoaderFactoryBean} 装载Bean
 * 是Spring对jdk原生的{@link ServiceLoader}的FactoryBean的封装，用来获取ServiceLoader的Bean对象
 * 获取到ServiceLoader的Bean对象后，就可以像原生ServiceLoader使用方式那样去获取相应的serviceType类型Bean
 * ServiceLoader的使用查看例子{@link ServiceLoaderCreationDemo}
 */
public class ServiceLoaderFactoryBeanCreationBeanDemo {
    public static void main(String[] args) {
        String location = "classpath:META-INF/service-loader-factory-bean.xml";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(location);
        ServiceLoader<UserFactory> loader = context.getBean("userFactoryServiceLoaderFactoryBean", ServiceLoader.class);
        for (UserFactory userFactory : loader) {
            System.out.println("加载UserFactory Bean: " + userFactory.getClass().getName());
            System.out.println("创建User：" + userFactory.createUser());
            System.out.println();
        }
    }
}
