package pers.cocoadel.learning.rpc.demo.provider;


import pers.cocoade.learning.rpc.api.User;
import pers.cocoade.learning.rpc.api.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
