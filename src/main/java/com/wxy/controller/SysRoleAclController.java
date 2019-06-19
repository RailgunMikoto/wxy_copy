package com.wxy.controller;

import com.wxy.dto.SysAclModuleDto;
import com.wxy.service.SysRoleAclService;
import com.wxy.util.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sys/role")
public class SysRoleAclController {

    @Resource
    private SysRoleAclService service;

    /**
     * 获取角色权限树
     * @param roleId
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/roleTree.json")
    public JsonData roleTree(Integer roleId, HttpSession session){
        // 获取角色权限树
        List<SysAclModuleDto> roleAclTree = service.createRoleAclTree(roleId);
        // 获取当前角色拥有的权限点id集合
        List<Integer> roleAclIds = service.getRoleAclIds(roleId);
        session.setAttribute("aclIds", roleAclIds);

        return JsonData.success(roleAclTree);
    }

    /**
     * 修改角色权限关系
     * @param roleId
     * @param s
     * @return
     */
    @ResponseBody
    @RequestMapping("changeAcls.json")
    public JsonData changeAcls(Integer roleId, @RequestParam("aclIds") String s){
        // 分割数据
        String[] temp = s.split(",");
        // 处理数据
        // 存储需要修改的权限点id
        List<Integer> aclIds = new ArrayList<>();
        for (String s1 : temp) {
            Integer aclId = Integer.parseInt(s1);
            aclIds.add(aclId);
        }
        if(service.changeAcls(roleId, aclIds)>0){
            return JsonData.success();
        }
        return JsonData.fail("修改失败");
    }
}
