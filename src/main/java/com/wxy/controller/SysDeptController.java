package com.wxy.controller;

import com.wxy.dto.SysDeptDto;
import com.wxy.entity.SysDept;
import com.wxy.exception.ParamException;
import com.wxy.param.SysDeptParam;
import com.wxy.service.SysDeptService;
import com.wxy.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("sys/dept")
public class SysDeptController {

    @Resource
    SysDeptService deptService;

    @RequestMapping("dept.page")
    public String dept(){
        return "dept";
    }

    /**
     * 添加一个部门
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("save.json")
    public JsonData save(SysDeptParam param){
        // 检查参数
        BeanValidator.check(param);
        if(deptService.insert(param)>0){
            return JsonData.success();
        }
        return JsonData.fail("添加失败");
    }

    /**
     * 部门树
     * @return
     */
    @ResponseBody
    @RequestMapping("tree.json")
    public JsonData tree(){
        List<SysDeptDto> deptTree = deptService.createDeptTree();
        return JsonData.success(deptTree);
    }

    /**
     * 更新部门
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("update.json")
    public JsonData update(SysDeptParam param){
        // 参数验证
        BeanValidator.check(param);
        if(deptService.updateDeptById(param)>0){
            return JsonData.success();
        }else {
            return JsonData.fail("更新失败");
        }
    }

    /**
     * 删除部门
     * @param dept
     * @return
     */
    @ResponseBody
    @RequestMapping("delete.json")
    public JsonData delete(SysDept dept){
        deptService.deleteDeptById(dept.getId());
        return JsonData.success();
    }
}
