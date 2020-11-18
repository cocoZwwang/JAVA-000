package pers.cocoadel.homework.spring.bean.register;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import pers.cocoadel.homework.spring.bean.domain.User;

/**
 * 通过{@link Import} 注册Bean
 */
//@Import(ImportUserSelector.class)
@Import(ImportUserRegister.class)
public class ImportBeanDefinitionDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ImportBeanDefinitionDemo.class);
        User user = context.getBean(User.class);
        System.out.println("User Bean: " + user);
        context.close();
    }
}


