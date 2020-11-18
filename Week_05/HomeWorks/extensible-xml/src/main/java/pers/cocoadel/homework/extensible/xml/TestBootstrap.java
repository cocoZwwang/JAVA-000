package pers.cocoadel.homework.extensible.xml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pers.cocoadel.homework.extensible.xml.domain.School;
import pers.cocoadel.homework.extensible.xml.domain.Student;

import java.util.List;

public class TestBootstrap {
    @Autowired
    private School school;

    @Autowired
    private List<Student> students;

    public static void main(String[] args) {
        //构建spring应用上下文
        String location = "classpath:META-INF/beans-context.xml";
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        //加载XML配置文件
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(location);
        //注册引导类
        context.register(TestBootstrap.class);
        //刷新spring 应用上下文
        context.refresh();
        //打印依赖注入属性
        TestBootstrap testBootstrap = context.getBean(TestBootstrap.class);
        System.out.println(testBootstrap.school);
        for (Student student : testBootstrap.students) {
            System.out.println(student);
        }
        //关闭spring应用上下文
        context.close();
    }
}
