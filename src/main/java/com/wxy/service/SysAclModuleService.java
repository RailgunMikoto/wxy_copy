package com.wxy.service;

import com.wxy.dto.SysAclModuleDto;
import com.wxy.entity.SysAclModule;
import com.wxy.param.SysAclModuleParam;

import java.util.List;

public interface SysAclModuleService {
    // 获取权限模块树
    List<SysAclModuleDto> getAclModuleTree();

    // 根据id查找权限模块
    SysAclModule findAclModuleById(Integer id);

    // 添加一个权限模块
    int insertAclModule(SysAclModuleParam param);

    // 修改权限模块
    int updateAclModule(SysAclModuleParam param);

    // 递归更新下级权限模块的level
    void recAclModuleLevel(List<SysAclModule> childAclModule, String parentLevel);

    // 删除权限模块
    int deleteAclModule(SysAclModule aclModule);
}
