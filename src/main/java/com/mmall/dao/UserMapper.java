package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int isUsername(String username);

    User login(@Param("username") String username,@Param("password") String password);

    //email存在
    int isEmail(@Param("email") String email);

    //email存在在userId下
    int isEmailById(@Param("email")String email ,@Param("userId")int userId);

    String selectQuestion(@Param("username") String username);

    int validQuestion(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);

    int no_loginUpdatePassword(@Param("username") String username,@Param("passwordNew") String passwordNew);

    int existUserPass(@Param("username") String username,@Param("password") String password);
}