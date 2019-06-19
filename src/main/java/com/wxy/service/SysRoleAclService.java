package com.wxy.service;

import com.wxy.dto.SysAclModuleDto;

import java.util.List;

public interface SysRoleAclService {
    // 获取角色权限树
    List<SysAclModuleDto> createRoleAclTree(Integer roleId);

    // 修改角色权限
    int changeAcls(Integer roleId, List<Integer> aclIds);

    // 获取当前角色拥有的权限id
    List<Integer> getRoleAclIds(Integer roleId);
}
