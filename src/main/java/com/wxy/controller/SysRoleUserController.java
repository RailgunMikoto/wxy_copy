package com.wxy.controller;

import com.wxy.dto.SysRoleUserDto;
import com.wxy.service.SysRoleUserService;
import com.wxy.util.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/sys/role")
public class SysRoleUserController {

    @Resource
    SysRoleUserService service;

    /**
     * 当前角色下的用户列表
     * @param roleId
     * @return
     */
    @ResponseBody
    @RequestMapping("/users.json")
    public JsonData roleUserList(Integer roleId){
        // 获取用户角色列表
        SysRoleUserDto roleUserList = service.getRoleUserList(roleId);
        return JsonData.success(roleUserList);
    }

    /**
     * 修改用户权限
     * @param roleId
     * @return
     */
    @ResponseBody
    @RequestMapping("/changeUsers.json")
    public JsonData changeUsers(Integer roleId, @RequestParam("userIds") List<Integer> userIds){
        if (service.changeUsers(roleId, userIds)>0){
            return JsonData.success();
        }
        return JsonData.fail("修改失败");
    }
}
