package pers.cocoadel.homework.spring.bean.domain;

import org.springframework.beans.factory.FactoryBean;

public class UserFactoryBean implements FactoryBean<User> {
    @Override
    public User getObject() throws Exception {
        User user = new User();
        user.setName("CocoAdel");
        user.setAge(18);
        user.setDescription("I am created from UserFactoryBean!");
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }
}
