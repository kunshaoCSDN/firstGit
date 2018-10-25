package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse register(User user);
    ServerResponse<String> selectQuestion(String username);
    ServerResponse checkValid(String str,String type);
    ServerResponse<String> validQuestion(String username,String question,String answer);
    ServerResponse<String> no_loginUpdatePassword(String username,String passwordNew,String forgetToken);
    ServerResponse<User> getUserInfo(int userId);
    ServerResponse<String> updateInformation(User user);
    ServerResponse<String> resetPassword(String username,String password,String passwordNew);
    ServerResponse<String> checkAdminRole(User user);
}
