package com.ark.arkcharts.mapper;

import com.ark.arkcharts.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * @author Ark
 * @date 2020/05/16 12:54
 */
public interface UserMapper {

    @Select("SELECT * FROM user WHERE account = #{account} AND password = #{password}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name")
    })
    User findUserByAccountAndPassword(String account, String password);

    @Select("SELECT * FROM user WHERE account = #{account}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name")
    })
    User findUserByAccount(String account);

    @Insert("INSERT INTO user VALUES(#{userId}, #{account}, #{password}, #{userName}, #{level})")
    void saveNewUser(User user);
}
