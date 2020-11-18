package pers.cocoadel.homework.spring.bean.domain;

public class DefaultUserFactory  implements UserFactory{

    @Override
    public User createUser() {
        User user = new User();
        user.setName("Yang XiaoLong");
        user.setAge(16);
        user.setDescription("I am created by DefaultUserFactory");
        return user;
    }
}
