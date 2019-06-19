package com.wxy.controller;

import com.wxy.entity.SysRole;
import com.wxy.param.SysRoleParam;
import com.wxy.service.SysRoleService;
import com.wxy.util.BeanValidator;
import com.wxy.util.IpUtil;
import com.wxy.util.JsonData;
import com.wxy.util.RequestHold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;

@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    SysRoleService service;

    @RequestMapping("role.page")
    public String rolePage(){
        return "role";
    }

    /**
     * 添加角色
     */
    @ResponseBody
    @RequestMapping("save.json")
    public JsonData insertRole(SysRoleParam param){
        BeanValidator.check(param);
        if(service.insertRole(param)>0){
            return JsonData.success();
        }
        return JsonData.fail("角色已存在");

    }

    /**
     * 修改角色
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("update.json")
    public JsonData updateRole(SysRoleParam param){
        BeanValidator.check(param);

        if(service.updateRole(param)>0){
            return JsonData.success();
        }
        return JsonData.fail("修改失败");
    }

    /**
     * 角色列表
     */
    @ResponseBody
    @RequestMapping("list.json")
    public JsonData roleList(){
        return JsonData.success(service.roleList());
    }

    //TODO:删除角色，可能需要删除此角色对应的权限和用户
    /**
     * 删除角色
     */
    @ResponseBody
    @RequestMapping("delete.json")
    public JsonData deleteRole(Integer roleId){
        System.out.println("roleId = ================="+roleId);

        return JsonData.success();
    }
}
