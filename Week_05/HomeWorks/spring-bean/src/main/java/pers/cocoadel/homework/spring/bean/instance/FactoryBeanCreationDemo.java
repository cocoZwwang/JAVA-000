package pers.cocoadel.homework.spring.bean.instance;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import pers.cocoadel.homework.spring.bean.domain.Cat;
import pers.cocoadel.homework.spring.bean.domain.User;

/**
 * 通过{@link org.springframework.beans.factory.FactoryBean} 装载Bean
 *
 */
public class FactoryBeanCreationDemo {

    public static void main(String[] args) {
        String location = "classpath:META-INF/factory-bean-creation-context.xml";
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FactoryBeanCreationDemo.class);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(location);

        //FactoryBean装载的Bean
        //通过FactoryBean装载的Bean不会经历完整的 Spring Bean生命周期
        //@PostConstruct和@PreDestroy 不会被调用
        User user = context.getBean(User.class);
        System.out.println("User Bean: " + user);

        //正常注解加载的Bean
        //@PostConstruct和@PreDestroy会被正常调用
        Cat cat = context.getBean(Cat.class);
        System.out.println("Cat Bean:" + cat);

        // 关闭Spring应用上下文
        context.close();
    }

    @Bean
    public Cat tomCat() {
        Cat cat = new Cat();
        cat.setName("Tom");
        cat.setAge(15);
        cat.setDescription("I'm Tom that created by @Bean annotation, Jerry's good friend, not Tomcat!");
        return cat;
    }
}
