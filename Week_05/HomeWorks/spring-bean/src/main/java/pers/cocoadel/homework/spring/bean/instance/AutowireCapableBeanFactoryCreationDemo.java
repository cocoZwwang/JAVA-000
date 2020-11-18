package pers.cocoadel.homework.spring.bean.instance;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pers.cocoadel.homework.spring.bean.domain.DefaultUserFactory;
import pers.cocoadel.homework.spring.bean.domain.UserFactory;

public class AutowireCapableBeanFactoryCreationDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.refresh();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        //这里参数一定要是实现类，不能是接口或者抽象类
        UserFactory userFactory = beanFactory.createBean(DefaultUserFactory.class);
        System.out.println("创建UserFactory对象：" + userFactory);
        //但是依赖查找会抛出异常，说明通过上面方法创建的对象，没有加载到Spring IOC容器里面去
        userFactory = context.getBean(UserFactory.class);
        context.close();
    }
}
