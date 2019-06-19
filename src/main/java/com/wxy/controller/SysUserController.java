package com.wxy.controller;

import com.wxy.beans.PageBean;
import com.wxy.beans.PageQuery;
import com.wxy.entity.SysUser;
import com.wxy.exception.ParamException;
import com.wxy.param.PageQueryParam;
import com.wxy.param.SysUserParam;
import com.wxy.service.SysUserService;
import com.wxy.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    SysUserService userService;

    /**
     * 进入用户管理界面
     * @return
     */
    @RequestMapping("user.page")
    public String userpage(){
        return "dept";
    }

    /**
     * 新增用户
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysUserParam param){
        // 检查参数
        BeanValidator.check(param);
        if(userService.insertUser(param)>0){
            return JsonData.success();
        }
        return JsonData.fail("添加用户失败");
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping("/login.page")
    public String login(String username, String password, HttpSession session){
        SysUser user = userService.userLogin(username, MD5Util.encrypt(password));
        if(user != null){
            session.setAttribute("user", user);
            return "admin";
        }
        throw new ParamException("用户登录异常");
    }

    /**
     * 用户退出
     * @param session
     * @return
     */
    @RequestMapping("logout.page")
    public String logoutUser(HttpSession session){
        session.removeAttribute("user");
        return "redirect:/signin.jsp";
    }

    /**
     * 更新修改用户
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("update.json")
    public JsonData updateUser(SysUserParam param){
        // 检查参数
        BeanValidator.check(param);
        if(userService.updateUser(param)>0){
            return JsonData.success();
        }
        return JsonData.fail("修改用户失败");
    }

    /**
     * 分页显示用户
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("/page.json")
    public JsonData queryUserByDept(PageQueryParam param){
        // 检查参数
        BeanValidator.check(param);
        PageBean<SysUser> pageBean = new PageBean<>();
        pageBean = userService.getPageBean(param, pageBean);
        return JsonData.success(pageBean);
    }
}
