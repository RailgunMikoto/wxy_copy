package com.wxy.service;

import com.wxy.beans.PageBean;
import com.wxy.beans.PageQuery;
import com.wxy.entity.*;

import java.util.List;

public interface SysLogService {

    // 获取分页显示数据
    PageBean<SysLogWithBLOBs> getPage(PageQuery query);

    // 保存操作日志
    void saveDeptLog(SysDept before, SysDept after);
    void saveUserLog(SysUser before, SysUser after);
    void saveAclModuleLog(SysAclModule before, SysAclModule after);
    void saveAclLog(SysAcl before, SysAcl after);
    void saveRoleLog(SysRole before, SysRole after);

    // 恢复操作
    void recover(Integer id);
}
