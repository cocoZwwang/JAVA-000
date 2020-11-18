package pers.cocoadel.homework.spring.bean.register;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import pers.cocoadel.homework.spring.bean.domain.User;

/**
 * 通过注解的方式
 */
@ComponentScan(basePackageClasses = AnnotationBeanDefinitionDemo.class)
public class AnnotationBeanDefinitionDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AnnotationBeanDefinitionDemo.class);
        User user = context.getBean(User.class);
        System.out.println("user bean: " + user);

        UserRuby ruby = context.getBean(UserRuby.class);
        System.out.println("UserRuby bean: " + ruby);
        context.close();
    }

    /**
     *  通过@Bean方式装配Bean
     */
    @Bean
    public User user(){
        User user = new User();
        user.setName("Weiss");
        user.setAge(14);
        user.setDescription("I am created by Bean annotation");
        return user;
    }

    @Component
    public static class UserRuby {
        private final User user;

        public UserRuby() {
            this.user = new User();
            user.setName("Ruby Rose");
            user.setAge(15);
            user.setDescription("I am created by Component annotation");
        }

        @Override
        public String toString() {
            return "UserRuby{" +
                    "user=" + user +
                    '}';
        }
    }
}
