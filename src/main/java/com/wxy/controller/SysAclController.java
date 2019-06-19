package com.wxy.controller;

import com.wxy.beans.PageBean;
import com.wxy.beans.PageQuery;
import com.wxy.entity.SysAcl;
import com.wxy.param.PageQueryAclParam;
import com.wxy.param.SysAclParam;
import com.wxy.service.SysAclService;
import com.wxy.util.BeanValidator;
import com.wxy.util.IpUtil;
import com.wxy.util.JsonData;
import com.wxy.util.RequestHold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/sys/acl")
public class SysAclController {

    @Resource
    SysAclService service;

    /**
     * 分页显示数据
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("page.json")
    public JsonData page(PageQueryAclParam param){
        BeanValidator.check(param);
        PageBean<SysAcl> page = service.findAclPage(param);
        return JsonData.success(page);
    }

    /**
     * 新增权限点
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("save.json")
    public JsonData insert(SysAclParam param){
        BeanValidator.check(param);
        int result = service.insertAcl(param);
        if(result>0){
            return JsonData.success();
        }
        return JsonData.fail("添加失败");
    }

    @ResponseBody
    @RequestMapping("update.json")
    public JsonData updateAcl(SysAclParam param){
        BeanValidator.check(param);
        //TODO:权限点code属性没有添加
        if(service.updateAcl(param)>0){
            return JsonData.success();
        }else{
            return JsonData.fail("添加失败");
        }
    }

    /**
     * 删除权限点
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("delete.json")
    public JsonData deleteAcl(SysAclParam param){
        if(service.deleteAcl(param)>0){
            return JsonData.success();
        }
        return JsonData.fail("删除失败");
    }

    @RequestMapping("/noAuth.page")
    public String noAuth(){
        return "noAuth";
    }
}
