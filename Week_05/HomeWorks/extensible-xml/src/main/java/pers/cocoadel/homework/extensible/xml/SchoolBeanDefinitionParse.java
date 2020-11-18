package pers.cocoadel.homework.extensible.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import pers.cocoadel.homework.extensible.xml.domain.School;

public class SchoolBeanDefinitionParse extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return School.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        setPropertyValue("id",element,builder);
        setPropertyValue("name", element, builder);
        setPropertyValue("address", element, builder);
    }

    private void setPropertyValue(String attributeName, Element element, BeanDefinitionBuilder builder) {
        String value = element.getAttribute(attributeName);
        if (StringUtils.hasText(value)) {
            builder.addPropertyValue(attributeName, value);
        }
    }
}
