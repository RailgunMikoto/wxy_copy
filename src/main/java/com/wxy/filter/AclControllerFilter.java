package com.wxy.filter;

import com.wxy.service.SysAclService;
import com.wxy.util.ApplicationContextHelper;
import com.wxy.util.JsonData;
import com.wxy.util.JsonMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AclControllerFilter implements Filter {
    // 全局静态无权限路径
    public static final String noAuthUrl = "/sys/acl/noAuth.page";

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        // 获取uri
        String uri = request.getRequestURI();
        if(uri.contains("login") || uri.contains(noAuthUrl)){
            chain.doFilter(req, resp);
            return;
        }
        // 判断用户有无权限
        SysAclService aclService = ApplicationContextHelper.popBean(SysAclService.class);
        if(aclService.hasAcl(uri)){
            // 用户有权限
            chain.doFilter(req, resp);
            return;
        }else {
            noAuth(uri, request, response);
            return;
        }
    }

    private void noAuth(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(uri.endsWith(".json")){
            response.setHeader("Content-Type", "application/json");
            JsonData fail = JsonData.fail("no auth");
            response.getWriter().print(JsonMapper.obj2String(fail));
        }else {
            response.setHeader("Content-Type", "text/html");
            response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                    + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                    + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                    + "window.location.href='" + noAuthUrl + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
        }
    }


    public void init(FilterConfig config) throws ServletException {

    }

}
