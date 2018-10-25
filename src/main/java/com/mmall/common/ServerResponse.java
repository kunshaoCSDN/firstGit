package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 用来返回客户端结果类型
 */
//因为有可能有的数据data是null，所以序列化的时候没有必要进行序列化进去
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
 public class ServerResponse<T> {
     private int status;
     private String msg;
     private T data;

     private ServerResponse(int status){
         this.status = status;
     }
     private ServerResponse(int status, String msg){
         this.status = status;
         this.msg=msg;
     }
     private ServerResponse(int status, String msg, T data){
         this.status = status;
         this.msg = msg;
         this.data = data;
     }
     private ServerResponse(int status, T data){
         this.status = status;
         this.data = data;
     }

    //使之不在json序列化结果当中
     @JsonIgnore
     public boolean isSuccess(){
         return this.status == ResponseCode.SUCCESS.getCode();
     }

     public int getStatus(){
         return this.status;
     }

     public String getMsg(){
         return this.msg;
     }

     public T getData(){
         return this.data;
     }

     /* 错误结果 */
     public static <T> ServerResponse<T> createByError(){
         return new ServerResponse(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
     }

    public static <T> ServerResponse<T> createByErrorMessage(String msg){
        return new ServerResponse(ResponseCode.ERROR.getCode(),msg);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String msg){
        return new ServerResponse(errorCode,msg);
    }

    /* 正确结果 */
    public static <T> ServerResponse<T> createBySuccess(){
         return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccess(String msg){
         return new ServerResponse<>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(),msg,data);
    }



}
