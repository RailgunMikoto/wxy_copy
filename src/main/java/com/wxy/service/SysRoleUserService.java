package com.wxy.service;

import com.wxy.dto.SysRoleUserDto;

import java.util.List;

public interface SysRoleUserService {

    // 获取角色用户列表
    SysRoleUserDto getRoleUserList(Integer roleId);

    // 获取当前拥有当前角色权限的用户Id
    List<Integer> getUserIds(Integer roleId);

    // 修改角色用户
    int changeUsers(Integer roleId, List<Integer> userIds);
}
