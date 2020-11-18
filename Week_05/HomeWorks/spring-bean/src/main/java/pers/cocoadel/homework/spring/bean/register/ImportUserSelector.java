package pers.cocoadel.homework.spring.bean.register;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import pers.cocoadel.homework.spring.bean.domain.User;

public class ImportUserSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{User.class.getName()};
    }
}
