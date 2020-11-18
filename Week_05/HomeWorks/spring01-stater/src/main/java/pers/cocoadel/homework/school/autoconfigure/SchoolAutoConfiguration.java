package pers.cocoadel.homework.school.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(SchoolImportBeanDefinitionRegistry.class)
@Configuration
//所以做一个简单的条件装配，如果在classpath下没有发现spring01/resources/applicationContext.xml文件则不装配
@ConditionalOnResource(resources = "applicationContext.xml")
public class SchoolAutoConfiguration {

}
