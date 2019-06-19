package com.wxy.service;

import com.wxy.beans.PageBean;
import com.wxy.entity.SysAcl;
import com.wxy.param.PageQueryAclParam;
import com.wxy.param.SysAclParam;

public interface SysAclService {
    // 查询分页显示的数据
    PageBean<SysAcl> findAclPage(PageQueryAclParam param);

    // 添加一个权限点
    int insertAcl(SysAclParam param);

    // 修改权限点
    int updateAcl(SysAclParam param);

    // 删除权限点
    int deleteAcl(SysAclParam param);

    // 判断用户有无权限
    boolean hasAcl(String url);
}
