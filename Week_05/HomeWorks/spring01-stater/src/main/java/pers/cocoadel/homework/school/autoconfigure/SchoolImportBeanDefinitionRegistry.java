package pers.cocoadel.homework.school.autoconfigure;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;


public class SchoolImportBeanDefinitionRegistry implements ImportBeanDefinitionRegistrar {
    private final static String LOCATION = "classpath:applicationContext.xml";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        //代替 SpringDemo01 里面的main方法 注册Bean XML资源
        //把Bean装载到引用项目的Bean容器中
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(registry);
        reader.loadBeanDefinitions(LOCATION);
    }
}
