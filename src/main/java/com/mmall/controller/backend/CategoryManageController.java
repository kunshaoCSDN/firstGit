package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/Manage/Category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 商品类型的添加
     * @param session
     * @param parentId
     * @param categoryName
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse <String> addCategory(HttpSession session, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId, String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            ServerResponse<String> valid = this.iUserService.checkAdminRole(user);
            if(!valid.isSuccess()){
                return ServerResponse.createByErrorMessage("无操作权限，需管理员身份登录");
            }
            //进行操作
            return this.iCategoryService.addCategory(parentId, categoryName);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
    }

    /**
     * 商品类型名称的修改
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse<String> updateCategoryName(HttpSession session,Integer categoryId, String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            ServerResponse<String> valid = this.iUserService.checkAdminRole(user);
            if(!valid.isSuccess()){
                return ServerResponse.createByErrorMessage("无操作权限，需管理员身份登录");
            }
            //进行操作

            return this.iCategoryService.updateCategoryName(categoryId,categoryName);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
    }

    /**
     * 商品类型获取所有的平级子类型，就找一层
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            ServerResponse<String> valid = this.iUserService.checkAdminRole(user);
            if(!valid.isSuccess()){
                return ServerResponse.createByErrorMessage("无操作权限，需管理员身份登录");
            }
            //进行操作
            return this.iCategoryService.getChildrenParallelCategory(categoryId);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
    }

    /**
     * 商品类型获取所有的子类型
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getChildrenDeepCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            ServerResponse<String> valid = this.iUserService.checkAdminRole(user);
            if(!valid.isSuccess()){
                return ServerResponse.createByErrorMessage("无操作权限，需管理员身份登录");
            }
            //进行操作
            return this.iCategoryService.getChildrenDeepCategory(categoryId);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
    }
}