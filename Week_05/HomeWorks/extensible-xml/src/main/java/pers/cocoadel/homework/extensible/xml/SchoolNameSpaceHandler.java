package pers.cocoadel.homework.extensible.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class SchoolNameSpaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("school", new SchoolBeanDefinitionParse());
        registerBeanDefinitionParser("student", new StudentBeanDefinitionParse());
    }
}
