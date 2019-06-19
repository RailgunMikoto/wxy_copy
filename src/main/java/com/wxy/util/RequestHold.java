package com.wxy.util;

import com.wxy.entity.SysUser;

import javax.servlet.http.HttpServletRequest;

public class RequestHold {

    private static final ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<SysUser> userThreadLocal = new ThreadLocal<>();

    public static void add(HttpServletRequest request){
        requestThreadLocal.set(request);
        System.out.println("添加request成功");
    }

    public static void add(SysUser user){
        userThreadLocal.set(user);
        System.out.println("添加user成功");
    }

    public static HttpServletRequest getRequest(){
        return requestThreadLocal.get();
    }

    public static SysUser getUser(){
        return userThreadLocal.get();
    }

    public static void remove(){
        requestThreadLocal.remove();
        userThreadLocal.remove();
        System.out.println("RequestHold解绑成功");
    }
}
