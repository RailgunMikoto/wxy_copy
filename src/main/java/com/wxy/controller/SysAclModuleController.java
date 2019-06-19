package com.wxy.controller;

import com.wxy.dto.SysAclModuleDto;
import com.wxy.entity.SysAclModule;
import com.wxy.param.SysAclModuleParam;
import com.wxy.service.SysAclModuleService;
import com.wxy.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/sys/aclModule")
public class SysAclModuleController {

    @Resource
    SysAclModuleService service;

    /**
     * 进入权限管理页面
     * @return
     */
    @RequestMapping("/acl.page")
    public String aclPage(){
        return "acl";
    }

    /**
     * 权限模块树
     * @return
     */
    @ResponseBody
    @RequestMapping("tree.json")
    public JsonData aclTree(){
        List<SysAclModuleDto> aclModuleTree = service.getAclModuleTree();

        return JsonData.success(aclModuleTree);
    }

    /**
     * 新增权限模块
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("save.json")
    public JsonData save(SysAclModuleParam param){
        // 检查参数
        BeanValidator.check(param);
        if(service.insertAclModule(param)>0){
            return JsonData.success();
        }
        return JsonData.fail("添加失败");
    }

    /**
     * 修改权限模块
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("update.json")
    public JsonData update(SysAclModuleParam param){
        BeanValidator.check(param);
        if(service.updateAclModule(param)>0){
            return JsonData.success();
        }
        return JsonData.fail("更新失败");
    }

    /**
     * 删除权限模块
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("delete.json")
    public JsonData deleteAclModule(SysAclModuleParam param){
        SysAclModule aclModule = SysAclModule.builder().id(param.getId()).build();
        if(service.deleteAclModule(aclModule)>0){
            return JsonData.success();
        }else{
            return JsonData.fail("删除失败");
        }
    }
}
