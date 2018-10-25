package com.mmall.controller.portal;

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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> login = iUserService.login(username, password);
        if(login.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,login.getData());
        }
        return login;
    }

    @RequestMapping(value = "/logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse loginOut(HttpSession session){
        if(session != null){
            session.removeAttribute(Const.CURRENT_USER);
            return ServerResponse.createBySuccess("退出成功");
        }
        return ServerResponse.createByErrorMessage("退出失败");
    }

    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(User user){
        return this.iUserService.register(user);
    }

    @RequestMapping(value = "/check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.validQuestion(username,question,answer);
    }

    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse noLoginUpdatePasswor(String username,String passwordNew,String tokenKey){
        return this.iUserService.no_loginUpdatePassword(username,passwordNew,tokenKey);
    }

    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse resetPassword(String username,String password,String passwordNew){
        return this.iUserService.resetPassword(username, password, passwordNew);
    }

    @RequestMapping(value = "/get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user =(User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            return this.iUserService.getUserInfo(user.getId());
        }
        return ServerResponse.createByErrorMessage("用户未登录,获取用户信息失败");
    }

    @RequestMapping(value = "/update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updateInformation(User user){
        return this.iUserService.updateInformation(user);
    }
}
