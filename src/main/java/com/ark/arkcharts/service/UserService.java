package com.ark.arkcharts.service;

import com.ark.arkcharts.entity.User;

/**
 * @author Ark
 * @date 2020/05/16 12:11
 */
public interface UserService {

    User findUserByAccountAndPassword(String account, String password);

    User findUserByAccount(String account);

    void saveNewUser(User user1);
}
