package pers.cocoadel.homework.spring.bean.domain;

public class UserFactoryImpl implements UserFactory {

    @Override
    public User createUser() {
        User user = new User();
        user.setName("Black Belladonna");
        user.setAge(15);
        user.setDescription("I am created by UserFactoryImpl");
        return user;
    }
}
