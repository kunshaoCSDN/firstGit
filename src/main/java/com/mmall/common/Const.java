package com.mmall.common;

public class Const {
    public final static String CURRENT_USER = "currentUser";

    public final static String EMAIL = "email";
    public final static String USERNAME = "username";
    public final static String TOKEN_PRE = "token_";

    /* 使用内部接口对常量进行分类 */
    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;//管理员
    }
}
