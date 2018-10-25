package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service(value = "iUserService")
@Transactional
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 登录
     * @param username 用户名
     * @param password 用户密码
     * @return 返回登录结果
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int count = userMapper.isUsername(username);
        if(count == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //todo MD5 密码加密
        String MD5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.login(username,MD5password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    /**
     * 注册
     * @param user 用户信息
     * @return 注册结果
     */
    @Override
    public ServerResponse register(User user) {
        ServerResponse valid = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!valid.isSuccess()){
            return valid;
        }
        valid = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!valid.isSuccess()){
            return valid;
        }
        /* 角色设置 */
        user.setRole(Const.Role.ROLE_CUSTOMER);
        /* 密码加密 */
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int count = this.userMapper.insert(user);
        if(count < 0){
            ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccess("注册成功");
    }

    /**
     * 校验
     * @param str  验证的用户名或者邮箱
     * @param type 用户名或者邮箱类型
     * @return 是否存在
     */
    public ServerResponse checkValid(String str,String type){
        if(StringUtils.isNotBlank(str)){
            if(StringUtils.equals(type, Const.USERNAME)){
                if(userMapper.isUsername(str) > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(StringUtils.equals(type,Const.EMAIL)){
                if(userMapper.isEmail(str) > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else{
            return ServerResponse.createBySuccess("参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    /**
     * @function 通过用户名查找问题
     * @param username 用户名
     * @return 问题查找情况
     */
    @Override
    public ServerResponse<String> selectQuestion(String username){
        ServerResponse valid = this.checkValid(username, Const.USERNAME);
        if(valid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = this.userMapper.selectQuestion(username);
        if(StringUtils.isBlank(question)){
            return ServerResponse.createByErrorMessage("问题提取失败");
        }
        return ServerResponse.createBySuccess("问题成功提取",question);
    }

    /**
     * 验证问题
     * @param username 用户名
     * @param question 问题
     * @param answer 回答
     * @return 是否成功
     */
    @Override
    public ServerResponse<String> validQuestion(String username,String question,String answer){
        int count = this.userMapper.validQuestion(username, question, answer);
        if(count > 0){
            //获取用户的token
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(Const.TOKEN_PRE + username,forgetToken);
            return ServerResponse.createBySuccess("验证成功",forgetToken);
        }
        return ServerResponse.createBySuccess("答案错误");
    }

    /**
     * 未登录修改密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> no_loginUpdatePassword(String username,String passwordNew,String forgetToken){
        String value = TokenCache.getKey(Const.TOKEN_PRE + username);
        if(value == null){
            return ServerResponse.createByErrorMessage("该用户token已过期");
        }
        if(!StringUtils.equals(value,forgetToken)){
            return ServerResponse.createByErrorMessage("请输入正确的Token");
        }
        ServerResponse serverResponse = this.checkValid(username, Const.USERNAME);
        if(serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        if(forgetToken == null){
            return ServerResponse.createByErrorMessage("无forgetToken");
        }
        String MD5passwordNew = MD5Util.MD5EncodeUtf8(passwordNew);
        int count = this.userMapper.no_loginUpdatePassword(username, MD5passwordNew);
        if(count == 0){
            return ServerResponse.createByErrorMessage("密码修改失败");
        }
        return ServerResponse.createBySuccess("密码更改成功");
    }

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 返回用户的信息
     */
    @Override
    public ServerResponse<User> getUserInfo(int userId) {
        User user = this.userMapper.selectByPrimaryKey(userId);
        if(user != null){
            user.setPassword(StringUtils.EMPTY);
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("获取用户信息失败");
    }

    /**
     * 用户信息的修改
     * @param user 用户信息
     * @return 用户信息修改结果
     */
    @Override
    public ServerResponse<String> updateInformation(User user) {
        //用户名不能被更新
        //用户的email需要校验
        int count = this.userMapper.isEmailById(user.getEmail(),user.getId());
        if(count > 0){
            return ServerResponse.createByErrorMessage("email已经存在");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());
        count = this.userMapper.updateByPrimaryKeySelective(updateUser);
        if(count == 0){
            return ServerResponse.createByErrorMessage("修改失败");
        }
        return ServerResponse.createBySuccess("修改成功");
    }

    /**
     * 重置密码
     * @param username 用户名
     * @param password 旧密码
     * @param passwordNew  新密码
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String username, String password, String passwordNew) {
        ServerResponse valid = this.checkValid(username, Const.USERNAME);
        if(valid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String MD5password = MD5Util.MD5EncodeUtf8(password);
        int count = this.userMapper.existUserPass(username,MD5password);
        if(count == 0){
            return ServerResponse.createByErrorMessage("密码不对");
        }
        String MD5passwordNew = MD5Util.MD5EncodeUtf8(passwordNew);
        count = this.userMapper.existUserPass(username,MD5passwordNew);
        if(count > 0){
            return ServerResponse.createByErrorMessage("新密码与旧密码相同");
        }
        count = this.userMapper.no_loginUpdatePassword(username,MD5passwordNew);
        if(count > 0){
            return ServerResponse.createBySuccess("密码修改成功");
        }
        return ServerResponse.createByErrorMessage("密码修改失败");
    }

    /**
     * 验证用户是否是管理员
     * @param user 用户信息
     * @return 返回用户角色
     */
    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess("管理员");
        }
        return ServerResponse.createByErrorMessage("不是管理员");
    }
}
