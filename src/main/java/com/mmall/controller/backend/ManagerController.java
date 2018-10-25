package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class ManagerController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> login = this.iUserService.login(username, password);
        if(login.isSuccess()){
            User user = login.getData();
            ServerResponse<String> validRole = this.iUserService.checkAdminRole(user);
            if(validRole.isSuccess()){
                //说明登录成功
                session.setAttribute(Const.CURRENT_USER,user);
                return login;
            }else{
                return ServerResponse.createByErrorMessage("不是管理员");
            }
        }
        return login;
    }
}
