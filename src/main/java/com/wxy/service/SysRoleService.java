package com.wxy.service;

import com.wxy.entity.SysRole;
import com.wxy.param.SysRoleParam;

import java.util.List;

public interface SysRoleService {
    // 添加一个角色
     int insertRole(SysRoleParam param);

     // 修改角色
    int updateRole(SysRoleParam param);

    // 角色列表
    List<SysRole> roleList();
}
