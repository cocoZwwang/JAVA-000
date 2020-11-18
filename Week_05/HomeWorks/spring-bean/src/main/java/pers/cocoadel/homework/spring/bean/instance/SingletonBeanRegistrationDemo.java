package pers.cocoadel.homework.spring.bean.instance;

import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pers.cocoadel.homework.spring.bean.domain.User;

/**
 * 外部单例加载Bean
 */
public class SingletonBeanRegistrationDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        SingletonBeanRegistry singletonBeanRegistry = context.getBeanFactory();
        User user = new User();
        user.setName("Black Belladonna");
        user.setAge(14);
        user.setDescription("I am from singletonBeanRegistry");
        singletonBeanRegistry.registerSingleton("blackBelladonna", user);
        context.refresh();

        User black = context.getBean(User.class);
        System.out.println("User Bean: " + black);

        context.close();
    }
}
