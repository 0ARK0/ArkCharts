package com.ark.arkcharts.serviceImpl;

import com.ark.arkcharts.entity.User;
import com.ark.arkcharts.mapper.UserMapper;
import com.ark.arkcharts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ark
 * @date 2020/05/16 12:48
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User findUserByAccountAndPassword(String account, String password) {
        return userMapper.findUserByAccountAndPassword(account, password);
    }

    @Override
    public User findUserByAccount(String account) {
        return userMapper.findUserByAccount(account);
    }

    @Override
    public void saveNewUser(User user) {
        userMapper.saveNewUser(user);
    }
}
