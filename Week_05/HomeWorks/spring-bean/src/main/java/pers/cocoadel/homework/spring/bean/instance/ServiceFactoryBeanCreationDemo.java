package pers.cocoadel.homework.spring.bean.instance;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pers.cocoadel.homework.spring.bean.domain.UserFactory;

import java.util.ServiceLoader;

/**
 * 通过{@link org.springframework.beans.factory.serviceloader.ServiceFactoryBean}装载Bean。
 * ServiceFactoryBean本身是一个{@link FactoryBean}的实现
 * 加载Bean的方式是通过{@link ServiceLoader}来完成的，是spring对ServiceLoader进一步封装和应用。
 */
public class ServiceFactoryBeanCreationDemo {
    public static void main(String[] args) {
        String location = "classpath:META-INF/service-factory-bean-creation.xml";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(location);
        //这里不管META-INF/services/pers.cocoadel.homework.spring.bean.domain.UserFactory文件
        //注册了多少个实现类，都只加载第一个实现
        UserFactory userFactory = context.getBean(UserFactory.class);
        System.out.println("加载UserFactory Bean：" + userFactory);
        System.out.println("通过UserFactory 创建 User 对象：" + userFactory.createUser());
        context.close();
    }
}
