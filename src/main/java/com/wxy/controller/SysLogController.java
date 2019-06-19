package com.wxy.controller;

import com.wxy.beans.PageBean;
import com.wxy.beans.PageQuery;
import com.wxy.entity.SysLogWithBLOBs;
import com.wxy.param.SysLogParam;
import com.wxy.service.SysLogService;
import com.wxy.util.BeanValidator;
import com.wxy.util.DateConvert;
import com.wxy.util.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("sys/log")
public class SysLogController {

    @Resource
    SysLogService service;

    /**
     * 进入权限更新记录页面
     * @return
     */
    @RequestMapping("log.page")
    public String logPage(){
        return "log";
    }

    /**
     * 分页显示权限更新记录
     * @return
     */
    @ResponseBody
    @RequestMapping("page.json")
    public JsonData pageJson(SysLogParam param){
        BeanValidator.check(param);
        PageQuery query = PageQuery.builder().
                pageSize(param.getPageSize()).
                pageNo(param.getPageNo()).
                type(param.getType()).
                afterSeg(param.getAfterSeg()).
                beforeSeg(param.getBeforeSeg()).
                operator(param.getOperator()).build();
        if(param.getOperator()==""){
            query.setOperator(null);
        }
        if(param.getAfterSeg()==""){
            query.setAfterSeg(null);
        }
        if(param.getBeforeSeg()==""){
            query.setBeforeSeg(null);
        }
        if(param.getFromTime()!="" && param.getFromTime()!=null){
            query.setFromTime(DateConvert.toDate(param.getFromTime()));
        }
        if(param.getToTime()!="" && param.getToTime()!=null){
            query.setToTime(DateConvert.toDate(param.getToTime()));
        }
        PageBean<SysLogWithBLOBs> page = service.getPage(query);

        return JsonData.success(page);
    }

    /**
     * 恢复操作
     * @return
     */
    @ResponseBody
    @RequestMapping("recover.json")
    public JsonData recover(Integer id){
        service.recover(id);
        return JsonData.success();
    }
}
