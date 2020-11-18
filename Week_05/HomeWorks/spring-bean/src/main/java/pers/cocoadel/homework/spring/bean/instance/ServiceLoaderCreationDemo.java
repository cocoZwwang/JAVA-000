package pers.cocoadel.homework.spring.bean.instance;


import pers.cocoadel.homework.spring.bean.domain.UserFactory;

import java.util.ServiceLoader;

/**
 * 通过{@link ServiceLoader} 加载Bean，这是Jdk本身自带Bean加载机制
 */
public class ServiceLoaderCreationDemo {
    //先在路径classpath:META-INF/services文件路径下，创建一个文件，该文件名称是你希望创建的Bean的接口或者类的全类名
    //这里是在classpath:META-INF/services文件路径下，创建了文件pers.cocoadel.learning.spring.bean.domain.UserFactory
    //文件内容是希望实例化的实现类的全类名，多个实现类分别写在不同行
    public static void main(String[] args) {
        ServiceLoader<UserFactory> loader =
                ServiceLoader.load(UserFactory.class,Thread.currentThread().getContextClassLoader());
        for (UserFactory userFactory : loader) {
            System.out.println("加载UserFactory Bean: " + userFactory.getClass().getName());
            System.out.println("创建User：" + userFactory.createUser());
            System.out.println();
        }
    }
}
