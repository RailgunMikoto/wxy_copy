package com.wxy.filter;

import com.wxy.entity.SysUser;
import com.wxy.util.RequestHold;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String uri = request.getRequestURI();
        System.out.println("uri = "+uri);
        if(uri.contains("login")){
            chain.doFilter(req, resp);
            return;
        }
        SysUser user = (SysUser) request.getSession().getAttribute("user");
        if(user == null){
            response.sendRedirect("signin.jsp");
            return;
        }
        System.out.println("用户已登录");
        // 将用户和request添加到ThreadLocal中
        RequestHold.add(user);
        RequestHold.add(request);
        chain.doFilter(req, resp);
        return;
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
